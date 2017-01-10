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

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.jachness.blockcalls.stuff.Util;

import java.util.regex.Pattern;

/**
 * Created by jachness on 12/11/2016.
 */

public final class ValidatorService {
    private final Pattern patternBeginWith = Pattern.compile("^(\\+|\\d)\\d*$");//^(\+|\d)\d*$
    private final Context context;

    public ValidatorService(Context context) {
        this.context = context;
    }

    boolean checkUserInput(String number, String defaultCountryISO) throws NumberParseException {
        PhoneNumber parsed = PhoneNumberUtil.getInstance().parse(number, defaultCountryISO);
        return PhoneNumberUtil.getInstance().isPossibleNumber(parsed);
    }

    public boolean checkUserInput(String number, boolean beginWith) {
        try {
            if (beginWith) {
                return patternBeginWith.matcher(number).matches();
            } else {
                String defaultCountryISO = Util.getDeviceCountryISO(context);
                return this.checkUserInput(number, defaultCountryISO);
            }
        } catch (NumberParseException e) {
            //Do nothing
        }
        return false;
    }
}
