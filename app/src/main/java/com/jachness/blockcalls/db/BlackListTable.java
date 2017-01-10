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

import android.net.Uri;

/**
 * Created by jachness on 21/10/2016.
 */
public class BlackListTable {
    public static final String UID = "_id";
    public static final String NORMALIZED_NUMBER = "normalized_number";
    public static final String DISPLAY_NUMBER = "display_number";
    public static final String DISPLAY_NAME = "display_name";
    public static final String BEGIN_WITH = "begin_with";
    public static final String ENABLED = "enabled";

    public static final String TABLE = "black_list";
    public static final String RELATIVE_PATH = "blacklist";

    public static final Uri CONTENT_URI = Uri.parse("content://" + BlackListProvider.AUTHORITY +
            "/" + RELATIVE_PATH);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + RELATIVE_PATH;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + RELATIVE_PATH;

    public static final int SSID_PATH_POSITION = 1;
    public static final String BEGIN_WITH_SYMBOL = "?";
}
