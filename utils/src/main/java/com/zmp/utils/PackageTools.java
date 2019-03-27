package com.zmp.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.text.format.Formatter;

import com.zmp.utils.bean.MyPackageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zmp
 */
public class PackageTools {

        /***
         * 获取所有安装的应用程序
         * @param context
         * @return
         */
        public static ArrayList<MyPackageInfo> getList(Context context) {
                ArrayList<MyPackageInfo> myList = new ArrayList<>();
                PackageManager pm = context.getPackageManager();
                List<PackageInfo> list = pm
                        .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
                for (PackageInfo info : list) {
                        ApplicationInfo app = info.applicationInfo;
                        String appName = app.loadLabel(pm).toString();
                        long length = new File(app.sourceDir).length();
                        String fileSize = Formatter.formatFileSize(context, length);
                        Boolean isSys = (app.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                        Boolean isSd = (app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0;
                        MyPackageInfo myPackageInfo = new MyPackageInfo(app.packageName, appName, fileSize, isSys, isSd);
                        myList.add(myPackageInfo);
                }
                return myList;
        }

        /**
         * 获取所有正在运行的进程
         *
         * @param context context
         * @return getProgress
         */
        public static List<MyPackageInfo> getProgress(Context context) {
                List<MyPackageInfo> list = new ArrayList<>();
                ActivityManager am = (ActivityManager) context
                        .getSystemService(Context.ACTIVITY_SERVICE);
                PackageManager pm = context.getPackageManager();
                List<ActivityManager.RunningAppProcessInfo> runList = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo runInfo : runList) {
                        String packageName = runInfo.pkgList[0];
                        ApplicationInfo appinfo = null;
                        try {
                                appinfo = pm.getApplicationInfo(packageName,
                                        PackageManager.GET_UNINSTALLED_PACKAGES);
                        } catch (PackageManager.NameNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                        if (appinfo != null) {
                                String appName = appinfo.loadLabel(pm).toString();
                                int[] pidS = new int[]{runInfo.pid};
                                //获取某个进程的内存信息
                                android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(pidS);
                                //获取进程所使用总内存
                                int totalMemory = processMemoryInfo[0].getTotalPrivateDirty();
                                String memory = Formatter.formatFileSize(context, totalMemory);
                                Boolean isSd = (appinfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0;
                                boolean isSys = (appinfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                                MyPackageInfo myPackageInfo = new MyPackageInfo(packageName, appName, memory, isSys, isSd);
                                list.add(myPackageInfo);
                        }
                }
                return list;
        }

        /**
         * 获取手机总共可用的进程数
         *
         * @return getTotalAvailProcNum
         */
        public static int getTotalAvailProcNum(Context ctx) {
                // step 1 . 获取所有安装的应用
                PackageManager pm = ctx.getPackageManager();
                List<PackageInfo> installedPackages = pm.getInstalledPackages(
                        PackageManager.GET_SERVICES
                                | PackageManager.GET_PROVIDERS
                                | PackageManager.GET_RECEIVERS
                                | PackageManager.GET_ACTIVITIES);
                // step 2 . 遍历所有安装的应用程序，统计每一个程序的可产生的进程数
                int count = 0;
                for (PackageInfo info : installedPackages) {
                        Set<String> set = new HashSet<>();
                        ApplicationInfo applicationInfo = info.applicationInfo;
                        set.add(applicationInfo.processName);

                        ActivityInfo[] activities = info.activities;
                        if (activities != null) {
                                for (ActivityInfo aInfo : activities) {
                                        set.add(aInfo.processName);
                                }
                        }

                        ActivityInfo[] receivers = info.receivers;
                        if (receivers != null) {
                                for (ActivityInfo rInfo : receivers) {
                                        set.add(rInfo.processName);
                                }
                        }

                        ServiceInfo[] services = info.services;
                        if (services != null) {
                                for (ServiceInfo sInfo : services) {
                                        set.add(sInfo.processName);
                                }
                        }

                        ProviderInfo[] providers = info.providers;
                        if (providers != null) {
                                for (ProviderInfo pInfo : providers) {
                                        set.add(pInfo.processName);
                                }
                        }

                        count += set.size();
                }
                return count;
        }

        /**
         * 获取手机运行内存   outInfo.totalMem总内存    outInfo.availMem可用内存
         *
         * @param context context
         * @return getTotalMemNum
         */
        public static long getTotalMemNum(Context context) {
                //ActivityManager
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                //通过ActivityManager获取手机的内存信息
                ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(outInfo);
                return outInfo.totalMem;
        }

        public static long getFreeMemNum(Context context) {
                // ActivityManager
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                // 通过ActivityManager获取手机的内存信息
                ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(outInfo);
                return outInfo.availMem;
        }
}