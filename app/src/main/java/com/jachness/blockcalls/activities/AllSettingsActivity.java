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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jachness.blockcalls.R;
import com.jachness.blockcalls.stuff.AppContext;
import com.jachness.blockcalls.stuff.AppPreferences;

import de.psdev.licensesdialog.LicensesDialog;

/**
 * Created by jachness on 14/11/2016.
 */


public class AllSettingsActivity extends PreferenceActivity {
    private static final String TAG = AllSettingsActivity.class.getSimpleName();
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection deprecation
        addPreferencesFromResource(R.xml.preferences);

        @SuppressWarnings("deprecation") Preference submitDebugLog = this.findPreference
                ("pref_submit_debug_logs");

        @SuppressWarnings("deprecation") Preference openSourceLicenses = this.findPreference
                ("pref_open_source_licenses");

        submitDebugLog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final Intent intent = new Intent(AllSettingsActivity.this, SendLogActivity.class);
                startActivity(intent);
                return true;
            }
        });
        submitDebugLog.setSummary(getVersion());

        openSourceLicenses.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                new LicensesDialog.Builder(AllSettingsActivity.this)
                        .setNotices(R.raw.notices)
                        .build()
                        .show();
                return true;
            }
        });

        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                AppContext appContext = (AppContext) getApplicationContext();
                if (key.equals(AppPreferences.LESSON_1)) {
                    if (!appContext.getAppPreferences().isLesson1()) {
                        finish();
                    }
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        //noinspection deprecation
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //noinspection deprecation
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }


    @NonNull
    private String getVersion() {
        try {
            String app = this.getString(R.string.app_name);
            String version = this.getPackageManager().getPackageInfo(this.getPackageName(),
                    0).versionName;

            return String.format("%s %s", app, version);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, e);
            return this.getString(R.string.app_name);
        }
    }
}
