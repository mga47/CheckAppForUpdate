package com.gar.checkappupdate.models;

import android.support.annotation.Nullable;
import java.net.URL;

/**
 * Model that contains information about APP:
 * - App version as string, for example 1.0.2
 * - App version code as Integer number, for example 16010002
 * - Latest release notes from Play store
 * - Valid URl to App page in Google play store
 */
public class Update {
    private String appVersion;
    private Integer appVersionCode;
    private String appReleaseNotes;
    private URL appAPK;

    public Update(String appVersion, Integer appVersionCode, String appReleaseNotes, URL appAPK) {
        this.appVersion = appVersion;
        this.appVersionCode = appVersionCode;
        this.appReleaseNotes = appReleaseNotes;
        this.appAPK = appAPK;
    }

    public Update(String appVersion, String appReleaseNotes, URL appAPK) {
        this(appVersion, null, appReleaseNotes, appAPK);
    }

    public Update(String appVersion, Integer appVersionCode) {
        this(appVersion, appVersionCode, null, null);
    }

    public Update(String appVersion) {
        this(appVersion, null, null, null);

    }

    public Update() {
    }

    @Nullable
    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    @Nullable
    public Integer getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(Integer appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    @Nullable
    public String getAppReleaseNotes() {
        return appReleaseNotes;
    }

    public void setAppReleaseNotes(String appReleaseNotes) {
        this.appReleaseNotes = appReleaseNotes;
    }

    @Nullable
    public URL getAppAPK() {
        return appAPK;
    }

    public void setAppAPK(URL appAPK) {
        this.appAPK = appAPK;
    }
}
