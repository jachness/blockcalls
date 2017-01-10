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

import android.text.TextUtils;

import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.jachness.blockcalls.stuff.BlockOrigin;

import java.util.Map;

/**
 * Created by jachness on 6/11/2016.
 */
public class Call {
    public static final String PRIVATE_NUMBER = "private";

    private String number;
    private BlockOrigin blockOrigin;
    private PhoneNumber normalizedNumber;
    private Map<String, String> extraData;

    public BlockOrigin getBlockOrigin() {
        return blockOrigin;
    }

    public void setBlockOrigin(BlockOrigin blockOrigin) {
        this.blockOrigin = blockOrigin;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public PhoneNumber getNormalizedNumber() {
        return normalizedNumber;
    }

    public void setNormalizedNumber(PhoneNumber normalizedNumber) {
        this.normalizedNumber = normalizedNumber;
    }

    public boolean isPrivateNumber() {
        return TextUtils.isEmpty(number);
    }

    public Map<String, String> getExtraData() {
        return extraData;
    }

    public void setExtraData(Map<String, String> extraData) {
        this.extraData = extraData;
    }

    public String getDisplayNumber() {
        if (this.isPrivateNumber()) {
            return PRIVATE_NUMBER;
        }
        return getNumber();
    }

    @Override
    public String toString() {
        return "Call{" +
                "blockOrigin=" + blockOrigin +
                ", number='" + number + '\'' +
                ", normalizedNumber=" + normalizedNumber +
                ", extraData=" + extraData +
                '}';
    }
}
