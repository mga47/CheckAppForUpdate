package com.gar.checkappupdate.models;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Utility class to compare App versions
 */
public class Version implements Comparable<Version> {
    private String version;

    public String get() {
        return this.version;
    }

    public Version(String version) {
        if (version == null) {
            Log.e("Error", "Version is null");
            return;
        }
        version = version.replaceAll("[^0-9?!\\.]", "");
        if (!version.matches("[0-9]+(\\.[0-9]+)*")) {
            Log.e("Error", "Wrong format");
        }
        this.version = version;
    }

    /**
     * Compare to Version objects
     * @param that .
     * @return -1 if current version of object is less, 1 if current version is greater and 0 if they are equal
     */
    @Override
    public int compareTo(@NonNull Version that) {
        String[] thisParts = this.get().split("\\.");
        String[] thatParts = that.get().split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for (int i = 0; i < length; i++) {
            try {
                int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
                int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;
                if (thisPart < thatPart) {
                    return -1;
                }
                if (thisPart > thatPart) {
                    return 1;
                }
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object that) {
        return this == that || that != null && this.getClass() == that.getClass() && this.compareTo((Version) that) == 0;
    }

}
