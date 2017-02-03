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
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jachness.blockcalls.androidService.CallBlockingService;
import com.jachness.blockcalls.db.BlackListTable;
import com.jachness.blockcalls.db.dao.BlackListDAO;
import com.jachness.blockcalls.db.dao.ContactDAO;
import com.jachness.blockcalls.entities.BlackListNumberEntity;
import com.jachness.blockcalls.stuff.AppPreferences;
import com.jachness.blockcalls.stuff.PermUtil;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 26/12/2016.
 */

public class BlackListWrapper {
    private static final String TAG = BlackListWrapper.class.getSimpleName();
    private final ValidatorService validatorService;
    private final NormalizerService normalizerService;
    private final Context context;
    private final ContactDAO contactDAO;
    private final BlackListDAO blackListDAO;
    private final AppPreferences appPreferences;

    public BlackListWrapper(Context context, ValidatorService validatorService, NormalizerService
            normalizerService, ContactDAO contactDAO, BlackListDAO blackListDAO, AppPreferences
                                    appPreferences) {
        this.context = context;
        this.normalizerService = normalizerService;
        this.validatorService = validatorService;
        this.contactDAO = contactDAO;
        this.blackListDAO = blackListDAO;
        this.appPreferences = appPreferences;
    }

    @DebugLog
    public boolean checkUserInput(String number, boolean beginWith) {
        return validatorService.checkUserInput(number, beginWith);
    }

    @DebugLog
    public int addNumberToBlackList(@NonNull String number, @Nullable String displayName, boolean
            beginWith) {
        if (this.validatorService.checkUserInput(number, beginWith)) {
            BlackListNumberEntity entity = new BlackListNumberEntity();
            entity.setBeginWith(beginWith);
            entity.setEnabled(true);
            entity.setDisplayName(displayName);

            if (beginWith) {
                entity.setNormalizedNumber(number);
                entity.setDisplayNumber(number + BlackListTable.BEGIN_WITH_SYMBOL);
            } else {
                String formattedNumber[] = normalizerService.normalizeUserInput(number);
                entity.setNormalizedNumber(formattedNumber[0]);
                entity.setDisplayNumber(formattedNumber[1]);
            }
            if (!blackListDAO.existByNormalizedNumber(entity.getNormalizedNumber())) {
                if (!entity.isBeginWith() && PermUtil.checkReadContacts(context)) {
                    String[] contact = contactDAO.findContact(entity.getNormalizedNumber());
                    if (contact != null) {
                        entity.setDisplayName(contact[1]);
                    }
                }

                ContentValues contentValue = new ContentValues();
                entity.toContentValues(contentValue);
                Uri newUri = context.getContentResolver().insert(BlackListTable.CONTENT_URI,
                        contentValue);
                if (newUri == null) {
                    Log.e(TAG, "Couldn't insert BlackListNumberEntity: " + entity.toString());
                }
                return 1;
            }
        }
        return 0;
    }

    @DebugLog
    public void delete(BlackListNumberEntity entity) {
        blackListDAO.delete(entity);
        refreshService();
    }

    @DebugLog
    public void updateEnabled(BlackListNumberEntity entity, boolean enabled) {
        blackListDAO.updateEnabled(entity, enabled);
        refreshService();
    }

    @DebugLog
    public void deleteAll() {
        context.getContentResolver().delete(BlackListTable.CONTENT_URI, null, null);
        refreshService();
    }

    private void refreshService() {
        if (appPreferences.isBlockingEnable()) {
            Intent i = new Intent(context, CallBlockingService.class);
            i.putExtra(CallBlockingService.DRY, true);
            context.startService(i);
        }
    }
}
