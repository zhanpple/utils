package com.zmp.utils.okhttp;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author zmp
 * 带进度监听功能的辅助类   断点下载
 */
public class ProgressDownloader {

        public static final String TAG = "ProgressDownloader";

        private IProgressCallback progressListener;

        private String url;

        private OkHttpClient client;

        private File destination;

        private Call call;

        public ProgressDownloader(String url, File destination, IProgressCallback progressListener) {
                this.url = url;
                this.destination = destination;
                this.progressListener = progressListener;
                //在下载、暂停后的继续下载中可复用同一个client对象
                client = getProgressClient();
        }

        //每次下载需要新建新的Call对象
        private Call newCall(long startPoints) {
                Request request = new Request.Builder()
                        .url(url).header("RANGE", "bytes=" + startPoints + "-")//断点续传要用到的，指示下载的区间
                        .build();
                return client.newCall(request);
        }

        private OkHttpClient getProgressClient() {  // 拦截器，用上ProgressResponseBody
                Interceptor interceptor = new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                                Response originalResponse = chain.proceed(chain.request());
                                return originalResponse.newBuilder().body(new FileResponseBody(originalResponse.body(), progressListener)).build();
                        }
                };
                return new OkHttpClient.Builder().addNetworkInterceptor(interceptor).build();
        }

        // startsPoint指定开始下载的点
        public void download(final long startsPoint) {
                Log.e(TAG, "download:" + startsPoint);
                call = newCall(startsPoint);
                call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                                Log.e(TAG, e.getMessage());
                                progressListener.onFailed(e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) {
                                save(response, startsPoint);
                        }
                });
        }

        public void pause() {
                if (call != null) {
                        call.cancel();
                }
        }

        private void save(Response response, long startsPoint) {
                ResponseBody body = response.body();
                InputStream in = body.byteStream();
                FileChannel channelOut = null;
                // 随机访问文件，可以指定断点续传的起始位置
                RandomAccessFile randomAccessFile = null;
                boolean isSuccess = false;
                try {
                        randomAccessFile = new RandomAccessFile(destination, "rwd");
                        //Chanel NIO中的用法，由于RandomAccessFile没有使用缓存策略，直接使用会使得下载速度变慢，亲测缓存下载3.3秒的文件，用普通的RandomAccessFile需要20多秒。
                        channelOut = randomAccessFile.getChannel();      // 内存映射，直接使用RandomAccessFile，是用其seek方法指定下载的起始位置，使用缓存下载，在这里指定下载位置。
                        long size = body.contentLength();
                        Log.e(TAG, "size:" + size);
                        Log.e(TAG, "startsPoint:" + startsPoint);
                        MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE, startsPoint, size);
                        byte[] buffer = new byte[1024];
                        int len;
                        int count = 0;
                        while ((len = in.read(buffer)) != -1) {
                                mappedBuffer.put(buffer, 0, len);
                                count += len;
                                progressListener.onSave(startsPoint, count);
                        }
                        isSuccess = true;
                }
                catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, e.getMessage());
                        isSuccess = false;
                        progressListener.onFailed(e.getMessage());
                }
                finally {
                        try {
                                in.close();
                                if (channelOut != null) {
                                        channelOut.close();
                                }
                                if (randomAccessFile != null) {
                                        randomAccessFile.close();
                                }
                        }
                        catch (IOException e) {

                                e.printStackTrace();
                        }
                }
                if (isSuccess) {
                        progressListener.onSuccess();
                }
        }

}

