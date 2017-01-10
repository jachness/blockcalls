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

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.jachness.blockcalls.R;
import com.jachness.blockcalls.exceptions.FileException;

import java.io.PrintWriter;
import java.util.Locale;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 28/9/2016.
 */
public class Util {


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
            }
        }

        if (TextUtils.isEmpty(result)) {
            result = Locale.US.getCountry();
        }

        return result.toUpperCase(Locale.US);
    }

    public static void close(PrintWriter writer) {
        if (writer != null) writer.close();
    }
}
