package com.zmp.utils;

/**
 * @author zmp
 * 线程池工厂.
 */
public class ThreadPoolProxyFactory {

        private static ThreadPoolProxy mNormalThreadPoolProxy;

        public static synchronized ThreadPoolProxy getNormalThreadPoolProxy() {
                if (mNormalThreadPoolProxy == null) {
                        synchronized (ThreadPoolProxyFactory.class) {
                                        mNormalThreadPoolProxy = new ThreadPoolProxy(5, 5);
                        }
                }
                return mNormalThreadPoolProxy;
        }
}
