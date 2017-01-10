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

package com.jachness.blockcalls.db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jachness.blockcalls.BuildConfig;

/**
 * Created by jachness on 4/11/2016.
 */
public class BlackListProvider extends AppProvider {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".blackList";
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int BLACK_LIST = 1;
    private static final int BLACK_LIST_ID = 2;

    static {
        sURIMatcher.addURI(AUTHORITY, BlackListTable.RELATIVE_PATH, BLACK_LIST);
        sURIMatcher.addURI(AUTHORITY, BlackListTable.RELATIVE_PATH + "/#", BLACK_LIST_ID);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[]
            selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sURIMatcher.match(uri)) {
            case BLACK_LIST:
                qb.setTables(BlackListTable.TABLE);
                break;
            case BLACK_LIST_ID:
                qb.setTables(BlackListTable.TABLE);
                String uid = uri.getPathSegments().get(BlackListTable.SSID_PATH_POSITION);
                qb.appendWhere(BlackListTable.UID + "=" + uid);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case BLACK_LIST:
                return BlackListTable.CONTENT_TYPE;
            case BLACK_LIST_ID:
                return BlackListTable.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        switch (sURIMatcher.match(uri)) {
            case BLACK_LIST:
                return insertBlackList(uri, values);
            case BLACK_LIST_ID:
                throw new IllegalArgumentException("Pointless using ID for new inserts: " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private Uri insertBlackList(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(BlackListTable.TABLE, null, values);

        if (rowId > 0) {
            Uri blackListUri = ContentUris.withAppendedId(BlackListTable.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(blackListUri, null);
            return blackListUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        switch (sURIMatcher.match(uri)) {
            case BLACK_LIST:
                return deleteBySelection(uri, selection, selectionArgs);
            case BLACK_LIST_ID:
                return deleteByID(uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private int deleteBySelection(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(BlackListTable.TABLE, selection, selectionArgs);
        if (rows > 0) getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    private int deleteByID(Uri uri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = ContentUris.parseId(uri);
        int rows = db.delete(BlackListTable.TABLE, BlackListTable.UID + "=?", new String[]{Long
                .toString(id)});
        if (rows > 0) getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[]
            selectionArgs) {
        switch (sURIMatcher.match(uri)) {
            case BLACK_LIST:
                throw new IllegalArgumentException("Bulk updates not supported" + uri);
            case BLACK_LIST_ID:
                return updateUniqueBlackList(uri, values);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private int updateUniqueBlackList(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long uid = ContentUris.parseId(uri);
        int rows = db.update(BlackListTable.TABLE, values, BlackListTable.UID + "=" + uid, null);
        if (rows == 1) {
            getContext().getContentResolver().notifyChange(uri, null);
            return rows;
        }
        throw new SQLException("Failed to update row into " + uri + ". Values: " + values
                .toString());
    }
}
