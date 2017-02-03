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

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.jachness.blockcalls.R;
import com.jachness.blockcalls.androidService.CallBlockingService;
import com.jachness.blockcalls.stuff.AppContext;
import com.jachness.blockcalls.stuff.AppPreferences;
import com.jachness.blockcalls.stuff.PermUtil;

/**
 * Created by jachness on 14/11/2016.
 */

@SuppressWarnings("deprecation")
public class BlockingSettingsActivity extends PreferenceActivity implements Preference
        .OnPreferenceChangeListener {
    private CheckBoxPreference pref;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.blocking_preferences);
        pref = (CheckBoxPreference) getPreferenceManager().findPreference(AppPreferences
                .ALLOW_ONLY_CONTACTS);
        pref.setOnPreferenceChangeListener(this);

        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                AppContext appContext = (AppContext) getApplicationContext();

                if (key.equals(AppPreferences.BLOCKING_ENABLED)) {
                    Intent i = new Intent(BlockingSettingsActivity.this, CallBlockingService.class);
                    if (appContext.getAppPreferences().isBlockingEnable()) {
                        i.putExtra(CallBlockingService.DRY, true);
                        startService(i);
                    } else {
                        stopService(i);
                    }
                }
            }
        };
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean newVal = (boolean) newValue;
        if (newVal && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!PermUtil.checkReadContacts(this)) {
                requestPermissions(new String[]{PermUtil.READ_CONTACTS}, 0);
                return false;
            }
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pref.setChecked(true);
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                boolean request = shouldShowRequestPermissionRationale(Manifest.permission
                        .READ_CONTACTS);
                if (!request) {
                    Toast.makeText(this, getString(R.string.common_contact_permission_message),
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }
}
