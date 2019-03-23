package com.zmp.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Method;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by zmp on 2017/12/27.
 */

public class StatusTools {

        public static int getStatusBarHeight(Context context) {
                // 获取状态栏高度——方法
                int statusBarHeight = -1;
                //获取status_bar_height资源的ID
                int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                        //根据资源ID获取响应的尺寸值
                        statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
                }
                return statusBarHeight;
        }

        /**
         * 隐藏状态栏
         * @param context Activity
         */
        public static void hideStatusBar(AppCompatActivity context) {
                context.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                context.requestWindowFeature(Window.FEATURE_NO_TITLE);
                context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
                context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                                             WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                hideBottomUIMenu(context);
        }

        /**
         * 隐藏虚拟按键，并且全屏
         */
        protected static void hideBottomUIMenu(final AppCompatActivity context) {

                context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                context.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                                //布局位于状态栏下方
                                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                                //全屏
                                                View.SYSTEM_UI_FLAG_FULLSCREEN |
                                                //隐藏导航栏
                                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                                uiOptions |= 0x00001000;
                                context.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
                        }
                });
        }


        public static int getDpi(Context context) {
                int dpi = 0;
                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                @SuppressWarnings("rawtypes")
                Class c;
                try {
                        c = Class.forName("android.view.Display");
                        @SuppressWarnings("unchecked")
                        Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
                        method.invoke(display, displayMetrics);
                        dpi = displayMetrics.heightPixels;
                }
                catch (Exception e) {
                        e.printStackTrace();
                }
                return dpi;
        }


        public static int getScreenHeight(Context context) {
                WindowManager wm = (WindowManager) context
                        .getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics outMetrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(outMetrics);
                return outMetrics.heightPixels;
        }

        /**
         * 获取 虚拟按键的高度
         *
         * @param context
         * @return
         */
        public static int getBottomStatusHeight(Context context) {
                int totalHeight = getDpi(context);

                int contentHeight = getScreenHeight(context);

                return totalHeight - contentHeight;
        }

        /**
         * 隐藏虚拟键
         *
         * @param dialog dialog
         */
        public static void hideVirtualKey(final Dialog dialog) {
                dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                dialog.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                        new View.OnSystemUiVisibilityChangeListener() {
                                @Override
                                public void onSystemUiVisibilityChange(int visibility) {
                                        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                                        //布局位于状态栏下方
                                                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                                        //全屏
                                                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                                                        //隐藏导航栏
                                                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                                        if (Build.VERSION.SDK_INT >= 19) {
                                                uiOptions |= 0x00001000;
                                        } else {
                                                uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                                        }
                                        dialog.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
                                }
                        });
        }

}
