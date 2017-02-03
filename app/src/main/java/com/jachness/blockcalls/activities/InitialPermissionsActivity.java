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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jachness.blockcalls.R;
import com.jachness.blockcalls.stuff.AppContext;
import com.jachness.blockcalls.stuff.AppPreferences;
import com.jachness.blockcalls.stuff.PermUtil;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 2/1/2017.
 */

public class InitialPermissionsActivity extends AppCompatActivity {
    private static final int PERM_CODE_CALL_PHONE = 0;
    private Button btOk;
    private AppPreferences appPreferences;
    private boolean[] results;
    private TextView labelText;


    @TargetApi(Build.VERSION_CODES.M)
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
                InitialPermissionsActivity.this.finishAffinity();
            }
        });

        btOk = (Button) findViewById(R.id.initialPermissionOk);

        labelText = (TextView) findViewById(R.id.initial_permissions_text);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        results = PermUtil.checkInitialPermissions(this);
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
        } else {
            Intent newActivity = new Intent(this, MainActivity.class);
            startActivity(newActivity);
            finish();
            return;
        }

        if (!results[0] && !results[1]) {
            labelText.setText(R.string.initial_permissions_message_0);
        } else if (!results[0]) {
            labelText.setText(R.string.initial_permissions_message_1);
        } else if (!results[1]) {
            labelText.setText(R.string.initial_permissions_message_2);
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
}
