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
 * Created by jachness on 2/2/2017.
 */

public class QuickBlackListProvider extends AppProvider {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".quickBlackList";
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int QUICK_BLACK_LIST = 1;
    private static final int QUICK_BLACK_LIST_ID = 2;
    private static final int QUICK_BLACK_LIST_JOIN = 3;

    static {
        sURIMatcher.addURI(AUTHORITY, QuickBlackListTable.RELATIVE_PATH, QUICK_BLACK_LIST);
        sURIMatcher.addURI(AUTHORITY, QuickBlackListTable.RELATIVE_PATH + "/#",
                QUICK_BLACK_LIST_ID);
        sURIMatcher.addURI(AUTHORITY, QuickBlackListTable.RELATIVE_PATH + "/join",
                QUICK_BLACK_LIST_JOIN);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[]
            selectionArgs,
                        String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sURIMatcher.match(uri)) {
            case QUICK_BLACK_LIST:
                qb.setTables(QuickBlackListTable.TABLE);
                break;
            case QUICK_BLACK_LIST_ID:
                qb.setTables(QuickBlackListTable.TABLE);
                String uid = uri.getPathSegments().get(QuickBlackListTable.SSID_PATH_POSITION);
                qb.appendWhereEscapeString(QuickBlackListTable.UID + "=" + uid);
                break;
            case QUICK_BLACK_LIST_JOIN:
                StringBuilder sb = new StringBuilder();
                sb.append(QuickBlackListTable.TABLE);
                sb.append(" INNER JOIN ");
                sb.append(BlackListTable.TABLE);
                sb.append(" ON (");
                sb.append(QuickBlackListTable.TABLE + "." + QuickBlackListTable.UID);
                sb.append(" = ");
                sb.append(BlackListTable.TABLE + "." + BlackListTable.UID);
                sb.append(")");
                qb.setTables(sb.toString());
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
            case QUICK_BLACK_LIST:
                return QuickBlackListTable.CONTENT_TYPE;
            case QUICK_BLACK_LIST_ID:
                return QuickBlackListTable.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        switch (sURIMatcher.match(uri)) {
            case QUICK_BLACK_LIST:
                return insertValues(uri, values);
            case QUICK_BLACK_LIST_ID:
                throw new IllegalArgumentException("Pointless using ID for new inserts: " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private Uri insertValues(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(QuickBlackListTable.TABLE, null, values);

        if (rowId > 0) {
            Uri quickBlackListUri = ContentUris.withAppendedId(QuickBlackListTable.CONTENT_URI,
                    rowId);
            getContext().getContentResolver().notifyChange(quickBlackListUri, null);
            return quickBlackListUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        switch (sURIMatcher.match(uri)) {
            case QUICK_BLACK_LIST:
                return deleteBySelection(uri, selection, selectionArgs);
            case QUICK_BLACK_LIST_ID:
                return deleteByID(uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private int deleteByID(Uri uri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = ContentUris.parseId(uri);
        int rows = db.delete(QuickBlackListTable.TABLE, QuickBlackListTable.UID + "=?", new
                String[]{Long.toString(id)});
        if (rows > 0) getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    private int deleteBySelection(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(QuickBlackListTable.TABLE, selection, selectionArgs);
        if (rows > 0) getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[]
            selectionArgs) {
        throw new SQLException("Updates are not supported. " + uri);
    }
}
