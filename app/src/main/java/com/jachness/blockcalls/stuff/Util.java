/*
 * Copyright (C) 2017 Jonatan Cheiro Anriquez
 *
 * This file is part of Block Calls.
 *
 * Block Calls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Block Calls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Block Calls. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jachness.blockcalls.stuff;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.jachness.blockcalls.R;
import com.jachness.blockcalls.exceptions.FileException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Locale;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 28/9/2016.
 */
public class Util {
    private static final String TAG = Util.class.getSimpleName();

    @SuppressWarnings("deprecation")
    @DebugLog
    public static Locale getDefaultLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            return Resources.getSystem().getConfiguration().locale;
        }
    }

    @DebugLog
    public static int getStringId(@NonNull FileException e) {
        switch (e.getErrorCode()) {
            case FileException.MEDIA_ERROR:
                return R.string.common_error_external_storage;
            case FileException.WRITE_FILE_ERROR:
                return R.string.common_error_write_file;
            case FileException.READ_FILE_ERROR:
                return R.string.common_error_read_file;
            default:
                throw new IllegalArgumentException("ErrorCode: " + e.getErrorCode(), e);
        }

    }

    @DebugLog
    public static void lockScreenOrientation(Activity activity) {
        int currentOrientation = activity.getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @DebugLog
    public static void unlockScreenOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    @DebugLog
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context
                .ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer
                .MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @DebugLog
    public static String getNumberExample(Context context) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber example = phoneUtil.getExampleNumber(Util.getDeviceCountryISO
                (context));
        return phoneUtil.format(example, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
    }

    public static void close(Cursor cursor) {
        if (cursor != null) cursor.close();
    }

    @DebugLog
    public static String getDeviceCountryISO(Context context) {

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context
                .TELEPHONY_SERVICE);

        String result = null;
        // On CDMA TelephonyManager.getNetworkCountryIso() just returns the SIM's country code.
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            result = tm.getNetworkCountryIso();
        }

        if (TextUtils.isEmpty(result)) {
            result = tm.getSimCountryIso();
        }

        if (TextUtils.isEmpty(result)) {
            Locale locale = getDefaultLocale();
            if (locale != null) {
                result = getDefaultLocale().getCountry();
                Log.w(TAG, "Setting system locale: " + result);
            }
        }

        if (TextUtils.isEmpty(result)) {
            result = Locale.US.getCountry();
            Log.w(TAG, "Setting default locale");
        }

        return result.toUpperCase(Locale.US);
    }

    public static void close(PrintWriter writer) {
        if (writer != null) writer.close();
    }

    public static String retrieveDebugLog(Context context) {
        return buildDescription(context) + "\n" + grabLogcat();
    }

    private static String buildDescription(Context context) {
        final PackageManager pm = context.getPackageManager();
        final StringBuilder builder = new StringBuilder();


        builder.append("Device  : ")
                .append(Build.MANUFACTURER).append(" ")
                .append(Build.MODEL).append(" (")
                .append(Build.PRODUCT).append(")\n");
        builder.append("Android : ").append(Build.VERSION.RELEASE).append(" (")
                .append(Build.VERSION.INCREMENTAL).append(", ")
                .append(Build.DISPLAY).append(")\n");
        builder.append("Memory  : ").append(getMemoryUsage()).append("\n");
        builder.append("Memclass: ").append(getMemoryClass(context)).append("\n");
        builder.append("OS Host : ").append(Build.HOST).append("\n");
        builder.append("App     : ");
        try {
            builder.append(pm.getApplicationLabel(pm.getApplicationInfo(context.getPackageName(),
                    0)))
                    .append(" ")
                    .append(pm.getPackageInfo(context.getPackageName(), 0).versionName)
                    .append("\n");
        } catch (PackageManager.NameNotFoundException nnfe) {
            builder.append("Unknown\n");
        }

        return builder.toString();
    }

    private static String getMemoryUsage() {
        Runtime info = Runtime.getRuntime();
        info.totalMemory();
        return String.format(Locale.ENGLISH, "%dM (%.2f%% free, %dM max)",
                asMegs(info.totalMemory()),
                (float) info.freeMemory() / info.totalMemory() * 100f,
                asMegs(info.maxMemory()));
    }

    private static long asMegs(long bytes) {
        return bytes / 1048576L;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getMemoryClass(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context
                .ACTIVITY_SERVICE);
        String lowMem = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && activityManager.isLowRamDevice
                ()) {
            lowMem = ", low-mem device";
        }
        return activityManager.getMemoryClass() + lowMem;
    }

    private static String grabLogcat() {
        try {
            final Process process = Runtime.getRuntime().exec("logcat -d");
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                    (process.getInputStream()));
            final StringBuilder log = new StringBuilder();
            final String separator = System.getProperty("line.separator");

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
                log.append(separator);
            }
            return log.toString();
        } catch (IOException ioe) {
            Log.w(TAG, "IOException when trying to read logcat.", ioe);
            return "Exception grabbing logcat";
        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(html);
        }
    }
}
