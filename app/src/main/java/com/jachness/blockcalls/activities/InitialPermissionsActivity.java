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

package com.jachness.blockcalls.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jachness.blockcalls.R;
import com.jachness.blockcalls.stuff.AppContext;
import com.jachness.blockcalls.stuff.AppPreferences;
import com.jachness.blockcalls.stuff.PermUtil;
import com.jachness.blockcalls.stuff.Util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 2/1/2017.
 */

public class InitialPermissionsActivity extends AppCompatActivity {
    private static final int PERM_CODE_CALL_PHONE = 0;
    private static final String TAG = InitialPermissionsActivity.class.getSimpleName();
    private Button btOk;
    private AppPreferences appPreferences;
    private boolean[] results;
    private TextView labelText;


    @Override
    @DebugLog
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_permissions);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appPreferences = ((AppContext) getApplicationContext()).getAppPreferences();
        Button btCancel = (Button) findViewById(R.id.initialPermissionsClose);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    InitialPermissionsActivity.this.finishAffinity();
                } else {
                    InitialPermissionsActivity.this.finish();
                }
            }
        });

        btOk = (Button) findViewById(R.id.initialPermissionOk);
        labelText = (TextView) findViewById(R.id.initial_permissions_text);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        results = PermUtil.checkInitialPermissions((AppContext) getApplicationContext());
        if (!results[0]) {
            if (appPreferences.isFirstTimePhonePerm() || shouldShowRequestPermissionRationale
                    (PermUtil.CALL_PHONE)) {
                btOk.setText(R.string.common_grant);
                btOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitialPermissionsActivity.this.requestPermissions(new
                                String[]{PermUtil.CALL_PHONE}, PERM_CODE_CALL_PHONE);
                    }
                });
            } else {
                btOk.setText(R.string.initial_permissions_settings);
                btOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
            }
        } else if (!results[1]) {
            btOk.setText(R.string.common_grant);
            btOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(
                            Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(intent);
                }
            });
        } else if (!results[2]) {
            btOk.setText(R.string.common_allow);
            btOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appPreferences.setShowProtectedAppsMessage(false);
                    runHuaweiProtectedApps();
                }
            });
        } else {
            Intent newActivity = new Intent(this, MainActivity.class);
            startActivity(newActivity);
            finish();
            return;
        }

        if (!results[0] && !results[1]) {
            String message = getResources().getString(R.string.initial_permissions_message_0);
            labelText.setText(Util.fromHtml(message), TextView.BufferType.SPANNABLE);
        } else if (!results[0]) {
            String message = getResources().getString(R.string.initial_permissions_message_1);
            labelText.setText(Util.fromHtml(message), TextView.BufferType.SPANNABLE);
        } else if (!results[1]) {
            String message = getResources().getString(R.string.initial_permissions_message_2);
            labelText.setText(Util.fromHtml(message), TextView.BufferType.SPANNABLE);
        } else if (!results[2]) {
            String message = getResources().getString(R.string
                    .initial_permissions_message_protected_apps, getResources().getString(R
                    .string.app_name));
            labelText.setText(Util.fromHtml(message), TextView.BufferType.SPANNABLE);
        } else {
            throw new RuntimeException("Should no be here");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_CODE_CALL_PHONE) {
            //Permission granted is already checked in onResume() method.
            appPreferences.setFirstTimePhonePerm(false);

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && !results[1]) {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
    }

    private void runHuaweiProtectedApps() {
        String cmd = "am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cmd += " --user " + getUserSerial();
            }
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ignored) {
            Log.w(TAG, "Huawei Protected Apps: exec cmd: " + cmd, ignored);
        }
    }

    @SuppressWarnings("ConfusingArgumentToVarargsMethod")
    private String getUserSerial() {
        //noinspection ResourceType
        Object userManager = getSystemService("user");
        if (null == userManager) return "";

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle",
                    (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[])
                    null);
            Method getSerialNumberForUser = userManager.getClass().getMethod
                    ("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException |
                IllegalAccessException ignored) {
            Log.w(TAG, "Huawei Protected Apps: exception getting user serial", ignored);
        }
        return "";
    }
}
