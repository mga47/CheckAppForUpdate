package com.gar.checkappupdate;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gar.checkappupdate.models.Update;

public class InstalledVersion {
    /**
     * Method return information about installed app
     * @param context .
     * @return Update object, that contains app version and app version code
     */
    @Nullable
    public static Update get(Context context) {
        if (context == null) {
            return null;
        }
        String version = "0.0.0.0";
        Integer versionCode = 0;

        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Exception", e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e("Exception", e.getLocalizedMessage());
        }

        return new Update(version, versionCode);
    }
}
