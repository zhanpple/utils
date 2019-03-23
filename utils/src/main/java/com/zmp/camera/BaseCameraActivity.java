package com.zmp.camera;

import android.os.Bundle;
import android.view.SurfaceHolder;

import androidx.appcompat.app.AppCompatActivity;

/**
 *
 * @author zmp
 * @date 2017/7/20
 */

public abstract class BaseCameraActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
        }

        protected void initCamera(SurfaceHolder holder) {
                CameraTools.getInstance().initCamera(holder, new CameraTools.ICallBack() {
                        @Override
                        public void takePicCallBack(String photoName) {
                                takePhotoBack(photoName);
                        }

                        @Override
                        public void callBack(byte[] data) {
                                onPreviewCallback(data);
                        }

                        @Override
                        public void initCameraBack(boolean isSuccess) {
                                initBack(isSuccess);
                        }
                });

        }


        /**
         * 初始化结果回调.
         *
         * @param isSuccess
         */
        protected void initBack(boolean isSuccess) {
        }

        /**
         * 拍照结果回调.
         *
         * @param pathName
         */
        protected void takePhotoBack(String pathName) {
        }


        protected void openCamera() {
                CameraTools.getInstance().openCamera();
        }


        /**
         * 释放摄像头
         */
        protected void recycleCamera() {
                CameraTools.getInstance().recycleCamera();
        }

        /**
         * 摄像头数据预览回调.
         *
         * @param data
         */
        protected abstract void onPreviewCallback(byte[] data);

        protected void stopPreview() {
                CameraTools.getInstance().stopPreview();
        }

        protected void startPreview() {
                CameraTools.getInstance().startPreview();
        }

}
