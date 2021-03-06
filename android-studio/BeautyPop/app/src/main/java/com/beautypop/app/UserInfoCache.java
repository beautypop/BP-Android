package com.beautypop.app;

import android.util.Log;

import com.beautypop.util.SharedPreferencesUtil;
import com.beautypop.viewmodel.SettingsVM;
import com.beautypop.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;

public class UserInfoCache {

    private static UserVM userInfo;

    private static boolean skippedAndroidUpgrade = false;

    private UserInfoCache() {}

    static {
        init();
    }

    private static void init() {
    }

    public static void refresh() {
        refresh(null);
    }

    public static void refresh(final Callback<UserVM> userCallback) {
        refresh(AppController.getInstance().getSessionId(), userCallback);
    }

    /**
     * For login screen
     * @param sessionId
     * @param userCallback
     */
    public static void refresh(final String sessionId, final Callback<UserVM> userCallback) {
        Log.d(UserInfoCache.class.getSimpleName(), "refresh");

        AppController.getApiService().getUserInfo(sessionId, new Callback<UserVM>() {
            @Override
            public void success(UserVM userVM, retrofit.client.Response response) {
                if (userVM == null || userVM.id == -1) {
                    AppController.getInstance().logout();
                } else {
                    SharedPreferencesUtil.getInstance().saveUserInfo(userVM);
                }

                userInfo = userVM;
                if (userCallback != null) {
                    userCallback.success(userVM, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (userCallback != null) {
                    userCallback.failure(error);
                }
                Log.e(UserInfoCache.class.getSimpleName(), "refresh.api.getUserInfo: failure", error);
            }
        });
    }

    public static UserVM getUser() {
        if (userInfo == null)
            userInfo = SharedPreferencesUtil.getInstance().getUserInfo();
        return userInfo;
    }

    public static void incrementNumProducts() {
        getUser().numProducts++;
    }

    public static void decrementNumProducts() {
        getUser().numProducts--;
    }

    public static void incrementNumLikes() {
        getUser().numLikes++;
    }

    public static void decrementNumLikes() {
        getUser().numLikes--;
    }

    public static void skipAndroidUpgrade() {
        skippedAndroidUpgrade = true;
    }

    public static boolean requestAndroidUpgrade() {
        if (skippedAndroidUpgrade) {
            return false;
        }

        SettingsVM settings = getUser().settings;
        if (settings == null) {
            return false;
        }

        Log.d(UserInfoCache.class.getSimpleName(), "requestAndroidUpgrade: client version="+AppController.getVersionCode());
        Log.d(UserInfoCache.class.getSimpleName(), "requestAndroidUpgrade: system version="+settings.systemAndroidVersion);

        try {
            int systemAndroidVersion = Integer.valueOf(settings.systemAndroidVersion);
            if (AppController.getVersionCode() < systemAndroidVersion) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static void clear() {
        SharedPreferencesUtil.getInstance().clear(SharedPreferencesUtil.USER_INFO);
    }
}
