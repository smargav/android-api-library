package com.smargav.api.remoteimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class RemoteImageLoaderJob implements Runnable
{
    private static final String TAG = "Ignition/ImageLoader";

    private static final int MAX_OOM_RETRY_INTERVAL = 1000; //ms
    private static final int MIN_OOM_RETRY_INTERVAL = 250;

    private static final int MAX_RETRIES = 10;

    private static final int DEFAULT_RETRY_HANDLER_SLEEP_TIME = 1000;

    private String imageUrl;
    private RemoteImageLoaderHandler handler;
    private ImageCache imageCache;
    private int numRetries, defaultBufferSize;

    private Random generator = new Random();


    public RemoteImageLoaderJob(String imageUrl, RemoteImageLoaderHandler handler, ImageCache imageCache,
                                int numRetries, int defaultBufferSize)
    {
        this.generator.setSeed(System.currentTimeMillis());
        this.imageUrl = imageUrl;
        this.handler = handler;
        this.imageCache = imageCache;
        this.numRetries = numRetries;
        this.defaultBufferSize = defaultBufferSize;
    }


    /**
     * The job method run on a worker thread. It will first query the image cache, and on a miss,
     * download the image from the Web.
     */
    @Override
    public void run()
    {
        Bitmap bitmap = null;
        boolean oomExceptionNotRaised = false;

        while (!oomExceptionNotRaised)
        {
            oomExceptionNotRaised = true;
            int retries = MAX_RETRIES;

            // If using the native heap, loop and sleep until we have enough memory,
            // or give up after MAX_RETRIES of waiting. NOTE: These 'retries'
            // are really just us polling to see if the garbage collector has done
            // its job, and if it hasn't we end this task. Otherwise, we break and
            // try another image download. This is separate from the above while
            // loop, which is for re-trying the entire task after an OOM occurs.
            while (retries-- > 0)
            {

                if (!MemoryHelper.isEnoughMemoryLeft())
                {
                    //System.gc();
                    Log.w(TAG, "out of memory... waiting!");
                    try
                    {
                        // Sleep some random time between MIN and MAX
                        // so that all the threads don't simultaneously wake up
                        // and OOM together.
                        int sleepTime = this.generator.nextInt(MAX_OOM_RETRY_INTERVAL -
                            MIN_OOM_RETRY_INTERVAL);
                        Log.d(TAG,
                            String.format("sleepTime=%d Thread=%d", sleepTime,
                                Thread.currentThread().getId()));
                        Thread.sleep(sleepTime);
                    }
                    catch (InterruptedException e)
                    {
                        Log.w(TAG, "OOM retry loop interrupted, giving up.");
                        return;
                    }
                }
                else
                {
                    Log.w(TAG, "plenty of memory!");
                    // don't give up if this is our last try and we just got the memory
                    // needed!
                    retries = 1;
                    break;  // we should have enough memory!
                }
            }

            if (retries == 0)
            {
                // we tried and tried, but there's probably not enough memory.
                Log.w(TAG, "OOM retry loop exceeded MAX_RETRIES. Giving up.");
                return;
            }

            if(imageCache != null)
            {
                // at this point we know the image is not in memory, but it could be cached to storage.
                try
                {
                    bitmap = imageCache.getBitmap(imageUrl);
                }
                catch (OutOfMemoryError e)
                {
                    String message =
                        String.format("Caught OOM trying to load: %s. Will retry.", imageUrl);

                    //System.gc();
                    // Force sleep here since we have no idea whether our heuristic will
                    // really work to trigger the sleep above:
                    SystemClock.sleep(DEFAULT_RETRY_HANDLER_SLEEP_TIME);

                    Log.w(TAG, message);
                    retries = MAX_RETRIES;
                    oomExceptionNotRaised = false;
                }
            }

            try
            {
                // If bitmap is null because of a previous OOM, don't download it!
                if(oomExceptionNotRaised && bitmap == null)
                {
                    bitmap = downloadImage();
                }
            }
            catch (OutOfMemoryError e)
            {
                String message =
                    String.format("Caught OOM trying to download: %s. Will retry.", imageUrl);

                //System.gc();
                SystemClock.sleep(DEFAULT_RETRY_HANDLER_SLEEP_TIME);

                retries = MAX_RETRIES;
                oomExceptionNotRaised = false;
            }
        }

        notifyImageLoaded(imageUrl, bitmap);
    }

    // TODO: we could probably improve performance by re-using connections instead of closing them
    // after each and every download
    protected Bitmap downloadImage()
    {
        int timesTried = 1;

        while(MemoryHelper.isEnoughMemoryLeft() && timesTried <= numRetries)
        {
            try
            {
                byte[] imageData = retrieveImageData();

                if(imageData == null)
                {
                    break;
                }

                if(imageCache != null)
                {
                    imageCache.put(imageUrl, imageData);
                }

                return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

            }
            catch(Throwable e)
            {
                Log.w(TAG, "download for " + imageUrl + " failed (attempt " + timesTried + ")");
                e.printStackTrace();
                SystemClock.sleep(DEFAULT_RETRY_HANDLER_SLEEP_TIME);
                timesTried++;
            }
        }

        return null;
    }

    protected byte[] retrieveImageData() throws IOException
    {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // determine the image size and allocate a buffer
        int fileSize = connection.getContentLength();
        Log.d(TAG, "fetching image " + imageUrl + " (" + (fileSize <= 0 ? "size unknown" : Integer.toString(fileSize)) + ")");

        BufferedInputStream istream = new BufferedInputStream(connection.getInputStream());

        try
        {
            if(fileSize <= 0)
            {
                Log.w(TAG,
                  "Server did not set a Content-Length header, will default to buffer size of "
                  + defaultBufferSize + " bytes");

                ByteArrayOutputStream buf = new ByteArrayOutputStream(defaultBufferSize);
                byte[] buffer = new byte[defaultBufferSize];
                int bytesRead = 0;
                while(bytesRead != -1)
                {
                    bytesRead = istream.read(buffer, 0, defaultBufferSize);
                    if(bytesRead > 0)
                        buf.write(buffer, 0, bytesRead);
                }
                return buf.toByteArray();
            }
            else
            {
                byte[] imageData = new byte[fileSize];

                int bytesRead = 0;
                int offset = 0;
                while(bytesRead != -1 && offset < fileSize)
                {
                    bytesRead = istream.read(imageData, offset, fileSize - offset);
                    offset += bytesRead;
                }
                return imageData;
            }
        }
        finally
        {
            // clean up
            try
            {
                istream.close();
                connection.disconnect();
            }
            catch(Exception ignore) { }
        }
    }

    protected void notifyImageLoaded(String url, Bitmap bitmap)
    {
        Message message = new Message();
        message.what = RemoteImageLoaderHandler.HANDLER_MESSAGE_ID;
        Bundle data = new Bundle();
        data.putString(RemoteImageLoaderHandler.IMAGE_URL_EXTRA, url);
        Bitmap image = bitmap;
        data.putParcelable(RemoteImageLoaderHandler.BITMAP_EXTRA, image);
        message.setData(data);

        handler.sendMessage(message);
    }
}
