package com.zmp.utils.okhttp;

/**
 * @author zmp
 */
public interface IProgressCallback {

        void onLoading(float current, float total);

        void onSuccess();

        void onFailed(String message);

        void onSave(long startsPoint, int len);
}