package com.zmp.utils;

import android.util.Log;

/**
 * @author zmp
 * 打印类名 行号的log
 */
public class Logger {

        private static final String TAG = "Logger";

        public static void e(String msg) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                String className = stackTrace[3].getClassName();
                int lineNumber = stackTrace[3].getLineNumber();
                String methodName = stackTrace[3].getMethodName();
                String format = String.format("%1$s->>%2$s->>%3$s:%4$s", className, methodName, lineNumber, msg);
                Log.e(TAG, format);
        }

        public static void d(String msg) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                String className = stackTrace[3].getClassName();
                int lineNumber = stackTrace[3].getLineNumber();
                String methodName = stackTrace[3].getMethodName();
                String format = String.format("%1$s->>%2$s->>%3$s:%4$s", className, methodName, lineNumber, msg);
                Log.d(TAG, format);
        }

        public static void i(String msg) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                String className = stackTrace[3].getClassName();
                int lineNumber = stackTrace[3].getLineNumber();
                String methodName = stackTrace[3].getMethodName();
                String format = String.format("%1$s->>%2$s->>%3$s:%4$s", className, methodName, lineNumber, msg);
                Log.i(TAG, format);
        }

        public static void v(String msg) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                String className = stackTrace[3].getClassName();
                int lineNumber = stackTrace[3].getLineNumber();
                String methodName = stackTrace[3].getMethodName();
                String format = String.format("%1$s->>%2$s->>%3$s:%4$s", className, methodName, lineNumber, msg);
                Log.v(TAG, format);
        }

        public static void w(String msg) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                String className = stackTrace[3].getClassName();
                int lineNumber = stackTrace[3].getLineNumber();
                String methodName = stackTrace[3].getMethodName();
                String format = String.format("%1$s->>%2$s->>%3$s:%4$s", className, methodName, lineNumber, msg);
                Log.w(TAG, format);
        }
}
