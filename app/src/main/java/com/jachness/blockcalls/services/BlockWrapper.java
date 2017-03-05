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

package com.jachness.blockcalls.services;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.jachness.blockcalls.BuildConfig;
import com.jachness.blockcalls.db.LogTable;
import com.jachness.blockcalls.entities.LogEntity;
import com.jachness.blockcalls.exceptions.PhoneNumberException;
import com.jachness.blockcalls.exceptions.TooShortNumberException;
import com.jachness.blockcalls.stuff.AppPreferences;
import com.jachness.blockcalls.stuff.ManagerUtil;

import java.util.Date;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 26/12/2016.
 */

public class BlockWrapper {

    private static final String TAG = BlockWrapper.class.getSimpleName();
    private final NormalizerService normalizerService;
    private final MasterChecker masterChecker;
    private final Context context;
    private final EndCallService endCallService;
    private final AppPreferences appPreferences;

    public BlockWrapper(Context context, MasterChecker masterChecker, EndCallService
            endCallService, NormalizerService normalizerService, AppPreferences appPreferences) {
        this.context = context;
        this.masterChecker = masterChecker;
        this.normalizerService = normalizerService;
        this.endCallService = endCallService;
        this.appPreferences = appPreferences;
    }

    @DebugLog
    public String checkAndBlock(boolean dry, String number) throws TooShortNumberException,
            PhoneNumberException {
        Call call = new Call();
        call.setNumber(number);

        boolean blockable = masterChecker.isBlockable(call);
        String formattedNumber = null;
        if (blockable && !dry) {
            int prev = mute();
            boolean res = endCallService.endCall();
            unMute(prev);

            normalizerService.normalizeCall(call);
            formattedNumber = normalizerService.getDisplayNumber(call);
            if (res && appPreferences.isEnableLog()) {
                saveLog(call);
            }
            masterChecker.doLast();
        }
        if (dry) {
            masterChecker.refresh();
        }
        return formattedNumber;
    }

    @DebugLog
    private void saveLog(Call call) {
        LogEntity logEntity = new LogEntity();
        logEntity.setCallerID(call.getNumber());
        logEntity.setDisplayNumber(normalizerService.getDisplayNumber(call));
        if (call.getExtraData().containsKey("displayName")) {
            logEntity.setDisplayName(call.getExtraData().get("displayName"));
        }
        logEntity.setTime((new Date()).getTime());
        logEntity.setBlockOrigin(call.getBlockOrigin());

        ContentValues contentValue = new ContentValues();
        logEntity.toContentValues(contentValue);

        Uri newUri = context.getContentResolver().insert(LogTable.CONTENT_URI, contentValue);
        if (newUri == null) {
            Log.e(TAG, "Couldn't insert LogEntity: " + logEntity.toString());
            throw new RuntimeException("Couldn't insert LogEntity: " + logEntity.toString());
        }
    }

    @DebugLog
    private int mute() {
        NotificationManager notificationManager = ManagerUtil.getNotificationManager(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager
                .isNotificationPolicyAccessGranted()) {
            return -1;
        }

        AudioManager audioManager = ManagerUtil.getAudioManager(context);
        int ringerMode = audioManager.getRingerMode();
        if (ringerMode != AudioManager.RINGER_MODE_SILENT) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Actual ringer mode: " + ringerMode);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            return ringerMode;
        }
        return -1;
    }

    @DebugLog
    private void unMute(int previousMode) {
        if (previousMode == -1) return;

        try {
            //A delay is introduced as sometimes Android does not restore the previous mode
            //correctly. This delay seems to work.
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //do nothing
        }

        AudioManager audioManager = ManagerUtil.getAudioManager(context);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Should be silent ringer mode: " + audioManager.getRingerMode());
            Log.d(TAG, "Restituting ringer mode: " + previousMode);
        }

        audioManager.setRingerMode(previousMode);
    }

}
