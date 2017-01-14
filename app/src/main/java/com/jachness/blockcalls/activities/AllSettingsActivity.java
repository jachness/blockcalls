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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;

import com.jachness.blockcalls.R;
import com.jachness.blockcalls.stuff.AppContext;
import com.jachness.blockcalls.stuff.AppPreferences;

/**
 * Created by jachness on 14/11/2016.
 */

@SuppressWarnings("deprecation")
public class AllSettingsActivity extends PreferenceActivity {
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

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
