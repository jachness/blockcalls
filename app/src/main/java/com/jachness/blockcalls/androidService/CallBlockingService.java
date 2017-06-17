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

package com.jachness.blockcalls.androidService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.jachness.blockcalls.BuildConfig;
import com.jachness.blockcalls.R;
import com.jachness.blockcalls.exceptions.PhoneNumberException;
import com.jachness.blockcalls.exceptions.TooShortNumberException;
import com.jachness.blockcalls.services.BlockWrapper;
import com.jachness.blockcalls.stuff.AppContext;
import com.jachness.blockcalls.stuff.Util;

import javax.inject.Inject;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 9/12/2016.
 */

public class CallBlockingService extends Service {
    public static final String DRY = "dry";
    public static final String WAKEUP = "wakeup";
    public static final String INCOMING_NUMBER = "number";
    private static final String TAG = CallBlockingService.class.getSimpleName();
    private static final int NOTIFICATION_BLOCKED_NUMBER = R.string.blocked_number;
    @Inject
    BlockWrapper blockWrapper;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Start foreground
     */
    @Override
    @DebugLog
    public void onCreate() {
        super.onCreate();
        ((AppContext) getApplicationContext()).getDagger().inject(this);
        NotificationCompat.Builder not = new NotificationCompat.Builder(this).setContentTitle
                (getResources().getString(R.string.blocking_service_started)).setOngoing(true)
                //Content intent is added to satisfy android 2.x requirements
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new
                        Intent(), 0));
        startForeground(R.string.blocking_service_started, not.build());
    }

    @Override
    @DebugLog
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    @DebugLog
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent == null || intent.getExtras() == null) {
            return Service.START_STICKY;
        }
        if (intent.getExtras().containsKey(WAKEUP)) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Waking up CallBlockingService at startup");
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
        if (intent.getExtras().containsKey(DRY)) {
            //Dry: Do nothing but runs one time just to let the blockWrapper be "ready" for the
            // next call
            String numberExample = Util.getNumberExample(getApplicationContext());
            try {
                blockWrapper.checkAndBlock(true, numberExample);
            } catch (TooShortNumberException e) {
                Log.w(TAG, "(dry) number example: " + numberExample, e);
            } catch (PhoneNumberException e) {
                Log.w(TAG, "(dry) number example: " + numberExample, e);
            }
            return Service.START_STICKY;
        }

        if (intent.getExtras().containsKey(INCOMING_NUMBER)) {
            final String incomingNumber = intent.getExtras().getString(INCOMING_NUMBER);

            if (BuildConfig.DEBUG) Log.d(TAG, "IncomingNumber: " + incomingNumber);

            try {
                final String formattedNumber = blockWrapper.checkAndBlock(false, incomingNumber);
                if (formattedNumber != null && ((AppContext) getApplicationContext())
                        .getAppPreferences().isNotificationBlockedCall()) {
                    if (BuildConfig.DEBUG) Log.i(TAG, "Blocked number: " + incomingNumber);
                    final Notification not = new NotificationCompat.Builder(this)
                            .setTicker(formattedNumber)
                            .setContentTitle(getString(R.string.notification_blocked_title))
                            .setContentText(formattedNumber)
                            .setSmallIcon(R.drawable.ic_stat_call_blocked)
                            .setAutoCancel(true)
                            //Content intent is added to satisfy android 2.x requirements
                            .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0))
                            .build();
                    if (BuildConfig.DEBUG) Log.d(TAG, not.toString());
                    NotificationManager mNotificationManager = (NotificationManager)
                            getSystemService(Context
                                    .NOTIFICATION_SERVICE);
                    mNotificationManager.notify(NOTIFICATION_BLOCKED_NUMBER, not);
                }

            } catch (PhoneNumberException e) {
                Log.w(TAG, "Incoming number: {" + incomingNumber + "}", e);
            } catch (TooShortNumberException e) {
                Log.w(TAG, "Incoming number: {" + incomingNumber + "}", e);
            }
        }

        return Service.START_STICKY;
    }
}
