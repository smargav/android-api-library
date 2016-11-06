package com.smargav.api.remoteimage;

import android.os.Build;
import android.os.Debug;
import android.util.Log;

/**
 Keep track of how much memory is available on the VM and native heaps
 respectively.

 http://stackoverflow.com/questions/1955410/bitmapfactory-oom-driving-me-nuts/5493182#5493182

 In essence; gingerbread and lower put bitmaps on the native heap, not the VM heap.
 In addition to this, the expected methods of getting the native heap size are unreliable,
 such as Debug.getNativeHeapSize() and Debug.getNativeHeapFreeSize().

 So, we a similar solution to the link above, which estimates the native heap
 size using the VM heap size.

 Then, we only load an image if we have at least MIN_HEAP_REQUIREMENT of heap left
 because we have no idea how big our images will be. Worst-case, we have to catch
 an OOM exception. Unlikely though, if you pick a sane MIN_HEAP_REQUIREMENT.

 @author Tom Dignan
 */
public final class MemoryHelper
{
    private static final String TAG = "MemoryHelper";

    // calibration still required.
    private static final int MAGIC_VM_HEAP_FREE_SIZE = 5000;

    // calibrated by manual testing on droid3 with monkey
    private static final int MAGIC_NATIVE_HEAP_CONSTANT = 200;

    private MemoryHelper()
    {
        throw new UnsupportedOperationException("Do not instantiate MemoryHelper");
    }

    /**
     * Returns true if there is enough memory to download/read/decompress a bitmap.
     * Uses a heuristic. It is assumed MIN_HEAP_REQUIREMENT is enough.
     *
     * @return boolean
     */
    public static boolean isEnoughMemoryLeft()
    {
        return _isEnoughMemoryLeft();
    }

    public static boolean _isEnoughMemoryLeft()
    {
        long maxVmHeap = Runtime.getRuntime().totalMemory();
        long minNativeFree = maxVmHeap / MAGIC_NATIVE_HEAP_CONSTANT; // it's an estimate.
        long freeMemory = Runtime.getRuntime().freeMemory();

        Log.d(TAG, String.format("maxVmHeap=%d minNativeFree=%d totalFree=%d",
                maxVmHeap, minNativeFree, freeMemory));

        // whether we're using the native or VM heap for bitmaps
        boolean isNativeHeap = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;

        if (isNativeHeap)
        {
            long allocatedSize = Debug.getNativeHeapAllocatedSize();
            long freeSize = Debug.getNativeHeapFreeSize();

            String msg = String.format("Native heap: max=%d allocated=%d free=%d",
                minNativeFree, allocatedSize, freeSize);

            Log.d(TAG, msg);

            return freeSize > minNativeFree;
        }
        else
        {
            long freeSize = Runtime.getRuntime().freeMemory();

            String msg = String.format("VM heap: max=%d free=%d", maxVmHeap,
                                       freeSize);
            Log.w(TAG, msg);
            return freeSize >= MAGIC_VM_HEAP_FREE_SIZE;
        }
    }
}
