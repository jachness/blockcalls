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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.jachness.blockcalls.db.BlackListTable;
import com.jachness.blockcalls.db.QuickBlackListTable;
import com.jachness.blockcalls.stuff.AppPreferences;
import com.jachness.blockcalls.stuff.BlockOrigin;
import com.jachness.blockcalls.stuff.Util;

import java.util.ArrayList;

import hugo.weaving.DebugLog;

import static android.content.ContentValues.TAG;

/**
 * Created by jachness on 2/2/2017.
 */

public class QuickBlackListChecker implements IChecker {
    private final Context context;
    private final AppPreferences appPreferences;
    private ArrayList<String> callerIdList = new ArrayList<>();
    private ArrayList<String> displayNameList = new ArrayList<>();

    public QuickBlackListChecker(Context context, AppPreferences appPreferences) {
        this.context = context;
        this.appPreferences = appPreferences;
    }

    @Override
    @DebugLog
    public int isBlockable(Call call) {
        final boolean enableBlackList = appPreferences.isEnableBlackList();
        if (!call.isPrivateNumber() && enableBlackList) {
            int index = callerIdList.indexOf(call.getNumber());
            if (index >= 0) {
                call.getExtraData().put("displayName", displayNameList.get(index));
                call.setBlockOrigin(BlockOrigin.BLACK_LIST);
                return IChecker.YES;
            }

        }
        return IChecker.NONE;
    }

    @Override
    public void doLast() {
        String number = appPreferences.getQuick();
        if (!TextUtils.isEmpty(number)) {
            String[] split = number.split(":");
            if (!exist(split[0], split[1])) {
                insertNew(split[0], split[1]);
                appPreferences.setQuick(AppPreferences.DEFAULT_QCK);
                this.refresh();
            }
        }
    }

    @Override
    public void refresh() {
        callerIdList = new ArrayList<>();
        displayNameList = new ArrayList<>();

        //Delete deprecated rows
        context.getContentResolver().delete(QuickBlackListTable.CONTENT_URI, QuickBlackListTable
                .UID + " NOT IN (select " + BlackListTable.UID + " from " + BlackListTable.TABLE
                + ")", null);

        //Load numbers
        Cursor cursor = null;
        try {
            String inclusion = BlackListTable.ENABLED + "=1";

            cursor = context.getContentResolver().query(Uri.withAppendedPath(QuickBlackListTable
                            .CONTENT_URI, "/join"), new String[]{QuickBlackListTable.CALLER_ID,
                            BlackListTable.DISPLAY_NAME},
                    inclusion, null, QuickBlackListTable.CALLER_ID + " ASC");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int indexCaller = cursor.getColumnIndex(QuickBlackListTable.CALLER_ID);
                    int indexDisplayName = cursor.getColumnIndex(BlackListTable.DISPLAY_NAME);
                    callerIdList.add(cursor.getString(indexCaller));
                    displayNameList.add(cursor.getString(indexDisplayName));
                }
            }
        } finally {
            Util.close(cursor);
        }
    }

    private boolean exist(String id, String callerId) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(QuickBlackListTable.CONTENT_URI, new
                            String[]{QuickBlackListTable.UID, QuickBlackListTable.CALLER_ID},
                    QuickBlackListTable.UID + "=? and " + QuickBlackListTable.CALLER_ID + "=?",
                    new String[]{id, callerId}, null);
            return cursor != null && cursor.getCount() == 1;
        } finally {
            Util.close(cursor);
        }
    }

    private void insertNew(String id, String callerId) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(QuickBlackListTable.UID, id);
        contentValue.put(QuickBlackListTable.CALLER_ID, callerId);

        Uri newUri = context.getContentResolver().insert(QuickBlackListTable.CONTENT_URI,
                contentValue);
        if (newUri == null) {
            Log.e(TAG, "Couldn't insert into quick_black_list");
            throw new RuntimeException("Couldn't insert into quick_black_list");
        }
    }
}
