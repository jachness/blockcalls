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

import android.support.annotation.NonNull;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.jachness.blockcalls.entities.BlackListNumberEntity;

import hugo.weaving.DebugLog;


/**
 * Created by jachness on 12/11/2016.
 */

public class MatcherService {
    private static final int NO_MATCH = 0;
    private static final int SOFT_MATCH = 1;
    private static final int STRICT_MATCH = 2;

    @DebugLog
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean isNumberMatch(@NonNull Call call, BlackListNumberEntity blackListNumberEntity) {
        if (call.getNumber() == null || call.getNormalizedNumber() == null) {
            return false;
        }

        if (blackListNumberEntity.isBeginWith()) {
            //This is already checked on db query. If there is any 'beginWith' entity, it means a
            // matching number
            return true;
        }

        int res = check(call, blackListNumberEntity.getNormalizedNumber());
        return res == SOFT_MATCH || res == STRICT_MATCH;
    }

    @DebugLog
    private int check(Call call, String otherNumber) {
        PhoneNumberUtil.MatchType res = PhoneNumberUtil.getInstance().isNumberMatch(call
                .getNormalizedNumber(), otherNumber);
        if (res.equals(PhoneNumberUtil.MatchType.NSN_MATCH) || res.equals(PhoneNumberUtil
                .MatchType.EXACT_MATCH)) {
            return STRICT_MATCH;
        } else if (res.equals(PhoneNumberUtil.MatchType.SHORT_NSN_MATCH)) {
            return SOFT_MATCH;
        }

        return NO_MATCH;
    }
}
