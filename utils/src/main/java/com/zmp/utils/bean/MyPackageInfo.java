package com.zmp.utils.bean;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.zmp.utils.R;

/**
 * @author zmp
 * @date 2017/12/18
 */

public class MyPackageInfo {

        private String packageName;

        private String appName;

        private String fileSize;

        private boolean isSys;


        private boolean isSd;

        public MyPackageInfo(String packageName, String appName, String fileSize, boolean isSys, boolean isSd) {
                this.packageName = packageName;
                this.appName = appName;
                this.fileSize = fileSize;
                this.isSys = isSys;
                this.isSd = isSd;
        }

        public String getPackageName() {
                return packageName;
        }

        public void setPackageName(String packageName) {
                this.packageName = packageName;
        }

        public String getAppName() {
                return appName;
        }

        public void setAppName(String appName) {
                this.appName = appName;
        }

        public String getFileSize() {
                return fileSize;
        }

        public void setFileSize(String fileSize) {
                this.fileSize = fileSize;
        }

        public boolean isSys() {
                return isSys;
        }

        public void setSys(boolean sys) {
                isSys = sys;
        }


        public boolean isSd() {
                return isSd;
        }

        public void setSd(boolean sd) {
                isSd = sd;
        }

        public Drawable getDrawable(Context context) {
                try {
                        return context.getPackageManager().getApplicationIcon(packageName);
                } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                }
                return null;
        }

        @Override
        public String toString() {
                return "MyPackageInfo{" +
                        "packageName='" + packageName + '\'' +
                        ", appName='" + appName + '\'' +
                        ", fileSize='" + fileSize + '\'' +
                        ", isSys=" + isSys +
                        ", isSd=" + isSd +
                        '}';
        }
}
