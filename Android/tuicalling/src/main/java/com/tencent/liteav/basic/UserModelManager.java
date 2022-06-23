package com.tencent.liteav.basic;

import android.util.Log;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserModelManager {
    private static final String TAG = "UserModelManager";

    private static final String PER_DATA       = "per_profile_manager";
    private static final String PER_USER_MODEL = "per_user_model";
    private static final String PER_USER_DATE  = "per_user_publish_video_date";
    private static final String USAGE_MODEL    = "usage_model";
    private static final String HAVE_BACKSTAGE = "have_backstage";

    private static UserModelManager sInstance;
    private        UserModel        mUserModel;
    private        String           mUserPubishVideoDate;
    private        Boolean          mHaveBackstage;

    public static UserModelManager getInstance() {
        if (sInstance == null) {
            synchronized (UserModelManager.class) {
                if (sInstance == null) {
                    sInstance = new UserModelManager();
                }
            }
        }
        return sInstance;
    }

    public synchronized UserModel getUserModel() {
        if (mUserModel == null) {
            loadUserModel();
        }
        return mUserModel == null ? new UserModel() : mUserModel;
    }

    public synchronized void setUserModel(UserModel model) {
        mUserModel = model;
        try {
            SPUtils.getInstance(PER_DATA).put(PER_USER_MODEL, GsonUtils.toJson(mUserModel));
        } catch (Exception e) {
            Log.d(TAG, "");
        }
    }

    private void loadUserModel() {
        try {
            String json = SPUtils.getInstance(PER_DATA).getString(PER_USER_MODEL);
            mUserModel = GsonUtils.fromJson(json, UserModel.class);
        } catch (Exception e) {
        }
    }

    public synchronized void clearUserModel() {
        try {
            SPUtils.getInstance(PER_DATA).put(PER_USER_MODEL, "");
        } catch (Exception e) {
            Log.e(TAG, "clear user model error:" + e.getMessage());
        }
    }

    private String getUserPublishVideoDate() {
        if (mUserPubishVideoDate == null) {
            mUserPubishVideoDate = SPUtils.getInstance(PER_DATA).getString(PER_USER_DATE, "");
        }
        return mUserPubishVideoDate;
    }

    private void setUserPublishVideoDate(String date) {
        mUserPubishVideoDate = date;
        try {
            SPUtils.getInstance(PER_DATA).put(PER_USER_DATE, mUserPubishVideoDate);
        } catch (Exception e) {
        }
    }

    public void setUsageModel(boolean haveBackstage) {
        mHaveBackstage = haveBackstage;
        try {
            SPUtils.getInstance(USAGE_MODEL).put(HAVE_BACKSTAGE, mHaveBackstage);
        } catch (Exception e) {
            Log.d(TAG, "setUsageModel failed");
        }
    }

    public boolean haveBackstage() {
        if (mHaveBackstage == null) {
            mHaveBackstage = SPUtils.getInstance(USAGE_MODEL).getBoolean(HAVE_BACKSTAGE, true);
        }
        return mHaveBackstage;
    }

    public boolean needShowSecurityTips() {
        String profileDate = getUserPublishVideoDate();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        String day = formatter.format(date);
        if (!day.equals(profileDate)) {
            setUserPublishVideoDate(day);
            return true;
        }
        return false;
    }
}