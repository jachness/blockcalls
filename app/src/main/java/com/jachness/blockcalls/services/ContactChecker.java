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

import android.content.Context;

import com.jachness.blockcalls.db.dao.ContactDAO;
import com.jachness.blockcalls.stuff.AppPreferences;
import com.jachness.blockcalls.stuff.BlockOrigin;
import com.jachness.blockcalls.stuff.PermUtil;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 11/11/2016.
 */

public class ContactChecker implements IChecker {
    private final Context context;
    private final AppPreferences appPreferences;
    private final ContactDAO contactDAO;

    public ContactChecker(Context context, AppPreferences appPreferences, ContactDAO contactDAO) {
        this.context = context;
        this.appPreferences = appPreferences;
        this.contactDAO = contactDAO;
    }

    @Override
    @DebugLog
    public int isBlockable(Call call) {
        final boolean allowOnlyContacts = appPreferences.isAllowOnlyContacts();
        if (allowOnlyContacts && PermUtil.checkReadContacts(context)) {
            call.setBlockOrigin(BlockOrigin.CONTACTS);
            if (!call.isPrivateNumber()) {
                if (contactDAO.findContact(call.getNumber()) != null) {
                    return IChecker.NO;
                }
            }
            return IChecker.YES;
        }
        return IChecker.NONE;
    }

    @Override
    public void doLast() {
        //do nothing
    }

    @Override
    public void refresh() {
        //do nothing
    }
}
