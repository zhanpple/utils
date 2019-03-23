package com.zmp.utils.okhttp;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * @author zmp
 */
public class FileResponseBody extends ResponseBody {

        private static final String TAG = "FileResponseBody";

        /**
         * 实际请求体
         */
        private ResponseBody mResponseBody;

        /**
         * 下载回调接口
         */
        private IProgressCallback mCallback;

        /**
         * BufferedSource
         */
        private BufferedSource mBufferedSource;

        public FileResponseBody(ResponseBody responseBody, IProgressCallback callback) {
                super();
                this.mResponseBody = responseBody;
                this.mCallback = callback;
        }

        @Override
        public BufferedSource source() {

                if (mBufferedSource == null) {
                        mBufferedSource = Okio.buffer(source(mResponseBody.source()));
                }
                return mBufferedSource;
        }

        @Override
        public long contentLength() {
                return mResponseBody.contentLength();
        }

        @Override
        public MediaType contentType() {
                return mResponseBody.contentType();
        }

        /**
         * 回调进度接口
         *
         * @param source dd
         * @return Source
         */
        private Source source(Source source) {
                return new ForwardingSource(source) {
                        volatile float totalBytesRead = 0L;

                        @Override
                        public synchronized long read(Buffer sink, long byteCount) throws IOException {
                                float bytesRead;
                                bytesRead = super.read(sink, byteCount);
                                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                                Log.e(TAG, "read: " + totalBytesRead + ",bytesRead:" + bytesRead);
                                mCallback.onLoading(totalBytesRead, mResponseBody.contentLength());
                                return (long) bytesRead;
                        }
                };
        }
}