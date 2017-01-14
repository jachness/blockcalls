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

package com.jachness.blockcalls.stuff;

import android.annotation.SuppressLint;
import android.content.Context;
import android.preference.PreferenceManager;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 29/10/2016.
 */
@SuppressWarnings("SameParameterValue")
public class AppPreferences {

    public static final String ALLOW_ONLY_CONTACTS = "allow_only_contacts_key";
    public static final String BLOCKING_ENABLED = "blocking_enabled";
    public static final String LESSON_1 = "lesson_1";
    private static final String BLOCK_PRIVATE_NUMBERS = "block_private_numbers_key";
    private static final String ENABLE_BLACK_LIST = "enable_black_list_key";
    private static final String FIRST_TIME = "first_time_key";
    private static final String FIRST_TIME_PHONE_PERM = "first_time_phone_perm_key";
    private static final String STRICT_MATCHING = "strict_matching_key";
    private static final String ENABLE_LOG = "enable_log_key";
    private static final String NOTIFICATION_BLOCKED_CALL = "notification_blocked_call";
    private static final boolean DEFAULT_FIR = true;
    private static final boolean DEFAULT_BUN = false;
    private static final boolean DEFAULT_AOC = false;
    private static final boolean DEFAULT_EBL = true;
    private static final boolean DEFAULT_SM = false;
    private static final boolean DEFAULT_EL = true;
    private static final boolean DEFAULT_BE = true;
    private static final boolean DEFAULT_NBC = true;
    private static final boolean DEFAULT_FTPP = true;
    private static final boolean DEFAULT_L1 = false;
    private final Context context;

    public AppPreferences(Context context) {
        this.context = context;
    }

    public boolean isAllowOnlyContacts() {
        return get(ALLOW_ONLY_CONTACTS, DEFAULT_AOC);
    }

    public void setAllowOnlyContacts(boolean allowOnlyContacts) {
        set(ALLOW_ONLY_CONTACTS, allowOnlyContacts);
    }

    public boolean isBlockPrivateNumbers() {
        return get(BLOCK_PRIVATE_NUMBERS, DEFAULT_BUN);
    }

    public void setBlockPrivateNumbers(boolean blockPrivateNumbers) {
        set(BLOCK_PRIVATE_NUMBERS, blockPrivateNumbers);
    }

    public boolean isEnableBlackList() {
        return get(ENABLE_BLACK_LIST, DEFAULT_EBL);
    }

    public void setEnableBlackList(boolean enableBlackList) {
        set(ENABLE_BLACK_LIST, enableBlackList);
    }

    public boolean isFirstTime() {
        return get(FIRST_TIME, DEFAULT_FIR);
    }

    public void setFirstTime(boolean firstTime) {
        set(FIRST_TIME, firstTime);
    }

    public boolean isStrictMatching() {
        return get(STRICT_MATCHING, DEFAULT_SM);
    }

    public void setStrictMatching(boolean strictMatching) {
        set(STRICT_MATCHING, strictMatching);
    }

    public boolean isEnableLog() {
        return get(ENABLE_LOG, DEFAULT_EL);
    }

    @SuppressWarnings("WeakerAccess")
    public void setEnableLog(boolean enableLog) {
        set(ENABLE_LOG, enableLog);
    }

    public boolean isBlockingEnable() {
        return get(BLOCKING_ENABLED, DEFAULT_BE);
    }

    @SuppressWarnings("WeakerAccess")
    public void setBlockingEnable(boolean enable) {
        set(BLOCKING_ENABLED, enable);
    }

    public boolean isNotificationBlockedCall() {
        return get(NOTIFICATION_BLOCKED_CALL, DEFAULT_NBC);
    }

    @SuppressWarnings("WeakerAccess")
    public void setNotificationBlockedCall(boolean enable) {
        set(NOTIFICATION_BLOCKED_CALL, enable);
    }

    public boolean isFirstTimePhonePerm() {
        return get(FIRST_TIME_PHONE_PERM, DEFAULT_FTPP);
    }

    public void setFirstTimePhonePerm(boolean enable) {
        set(FIRST_TIME_PHONE_PERM, enable);
    }

    public boolean isLesson1() {
        return get(LESSON_1, DEFAULT_L1);
    }

    public void setLesson1(boolean enable) {
        set(LESSON_1, enable);
    }



    @SuppressLint("CommitPrefEdits")
    private void set(String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value)
                .commit();
    }

    @DebugLog
    private boolean get(String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaultValue);
    }

    @SuppressLint("CommitPrefEdits")
    public void deleteAll() {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();
    }

    public void setAllDefaults() {
        this.setBlockPrivateNumbers(DEFAULT_BUN);
        this.setAllowOnlyContacts(DEFAULT_AOC);
        this.setEnableBlackList(DEFAULT_EBL);
        this.setStrictMatching(DEFAULT_SM);
        this.setEnableLog(DEFAULT_EL);
        this.setBlockingEnable(DEFAULT_BE);
        this.setNotificationBlockedCall(DEFAULT_NBC);
    }
}
