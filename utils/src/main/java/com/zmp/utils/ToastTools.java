package com.zmp.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by zmp on 2017/12/11.
 * 可在子线程Toast
 */
public class ToastTools {

        private Toast mToast;

        private Handler mHandler;

        private ToastTools() {
        }

        private static ToastTools toastTools;

        public static ToastTools getDefault() {
                if (toastTools == null) {
                        toastTools = new ToastTools();
                }
                return toastTools;
        }

        public void init(Context context) {
                mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                mHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                                switch (msg.what) {
                                        case 0:
                                                mToast.setText(msg.obj.toString());
                                                mToast.show();
                                                break;
                                }
                        }
                };
        }

        public void show(String text) {
                if (TextUtils.isEmpty(text) || mHandler == null || Looper.getMainLooper() != mHandler.getLooper()) {
                        return;
                }
                mHandler.obtainMessage(0, text).sendToTarget();
        }
}
