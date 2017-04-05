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

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.jachness.blockcalls.BuildConfig;
import com.jachness.blockcalls.R;
import com.jachness.blockcalls.stuff.PermUtil;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 21/11/2016.
 */

public class AddActivity extends AppCompatActivity {
    public static final String FRAGMENT_CONTACTS = "contacts";
    public static final String FRAGMENT_MANUAL = "manual";
    public static final String FRAGMENT_CALLLOG = "calllog";
    public static final String FRAGMENT_KEY = "fragment_key";

    private static final String TAG = AddActivity.class.getSimpleName();
    private static final String TAG_FRAGMENT = "TAG_FRAGMENT";

    private Fragment fragment;

    @Override
    @DebugLog
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!hasGrantedPermissions()) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Finish activity due to lack of permissions");
            finish();
            return;
        }
        setContentView(R.layout.add_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.addToolBar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            String val = getIntent().getExtras().getString(FRAGMENT_KEY);
            switch (val) {
                case FRAGMENT_CONTACTS:
                    fragment = new AddContactFragment();
                    setTitle(R.string.contactTitle);
                    break;
                case FRAGMENT_MANUAL:
                    fragment = new AddManualFragment();
                    setTitle(R.string.add_manual_title);
                    break;
                case FRAGMENT_CALLLOG:
                    fragment = new AddCallLogFragment();
                    setTitle(R.string.call_log_title);
                    break;
                default:
                    throw new RuntimeException("Should not be here");
            }
        } else {
            fragment = getSupportFragmentManager().findFragmentByTag(savedInstanceState.getString
                    (TAG_FRAGMENT));
        }
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        tr.replace(R.id.fragment_container, fragment, TAG_FRAGMENT);
        tr.commit();

    }


    @Override
    @DebugLog
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != fragment) {
            outState.putString(TAG_FRAGMENT, fragment.getTag());
        }
    }

    @Override
    @DebugLog
    protected void onRestart() {
        super.onRestart();
        if (!hasGrantedPermissions()) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Finish activity due to contact permission revoked");
            finish();
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean hasGrantedPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String val = getIntent().getExtras().getString(FRAGMENT_KEY);
            if (val.equals(FRAGMENT_CONTACTS)) {
                return PermUtil.checkReadContacts(this);
            } else if (val.equals(FRAGMENT_CALLLOG)) {
                return PermUtil.checkCallPhone(this);
            }
        }
        return true;
    }
}
