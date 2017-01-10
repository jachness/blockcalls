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

package com.jachness.blockcalls.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.jachness.blockcalls.db.LogTable;
import com.jachness.blockcalls.stuff.BlockOrigin;

public class LogEntity {
    private Long uid;
    private String callerID;
    private String displayNumber;
    private String displayName;
    private long time;
    private BlockOrigin blockOrigin;

    public LogEntity() {
    }

    public LogEntity(Cursor cur) {
        for (String colName : cur.getColumnNames()) {
            int colIndex = cur.getColumnIndexOrThrow(colName);
            switch (colName) {
                case LogTable.CALLER_ID:
                    this.setCallerID(cur.getString(colIndex));
                    break;
                case LogTable.DISPLAY_NUMBER:
                    this.setDisplayNumber(cur.getString(colIndex));
                    break;
                case LogTable.DATE:
                    this.setTime(cur.getLong(colIndex));
                    break;
                case LogTable.BLOCK_ORIGIN:
                    this.setBlockOrigin(BlockOrigin.valueOf(cur.getString(colIndex)));
                    break;
                case LogTable.UID:
                    this.setUid(cur.getLong(colIndex));
                    break;
                case LogTable.DISPLAY_NAME:
                    this.setDisplayName(cur.getString(colIndex));
                    break;
            }
        }
    }

    public String getCallerID() {
        return callerID;
    }

    public void setCallerID(String callerID) {
        this.callerID = callerID;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDisplayNumber() {
        return displayNumber;
    }

    public void setDisplayNumber(String displayNumber) {
        this.displayNumber = displayNumber;
    }

    public BlockOrigin getBlockOrigin() {
        return blockOrigin;
    }

    public void setBlockOrigin(BlockOrigin blockOrigin) {
        this.blockOrigin = blockOrigin;
    }

    @SuppressWarnings("unused")
    public Long getUid() {
        return uid;
    }

    @SuppressWarnings("WeakerAccess")
    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void toContentValues(@NonNull ContentValues contentValue) {
        if (uid != null) contentValue.put(LogTable.UID, uid);
        contentValue.put(LogTable.CALLER_ID, callerID);
        contentValue.put(LogTable.DISPLAY_NUMBER, displayNumber);
        contentValue.put(LogTable.DISPLAY_NAME, displayName);
        contentValue.put(LogTable.DATE, Long.toString(time));
        contentValue.put(LogTable.BLOCK_ORIGIN, blockOrigin.toString());
    }

    @Override
    public String toString() {
        return "LogEntity{" +
                "blockOrigin=" + blockOrigin +
                ", uid=" + uid +
                ", callerID='" + callerID + '\'' +
                ", displayNumber='" + displayNumber + '\'' +
                ", displayName='" + displayName + '\'' +
                ", time=" + time +
                '}';
    }
}