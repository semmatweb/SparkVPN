package com.bycomsolutions.bycomvpn.utils;

import android.content.Context;

import java.util.Locale;

import unified.vpn.sdk.UnifiedSdkConfig;

public class Utils {
    public static String humanReadableByteCountOld(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = String.valueOf((si ? "kMGTPE" : "KMGTPE").charAt(exp - 1));
        return String.format(Locale.ENGLISH, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    public static String megabyteCount(long bytes) {
        return String.format(Locale.getDefault(), "%.0f", (double) bytes / 1024 / 1024);
    }
}
