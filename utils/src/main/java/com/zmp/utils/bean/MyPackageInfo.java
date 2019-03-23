package com.zmp.utils.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by zmp on 2017/12/18.
 */

public class MyPackageInfo {

        private Drawable drawable;

        private String packageName;

        private String appName;

        private String fileSize;

        private boolean isSys;

        public MyPackageInfo(Drawable drawable, String packageName, String appName, String fileSize, boolean isSys) {
                this.drawable = drawable;
                this.packageName = packageName;
                this.appName = appName;
                this.fileSize = fileSize;
                this.isSys = isSys;
        }

        public Drawable getDrawable() {
                return drawable;
        }

        public void setDrawable(Drawable drawable) {
                this.drawable = drawable;
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
}
