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
import android.support.annotation.NonNull;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.jachness.blockcalls.R;
import com.jachness.blockcalls.exceptions.PhoneNumberException;
import com.jachness.blockcalls.exceptions.TooShortNumberException;
import com.jachness.blockcalls.stuff.Util;

/**
 * Created by jachness on 4/11/2016.
 */
public class NormalizerService {
    private final Context context;
    private PhoneNumber holder;

    public NormalizerService(@NonNull Context context) {
        this.context = context;
        String number = null;
        try {
            number = Util.getNumberExample(context);
            //This holder is reused to decrease object creation
            this.holder = PhoneNumberUtil.getInstance().parseAndKeepRawInput(number, null);
        } catch (NumberParseException e) {
            throw new RuntimeException("Error parsing number: " + number, e);
        }
    }

    String[] normalizeUserInput(String number) {
        String defaultCountryISO = Util.getDeviceCountryISO(context);
        try {
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            PhoneNumber phoneNumber = phoneNumberUtil.parse(number, defaultCountryISO);
            String e164 = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat
                    .E164);
            String international = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil
                    .PhoneNumberFormat.INTERNATIONAL);
            return new String[]{e164, international};
        } catch (NumberParseException e) {
            throw new IllegalArgumentException("Should be validated before call this method", e);
        }
    }

    public void normalizeCall(@NonNull Call call) throws PhoneNumberException,
            TooShortNumberException {

        if (call.isPrivateNumber() || call.getNormalizedNumber() != null) {
            return;
        }

        if (call.getCountryISO() == null) {
            call.setCountryISO(Util.getDeviceCountryISO(context));
        }

        String number = call.getNumber();
        try {
            PhoneNumberUtil.getInstance().parseAndKeepRawInput(number, call.getCountryISO(),
                    holder);

            String nationalNumber = Long.toString(holder.getNationalNumber());
            if (nationalNumber.length() < TooShortNumberException.MINIMUM_LENGTH) {
                throw new TooShortNumberException("Too short national number: " + nationalNumber + ". Minimum length" +
                        " is " + TooShortNumberException.MINIMUM_LENGTH);
            }

            call.setNormalizedNumber(holder);
        } catch (NumberParseException e) {
            throw new PhoneNumberException("Error parsing number: " + number, e);
        }

    }

    public String getDisplayNumber(Call call) {
        if (call.isPrivateNumber()) {
            return context.getString(R.string.common_private);
        } else {
            return PhoneNumberUtil.getInstance().format(call.getNormalizedNumber(), PhoneNumberUtil
                    .PhoneNumberFormat.INTERNATIONAL);
        }
    }

}
