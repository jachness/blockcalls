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

package com.jachness.blockcalls.broadcasts;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.jachness.blockcalls.BuildConfig;
import com.jachness.blockcalls.androidService.CallBlockingService;
import com.jachness.blockcalls.stuff.AppContext;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 9/12/2016.
 */
public class WakeUpReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = WakeUpReceiver.class.getSimpleName();

    @Override
    @DebugLog
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            boolean blockingEnabled = ((AppContext) context.getApplicationContext())
                    .getAppPreferences().isBlockingEnable();
            if (blockingEnabled) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Starting service " + CallBlockingService.class.getSimpleName());
                }
                Intent startServiceIntent = new Intent(context, CallBlockingService.class);
                startServiceIntent.putExtra(CallBlockingService.WAKEUP, 1);
                startServiceIntent.putExtra(CallBlockingService.DRY, true);
                startWakefulService(context, startServiceIntent);
            }
        }
    }
}