package com.zmp.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.zxing.Result;
import com.zmp.utils.R;
import com.zmp.utils.qr.QRCodeTools;

public class QRScanActivity extends BaseCameraActivity implements SurfaceHolder.Callback, View.OnClickListener {


        public static final String  CODE_RESULT = "codeResult";;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(getLayout());
                findViewById(R.id.code_iv_back).setOnClickListener(this);
                SurfaceView surfaceView = (SurfaceView) findViewById(R.id.sv);
                surfaceView.getHolder().addCallback(this);
        }
        @LayoutRes
        public int getLayout(){
                return R.layout.activity_qr_scan;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
                initCamera(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                openCamera();

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
                recycleCamera();
        }

        boolean isQr = false;

        @Override
        protected void onPreviewCallback(byte[] data) {
                if (isQr) {
                        return;
                }
                isQr = true;
                QRCodeTools.analyzeYuvData(data, 640, 480, analyzeCallback);
        }

        private QRCodeTools.AnalyzeCallback analyzeCallback = new QRCodeTools.AnalyzeCallback() {
                @Override
                public void onAnalyzeSuccess(Bitmap mBitmap, Result result, int sampleSize) {
                        Log.e("result", result.getText());
                        Intent codeResult = new Intent().putExtra(CODE_RESULT, result.getText());
                        setResult(RESULT_OK, codeResult);
                        finish();
                }

                @Override
                public void onAnalyzeFailed() {
                        isQr = false;
                        Log.e("result", "onAnalyzeFailed");
                }
        };

        @Override
        public void onClick(View v) {
                finish();
        }
}
