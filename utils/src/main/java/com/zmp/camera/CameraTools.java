package com.zmp.camera;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by zmp on 2017/7/20.
 */

/**
 * 摄像头工具类
 */
public class CameraTools{

        private String TAG = "BaseCameraActivity";

        private volatile static Camera mCamera;

        private static HandlerThread handlerThread;

        private static Handler handler;

        private volatile static CameraTools cameraTools;

        private ICallBack callBack;

        private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback(){
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                        if (mCamera != null) {
                                if (success) {
                                        mCamera.cancelAutoFocus();
                                }
                                mCamera.autoFocus(this);
                        }
                }
        };

        private Camera.PreviewCallback cb = new Camera.PreviewCallback(){
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                        mCamera.setOneShotPreviewCallback(this);
                        if (callBack != null) {
                                callBack.callBack(data);
                        }
                }
        };

        private int PREVIEW_WIDTH = 640;

        private int PREVIEW_HEIGHT = 480;


        private CameraTools() {
        }

        public synchronized static CameraTools getInstance() {
                if (cameraTools == null) {
                        cameraTools = new CameraTools();
                }
                cameraTools.initHandler();
                return cameraTools;
        }

        protected void initCamera(SurfaceHolder holder, ICallBack callBack) {
                this.callBack = callBack;
                handler.obtainMessage(0, holder).sendToTarget();
        }

        private void initHandler() {
                if (handlerThread == null || !handlerThread.isAlive()) {
                        handlerThread = new HandlerThread(UUID.randomUUID().toString());
                        handlerThread.start();
                        handler = new Handler(handlerThread.getLooper()){
                                @Override
                                public void handleMessage(Message msg) {
                                        switch (msg.what) {
                                                case 0:
                                                        boolean b = asyncInitCamera((SurfaceHolder) msg.obj);
                                                        if (callBack != null) {
                                                                callBack.initCameraBack(b);
                                                        }
                                                        break;
                                                case 1:
                                                        asyncOpenCamera();
                                                        break;
                                                case 2:
                                                        asyncStartPreview();
                                                        break;
                                                case 3:
                                                        asyncStopPreview();
                                                        break;
                                                case 4:
                                                        removeCallbacksAndMessages(null);
                                                        asyncRecycleCamera();
                                                        break;
                                        }

                                }
                        };
                }
        }

        private void asyncStopPreview() {
                if (mCamera != null) {
                        mCamera.stopPreview();
                }
        }

        private void asyncStartPreview() {
                if (mCamera != null) {
                        mCamera.startPreview();
                        mCamera.setOneShotPreviewCallback(cb);
//                        mCamera.autoFocus(autoFocusCallback);
                }
        }


        private boolean asyncInitCamera(SurfaceHolder holder) {
                Log.d(TAG, "asyncInitCamera:");
                try {
                        mCamera = Camera.open(0);
                        mCamera.setPreviewDisplay(holder);
                        return true;
                } catch (Exception ioe){
                        ioe.printStackTrace(System.out);
                        if (mCamera != null) {
                                mCamera.release();
                                mCamera = null;
                        }
                        Log.e(TAG, "初始化摄像头失败");
                        return false;
                }
        }

        protected void openCamera() {
                if (handler != null) {
                        boolean b = handler.sendEmptyMessage(1);
                        Log.d(TAG, "openCamera:" + b);
                }
        }

        private void asyncOpenCamera() {
                Log.d(TAG, "asyncOpenCamera:");
                if (mCamera != null) {
                        try {
                                Camera.Parameters parameters = mCamera.getParameters();
                                parameters.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);// 设置预览照片的大小
                                parameters.setPictureSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);

                                parameters.setPreviewFrameRate(30);// 设置每秒30帧

                                List<Integer> frameRates = parameters.getSupportedPreviewFrameRates();
                                for (int i : frameRates) {
                                        Log.d(TAG, "支持的预览帧数:" + i);
                                }
                                List<String> focusModes = parameters.getSupportedFocusModes();
                                if (focusModes.contains("continuous-video")) {
                                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                                }
                                if (focusModes.contains("continuous-picture")) {
                                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                                }
                                if (focusModes.contains("torch")) {
                                        parameters.setFocusMode(Camera.Parameters.FLASH_MODE_TORCH);
                                }
                                parameters.setPictureFormat(ImageFormat.JPEG);// 设置照片的格式（只支持JPEG）
                                parameters.setPreviewFormat(ImageFormat.NV21);// (支持NV21、YV12)
                                parameters.setJpegQuality(100);// 设置照片的质量
                                mCamera.setParameters(parameters);
                                mCamera.setOneShotPreviewCallback(cb);
                                mCamera.startPreview();
                                mCamera.cancelAutoFocus();
//                                mCamera.autoFocus(autoFocusCallback);
                                Log.e(TAG, "初始化摄像头成功");
                        } catch (Exception e){
                                e.printStackTrace();
                                Log.e(TAG, "初始化摄像头失败");
                        }
                }
        }

        /**
         * 释放摄像头
         */
        protected void recycleCamera() {
                if (handler != null) {
                        handler.sendEmptyMessage(4);
                }
        }

        /**
         * 释放摄像头
         */
        private void asyncRecycleCamera() {
                Log.d(TAG, "释放摄像头！recycleCamera");
                if (mCamera != null) {
                        try {
                                mCamera.setPreviewDisplay(null);
                        } catch (IOException e){
                                e.printStackTrace();
                        }
                        Log.d(TAG, "释放摄像头！printStackTrace");
                        mCamera.cancelAutoFocus();
                        Log.d(TAG, "释放摄像头！cancelAutoFocus");
                        mCamera.stopPreview();
                        Log.d(TAG, "释放摄像头！stopPreview");
                        mCamera.setOneShotPreviewCallback(null);
                        mCamera.setPreviewCallback(null);
                        Log.d(TAG, "释放摄像头！setPreviewCallback");
                        mCamera.release();
                        mCamera = null;
                        Log.d(TAG, "释放摄像头！release");
                        Log.e(TAG, "释放摄像头成功！");
                }
        }

        protected void stopPreview() {
                handler.sendEmptyMessage(3);
        }

        protected void startPreview() {
                handler.sendEmptyMessage(2);
        }


        protected interface ICallBack{

                void takePicCallBack(String photoName);

                void callBack(byte[] data);

                void initCameraBack(boolean isSuccess);
        }
}
