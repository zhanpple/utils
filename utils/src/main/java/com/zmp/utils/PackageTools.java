package com.zmp.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;

import com.zmp.utils.bean.MyPackageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                        Drawable loadIcon = app.loadIcon(pm);
                        long length = new File(app.sourceDir).length();
                        String fileSize = Formatter.formatFileSize(context, length);
                        Boolean isSd = (app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0;
                        Boolean isSys = (app.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                        MyPackageInfo myPackageInfo = new MyPackageInfo(loadIcon,
                                app.packageName, appName, fileSize, isSys);
                        myList.add(myPackageInfo);
                }
                return myList;
        }

        /**
         * 获取所有正在运行的进程
         *
         * @param context
         * @return
         */
        public static List<MyPackageInfo> getProgress(Context context) {
                List<MyPackageInfo> list = new ArrayList<>();
                ActivityManager am = (ActivityManager) context
                        .getSystemService(Context.ACTIVITY_SERVICE);
                PackageManager pm = context.getPackageManager();
                List<ActivityManager.RunningAppProcessInfo> runList = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo runInfo : runList) {
                        String packageName = runInfo.pkgList[0];
                        ApplicationInfo Appinfo = null;
                        try {
                                Appinfo = pm.getApplicationInfo(packageName,
                                                                PackageManager.GET_UNINSTALLED_PACKAGES);
                        }
                        catch (PackageManager.NameNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                        if (Appinfo != null) {
                                Drawable image = Appinfo.loadIcon(pm);
                                String appName = Appinfo.loadLabel(pm).toString();
                                int[] pids = new int[]{runInfo.pid};
                                android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(pids); //获取某个进程的内存信息
                                int totalMemory = processMemoryInfo[0].getTotalPrivateDirty();//获取进程所使用总内存
                                String memory = Formatter.formatFileSize(context, totalMemory);
                                boolean isSys = (Appinfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                                MyPackageInfo myPackageInfo = new MyPackageInfo(image, packageName, appName, memory, isSys);
                                list.add(myPackageInfo);
                        }
                }
                return list;
        }

        /**
         * 获取手机总共可用的进程数
         *
         * @return
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
         * @param context
         * @return
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