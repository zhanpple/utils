package com.zmp.utils.okhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zmp on 2017/4/8.
 */

public class OkHttpUtils {

        private static OkHttpClient mOkHttpClient;

        private static OkHttpUtils okHttpUtils;

        private static final String TAG = "OkHttpUtils";

        public static OkHttpUtils getInstance() {
                if (okHttpUtils == null) {
                        okHttpUtils = new OkHttpUtils();
                }
                return okHttpUtils;
        }

        private static final int CONNECT_TIMEOUT = 5000;

        private static final int READ_TIMEOUT = 5000;

        private static final int WRITE_TIMEOUT = 5000;

        private OkHttpUtils() {
                mOkHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                        .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                        .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                        .addInterceptor(new Interceptor() {
                                @Override
                                public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {

                                        okhttp3.Response response = chain.proceed(chain.request());
                                        //将ResponseBody转换成我们需要的FileResponseBody
                                        return response.newBuilder()
                                                       .body(new FileResponseBody(response.body(), new IProgressCallback() {
                                                               @Override
                                                               public void onLoading(float current, float total) {
                                                                       Log.e(TAG, current * 100.0 / total + "%");
                                                               }

                                                               @Override
                                                               public void onSuccess() {

                                                               }

                                                               @Override
                                                               public void onFailed(String message) {

                                                               }

                                                               @Override
                                                               public void onSave(long startsPoint, int len) {

                                                               }
                                                       }))
                                                       .build();
                                }
                        })
                        .build();
        }

        /**
         * get请求.
         *
         * @param url        请求地址
         * @param tag        请求TAG
         * @param myCallBack 请求结果回调
         * @param <T>        返回类型
         */
        public <T> void get(String url, Object tag, final MyCallBack<T> myCallBack) {
                Request request = new Request.Builder().url(url).get().tag(tag).build();
                mOkHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onResponse(Call arg0, Response response) {
                                Log.e(TAG, "onResponse:" + response.isSuccessful());
                                if (response.isSuccessful()) {
                                        try {
                                                if (myCallBack != null) {
                                                        Class<T> clazz = myCallBack.getClazz();
                                                        if (clazz == String.class) {
                                                                String string = response.body().string();
                                                                myCallBack.onResponse((T) string);
                                                                return;
                                                        }
                                                        if (clazz == Bitmap.class) {
                                                                myCallBack.onResponse((T) BitmapFactory.decodeStream(response.body().byteStream()));
                                                                return;
                                                        }
                                                        String string = response.body().string();
                                                        T object = JSONObject.parseObject(string, clazz);
                                                        myCallBack.onResponse(object);
                                                }
                                        }
                                        catch (IOException e) {
                                                e.printStackTrace();
                                                if (myCallBack != null) {
                                                        myCallBack.onFailure(e.getMessage());
                                                }
                                        }
                                        catch (JSONException jsonException) {
                                                jsonException.printStackTrace();
                                                if (myCallBack != null) {
                                                        myCallBack.onFailure(jsonException.getMessage());
                                                }
                                        }
                                }
                                else {
                                        if (myCallBack != null) {
                                                myCallBack.onFailure("onResponse:onFailure");
                                        }
                                }
                        }

                        @Override
                        public void onFailure(Call arg0, IOException arg1) {
                                arg1.printStackTrace();
                                if (myCallBack != null) {
                                        myCallBack.onFailure(arg1.getMessage());
                                }
                        }
                });
        }

        /**
         * 基于Url get请求
         *
         * @param httpOrs     http://  https://
         * @param host        //主机地址
         * @param pathSegment //相对地址
         * @param paramsMap   //请求参数
         * @param tag         //请求TAG
         * @param callback    //请求结果
         */
        public void get(String httpOrs, String host, String[] pathSegment, Map<String, String> paramsMap, Object tag, Callback callback) {
                HttpUrl.Builder https = new HttpUrl.Builder().scheme(httpOrs).host(host);
                if (pathSegment != null && pathSegment.length > 0) {
                        for (String s : pathSegment) {
                                Log.d(TAG, "pathSegment" + s);
                                https.addPathSegment(s);
                        }
                }
                if (paramsMap != null && paramsMap.size() > 0) {
                        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                                https.addQueryParameter(entry.getKey(), entry.getValue());
                        }
                }
                get(https.build(), tag, callback);
        }


        public void get(HttpUrl httpUrl, Object tag, Callback callback) {
                Request request = new Request.Builder().url(httpUrl).tag(tag).get().build();
                mOkHttpClient.newCall(request).enqueue(callback);
        }

        /**
         * 基于tag 取消请求.
         *
         * @param tag 请求tag
         */
        public synchronized void cancel(Object tag) {
                if (tag != null) {
                        return;
                }
                for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
                        if (tag.equals(call.request().tag())) {
                                call.cancel();
                        }
                }
                for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
                        if (tag.equals(call.request().tag())) {
                                call.cancel();
                        }
                }
        }

        public interface MyCallBack<T> {

                void onResponse(T t);

                Class<T> getClazz();

                void onFailure(String s);
        }


        /**
         * 表单提交.
         *
         * @param actionUrl 接口地址
         * @param paramsMap 参数
         * @param callBack  回调
         */
        public static void postForm(String actionUrl, HashMap<String, ? extends Object> paramsMap, final OkHttpCallBack callBack) {
                try {
                        //补全请求地址
                        //String requestUrl = "";//String.format("%checkStyle.xml/%checkStyle.xml", upload_head, actionUrl);
                        MultipartBody.Builder builder = new MultipartBody.Builder();
                        //设置类型
                        builder.setType(MultipartBody.FORM);
                        //追加参数
                        for (String key : paramsMap.keySet()) {
                                Object object = paramsMap.get(key);
                                Log.e(TAG, "file: " + object);
                                if (null == object) {
                                        continue;
                                }
                                if (!(object instanceof File)) {
                                        builder.addFormDataPart(key, object.toString());
                                }
                                else {
                                        File file = (File) object;
                                        Log.v(TAG, "key" + key + "file: " + file.getName());
                                        String uploadType = "application/*";//二进制
                                        if (key.equals("audioFile")) {
                                                uploadType = "audio/*";
                                        }
                                        else if (key.equals("imageFile")) {
                                                uploadType = "image/*";
                                        }
                                        builder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse(uploadType), file));
                                }
                        }
                        //创建RequestBody
                        RequestBody body = builder.build();
                        //创建Request
                        final Request request = new Request.Builder().url(actionUrl).post(body).build();
                        //单独设置参数 比如读取超时时间
                        mOkHttpClient.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                        Log.d(TAG, "response ----->" + e.toString());
                                        if (callBack != null) {
                                                callBack.onFailure(e.getMessage());
                                        }
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                                String string = response.body().string();
                                                Log.d(TAG, "response ----->" + string);
                                                if (callBack != null) {
                                                        callBack.onSuccess(string);
                                                }
                                        }
                                        else {
                                                if (callBack != null) {
                                                        callBack.onFailure("response:onFailure");
                                                }
                                        }
                                }
                        });
                }
                catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public interface OkHttpCallBack {

                void onSuccess(String result);

                void onFailure(String ex);
        }


}
