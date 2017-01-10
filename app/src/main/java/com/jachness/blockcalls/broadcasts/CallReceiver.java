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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.jachness.blockcalls.androidService.CallBlockingService;
import com.jachness.blockcalls.stuff.AppContext;
import com.jachness.blockcalls.stuff.Util;

import hugo.weaving.DebugLog;

import static com.jachness.blockcalls.BuildConfig.DEBUG;

/**
 * Created by jachness on 27/9/2016.
 */
public class CallReceiver extends BroadcastReceiver {
    private static final String TAG = CallReceiver.class.getSimpleName();

    @Override
    @DebugLog
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {

                boolean blockingEnabled = ((AppContext) context.getApplicationContext())
                        .getAppPreferences().isBlockingEnable();
                if (blockingEnabled) {
                    String incomingNumber = intent.getStringExtra(TelephonyManager
                            .EXTRA_INCOMING_NUMBER);

                    Log.i(TAG, "Incoming number: {" + incomingNumber + "}");
                    if (DEBUG) {
                        Log.d(TAG, "Ringing number: " + incomingNumber);
                        Log.d(TAG, "Running " + CallBlockingService.class.getSimpleName() + ": " +
                                Util.isServiceRunning(context, CallBlockingService.class));
                    }
                    Intent i = new Intent(context, CallBlockingService.class);
                    i.putExtra(CallBlockingService.INCOMING_NUMBER, incomingNumber);
                    context.startService(i);
                }
            }
        }
    }
}