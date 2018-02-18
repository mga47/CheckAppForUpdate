package com.gar.checkappupdate;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.gar.checkappupdate.models.Update;
import com.gar.checkappupdate.models.Version;

import java.net.URL;

public class UtilsLib {
    /**
     * Method take to Update objects, compare their versions
     * @param installedVersion Update object with installed App info
     * @param remoteVersion Update object with Play Market App info
     * @return true, if installed app version is smaller then Play Market version, otherwise return false
     */
    public static boolean isUpdateAvailable(@Nullable Update installedVersion, @Nullable Update remoteVersion) {
        boolean res = false;
        if (installedVersion == null || remoteVersion == null) {
            return false;
        }

        if (remoteVersion.getAppVersionCode() != null && remoteVersion.getAppVersionCode() > 0) {
            return remoteVersion.getAppVersionCode() > installedVersion.getAppVersionCode();
        } else {
            if (!TextUtils.equals(installedVersion.getAppVersion(), "0.0.0.0")
                    && !TextUtils.equals(remoteVersion.getAppVersion(), "0.0.0.0")) {

                if (installedVersion.getAppVersion() == null || remoteVersion.getAppVersion() == null) {
                    return false;
                }

                Version installed = new Version(installedVersion.getAppVersion());
                Version latest = new Version(remoteVersion.getAppVersion());
                res = installed.compareTo(latest) < 0;
            }
        }

        return res;
    }

    /**
     * @param context application context
     * @return empty string if context is null or App package name
     */
    @NonNull
    public static String getAppPackageName(Context context) {
        return context == null ? "" : context.getPackageName();
    }

    /**
     * @param context App context
     * @return Intent with play market page info
     */
    public static Intent intentToUpdate(Context context) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getAppPackageName(context)));
    }

    /**
     * Open in Play market or Browser App page
     * @param context application context
     * @param url valid url to play market
     */
    public static void goToUpdate(Context context, URL url) {
        if (context == null || url == null) {
            return;
        }
        Intent intent = intentToUpdate(context);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
            context.startActivity(intent);
        }
    }
}
