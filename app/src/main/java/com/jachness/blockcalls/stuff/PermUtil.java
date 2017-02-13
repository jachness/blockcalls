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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;

import java.util.List;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 28/12/2016.
 */

@SuppressWarnings("SimplifiableIfStatement")
public class PermUtil {
    public static final String READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    public static final String CALL_PHONE = Manifest.permission.CALL_PHONE;
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    @SuppressLint("InlinedApi")
    public static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;

    @DebugLog
    public static boolean checkReadContacts(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @DebugLog
    public static boolean checkCallPhone(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @DebugLog
    public static boolean checkWriteExternalStorage(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager
                    .PERMISSION_GRANTED;
        }
        return true;
    }

    @DebugLog
    public static boolean checkReadExternalStorage(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager
                    .PERMISSION_GRANTED;
        }
        return true;
    }

    @DebugLog
    private static boolean checkNotificationPolicyAccess(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return ManagerUtil.getNotificationManager(context).isNotificationPolicyAccessGranted();
        }
        return true;
    }


    @DebugLog
    public static boolean[] checkInitialPermissions(AppContext context) {
        return new boolean[]{checkCallPhone(context), checkNotificationPolicyAccess(context),
                checkProtectedApps(context)};
    }

    /**
     * This is for Huawei phones. It has built in a "feature" called Protected Apps, that can be
     * accessed from the phone settings (Battery Manager > Protected Apps). This allows elected
     * apps to keep running after the screen is turned off.
     */
    private static boolean checkProtectedApps(AppContext context) {
        if (context.getAppPreferences().isShowProtectedAppsMessage()) {
            Intent intent = new Intent();
            intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize" +
                    ".process.ProtectActivity");
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() == 0;
        }
        return true;
    }


}
