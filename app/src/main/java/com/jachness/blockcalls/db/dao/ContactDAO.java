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

package com.jachness.blockcalls.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.jachness.blockcalls.stuff.Util;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 26/12/2016.
 */

public class ContactDAO {
    private final Context context;

    public ContactDAO(Context context) {
        this.context = context;
    }

    @DebugLog
    public String[] findContact(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                .encode(number));
        Cursor cursor = context.getContentResolver().query(uri,
                new String[]{ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup
                        .DISPLAY_NAME},
                null, null, null);

        try {
            String contact[] = null;
            if (cursor != null && cursor.moveToNext()) {
                int displayNameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup
                        .DISPLAY_NAME);
                int numberIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.NUMBER);
                contact = new String[]{cursor.getString(numberIndex), cursor.getString
                        (displayNameIndex)};
            }
            return contact;
        } finally {
            Util.close(cursor);
        }
    }
}
