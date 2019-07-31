package com.smargav.api.net;

import com.smargav.api.execptions.NetIOException;
import com.smargav.api.logger.AppLogger;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.Util;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 *
 */

public class WebSession {
    
    public interface FileDownloadListener {
        void downloadProgress(long total);
    }
    
    private static final MediaType JSON = MediaType
                                                  .parse("application/json; charset=utf-8");
    public static int NET_TIMEOUT = 30;
    
    private int connectionTimeout;
    private int readTimeout;
    private OkHttpClient client;
    
    private Response lastCallResponse;
    private Request lastCallRequest;
    
    public WebSession() {
        this(NET_TIMEOUT);
    }
    
    public WebSession(int timeOutInSec) {
        this(timeOutInSec, timeOutInSec);
    }
    
    public WebSession(int connectionTimeout, int readTimeout) {
        client = new OkHttpClient();
        client.interceptors().add(new LoggingInterceptor());
        this.readTimeout = readTimeout;
        this.connectionTimeout = connectionTimeout;
        client.setConnectTimeout(connectionTimeout, TimeUnit.SECONDS);
        client.setReadTimeout(readTimeout, TimeUnit.SECONDS);
    }
    
    public WebSession(OkHttpClient okHttpClient) {
        this(NET_TIMEOUT);
        client = okHttpClient;
    }
    
    public void set2gTimeOut() {
        readTimeout = NET_TIMEOUT + NET_TIMEOUT / 2;
        connectionTimeout = NET_TIMEOUT + NET_TIMEOUT / 2;
        
        client.setConnectTimeout(connectionTimeout, TimeUnit.SECONDS);
        client.setReadTimeout(readTimeout, TimeUnit.SECONDS);
    }
    
    public void setRegularTimeOut() {
        readTimeout = NET_TIMEOUT;
        connectionTimeout = NET_TIMEOUT;
        client.setConnectTimeout(connectionTimeout, TimeUnit.SECONDS);
        client.setReadTimeout(readTimeout, TimeUnit.SECONDS);
    }
    
    /**
     * new AppAuthenticator(username, password)
     *
     * @param url
     * @param json
     * @param authenticator
     * @return
     * @throws IOException
     */
    public String postGzip(String url, String json, Authenticator authenticator) throws IOException {
        Builder builder = new Builder();
        builder.header("Content-Encoding", "gzip");
        
        // Compress data.
        byte[] data = compressData(json);
        
        RequestBody body = RequestBody.create(JSON, data);
        builder.url(url).post(body);
        Request request = builder.build();
        if (authenticator != null) {
            client.setAuthenticator(authenticator);
        }
        
        lastCallRequest = request;
        Response response = client.newCall(request).execute();
        lastCallResponse = response;
        verifyResponseCode(response);
        String resString = response.body().string();
        validateResponse(resString);
        return resString;
    }
    
    public String postGzip(String url, String json) throws IOException {
        return postGzip(url, json, null);
    }
    
    public String post(String url, String json) throws IOException {
        return post(url, json, null);
    }
    
    public String get(String url, Map<String, String> params) throws IOException {
        return get(url, params, null);
    }
    
    public String get(Request request, Authenticator authenticator) throws IOException {
        
        if (authenticator != null) {
            client.setAuthenticator(authenticator);
        }
        
        lastCallRequest = request;
        Response response = client.newCall(request).execute();
        lastCallResponse = response;
        
        verifyResponseCode(response);
        String resString = response.body().string();
        validateResponse(resString);
        return resString;
    }
    
    public String get(String url, Map<String, String> params, Authenticator authenticator, Map<String, String> headers) throws IOException {
        String finalUrl = buildURI(url, params);
        Request.Builder builder = new Builder();
        for (String key : headers.keySet()) {
            builder.addHeader(key, headers.get(key));
        }
        
        Request request = builder.url(finalUrl).build();
        return get(request, authenticator);
        
    }
    
    public String get(String url, Map<String, String> params, Authenticator authenticator) throws IOException {
        String finalUrl = buildURI(url, params);
        Request request = new Builder().url(finalUrl).build();
        return get(request, authenticator);
    }
    
    public String buildURI(String baseUrl, Map<String, String> params)
            throws IOException {
        if (params != null && params.size() > 0) {
            StringBuilder builder = new StringBuilder(baseUrl + "?");
            builder.append(parseMap(params));
            baseUrl = builder.toString();
        }
        
        return baseUrl;
    }
    
    
    public String parseMap(Map<String, String> params) throws IOException {
        if (params == null && params.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String key : params.keySet()) {
            String parameterName = URLEncoder.encode(key, "UTF-8");
            
            String parameterValue = params.get(key);
            
            if (StringUtils.isNotBlank(parameterValue)) {
                parameterValue = URLEncoder.encode(parameterValue,
                        "UTF-8");
            }
            builder.append(parameterName).append("=")
                    .append(parameterValue).append("&");
        }
        
        // remove last '&'
        builder.deleteCharAt(builder.length() - 1);
        
        
        return builder.toString();
        
    }
    
    public String post(Request request, Authenticator authenticator) throws IOException {
        
        if (authenticator != null) {
            client.setAuthenticator(authenticator);
        }
        
        lastCallRequest = request;
        Response response = client.newCall(request).execute();
        lastCallResponse = response;
        
        String resString = response.body().string();
        validateResponse(resString);
        
        return resString;
    }
    
    public String post(String url, String data, Authenticator authenticator, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(JSON, data);
        Request.Builder builder = new Builder();
        for (String key : headers.keySet()) {
            builder.addHeader(key, headers.get(key));
        }
        builder.url(url).post(body);
        Request request = builder.build();
        return post(request, authenticator);
    }
    
    public String post(String url, String data, Authenticator authenticator) throws IOException {
        RequestBody body = RequestBody.create(JSON, data);
        Request.Builder builder = new Builder();
        builder.url(url).post(body);
        Request request = builder.build();
        return post(request, authenticator);
    }
    
    public String post(String url, Map<String, String> params, Authenticator authenticator) throws IOException {
        FormEncodingBuilder formBody = new FormEncodingBuilder();
        
        for (String key : params.keySet()) {
            formBody.add(key, params.get(key));
        }
        
        RequestBody body = formBody.build();
        Request.Builder builder = new Builder();
        builder.url(url).post(body);
        Request request = builder.build();
        
        
        return post(request, authenticator);
    }
    
    public String post(String url, Map<String, String> params, Authenticator authenticator, Map<String, String> headers) throws IOException {
        FormEncodingBuilder formBody = new FormEncodingBuilder();
        
        for (String key : params.keySet()) {
            formBody.add(key, params.get(key));
        }
        
        RequestBody body = formBody.build();
        Request.Builder builder = new Builder();
        for (String key : headers.keySet()) {
            builder.addHeader(key, headers.get(key));
        }
        builder.url(url).post(body);
        Request request = builder.build();
        
        
        return post(request, authenticator);
    }
    
    public String put(String url, Map<String, String> params, Authenticator authenticator, Map<String, String> headers) throws IOException {
        FormEncodingBuilder formBody = new FormEncodingBuilder();
        
        for (String key : params.keySet()) {
            formBody.add(key, params.get(key));
        }
        
        RequestBody body = formBody.build();
        Request.Builder builder = new Builder();
        for (String key : headers.keySet()) {
            builder.addHeader(key, headers.get(key));
        }
        builder.url(url).put(body);
        Request request = builder.build();
        
        
        return post(request, authenticator);
    }
    
    public Response getLastCallResponse() {
        return lastCallResponse;
    }
    
    public void setLastCallResponse(Response lastCallResponse) {
        this.lastCallResponse = lastCallResponse;
    }
    
    public Request getLastCallRequest() {
        return lastCallRequest;
    }
    
    public void setLastCallRequest(Request lastCallRequest) {
        this.lastCallRequest = lastCallRequest;
    }
    
    private void verifyResponseCode(Response response) throws IOException {
        
        int code = response.code();
        
        if (code == 404) {
            throw new NetIOException(404, "404 - Not found");
        }
        
        if (code == 400) {
            throw new NetIOException(400,
                    "400 - The request sent by the client was syntactically incorrect.");
        }
        
        if (code == 500) {
            AppLogger.e(getClass(), "Server Exception : " + response.body().string());
            throw new NetIOException(500,
                    "500 - Unexpected Server error");
        }
    }
    
    private byte[] compressData(String body) throws IOException {
        byte[] data = body.getBytes("UTF-8");
        ByteArrayOutputStream arr = new ByteArrayOutputStream();
        OutputStream zipper = new GZIPOutputStream(arr);
        zipper.write(data);
        zipper.close();
        
        return arr.toByteArray();
    }
    
    private void validateResponse(String resString) throws IOException {
        if (StringUtils.contains(resString,
                "The request sent by the client was syntactically incorrect.")) {
            throw new IOException(
                    "The request sent by the client was syntactically incorrect.");
        }
        //
        AppLogger.i(getClass(),
                "Response : " + StringUtils.substring(resString, 0, 500));
        
    }
    
    
    public String get(String url) throws IOException {
        Request request = new Builder().url(url).build();
        
        lastCallRequest = request;
        Response response = client.newCall(request).execute();
        lastCallResponse = response;
        
        verifyResponseCode(response);
        String resString = response.body().string();
        validateResponse(resString);
        return resString;
    }
    
    
    public long downloadFile(File folder, String mediaURL, String name, Authenticator authenticator, FileDownloadListener listener) throws IOException {
        
        AppLogger.i(getClass(), "Downloading File - " + mediaURL + " to folder " + folder.getAbsolutePath());
        
        
        if (authenticator != null) {
            client.setAuthenticator(authenticator);
        }
        
        Request request = new Request.Builder().url(mediaURL).build();
        // OkHttpClient client = getUnsafeOkHttpClient();
        Response response = client.newCall(request).execute();
        File downloadedFile = new File(folder, name);
        
        InputStream is = response.body().byteStream();
        OutputStream output = new FileOutputStream(downloadedFile);
        
        BufferedInputStream input = new BufferedInputStream(is);
        
        byte[] data = new byte[2048];
        long total = 0;
        int count = 0;
        int iteration = 0;
        while ((count = input.read(data)) != -1) {
            total += count;
            output.write(data, 0, count);
            
            if (iteration % 100 == 0) {
                if (listener != null) {
                    listener.downloadProgress(total);
                }
            }
        }
        
        output.flush();
        output.close();
        input.close();
        
        listener.downloadProgress(total);
        return total;
        
    }
    
    
    private class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            
            long t1 = System.nanoTime();
            
            long contentLength = 0;
            String content = "";
            if (request.body() != null) {
                contentLength = request.body().contentLength();
                content = bodyToString(request);
            }
            AppLogger.i(getClass(), "Request: " + request.url() + " Content-Length: " + contentLength);
            int end = content.length() - 1;
            if (content != null && content.length() > 200) {
                end = 200;
            }
            AppLogger.i(getClass(), "Params: " + content.substring(0, end));
            
            Response response = chain.proceed(request);
            
            long t2 = System.nanoTime();
            AppLogger.i(getClass(), String.format(
                    "Received response for %s in %.1fms%n\tHTTP Status %d",
                    response.request().url(), (t2 - t1) / 1e6d, response.code()));
            return response;
        }
    }
    
    public static class BasicAuthenticator implements Authenticator {
        
        private String username, password;
        
        public BasicAuthenticator(String u, String p) {
            username = u;
            password = p;
        }
        
        @Override
        public Request authenticate(Proxy proxy, Response response)
                throws IOException {
            String credential = Credentials.basic(username, password);
            return response.request().newBuilder()
                           .header("Authorization", credential).build();
        }
        
        @Override
        public Request authenticateProxy(Proxy arg0, Response arg1)
                throws IOException {
            return null;
        }
        
    }
    
    public static RequestBody create(final MediaType mediaType,
                                     final InputStream inputStream) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }
            
            @Override
            public long contentLength() {
                try {
                    return inputStream.available();
                } catch (IOException e) {
                    return 0;
                }
            }
            
            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source = null;
                try {
                    source = Okio.source(inputStream);
                    sink.writeAll(source);
                } finally {
                    Util.closeQuietly(source);
                }
            }
        };
    }
    
    
    private String bodyToString(final Request request) {
        
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return new String(buffer.readByteArray());
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
