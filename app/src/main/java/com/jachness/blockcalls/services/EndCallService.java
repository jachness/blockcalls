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
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.jachness.blockcalls.stuff.ManagerUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 13/11/2016.
 */

@SuppressWarnings("TryWithIdenticalCatches")
public class EndCallService {
    private static final String GET_I_TELEPHONY = "getITelephony";
    private static final String TAG = EndCallService.class.getSimpleName();
    private final Context context;
    private final Method method;

    @SuppressWarnings("unchecked")
    public EndCallService(Context context) {
        this.context = context;
        try {
            Class clazz = Class.forName(TelephonyManager.class.getName());
            method = clazz.getDeclaredMethod(GET_I_TELEPHONY);
            method.setAccessible(true);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @DebugLog
    public boolean endCall() {
        try {
            TelephonyManager tm = ManagerUtil.getTelephonyManager(context);
            ITelephony telephonyService = (ITelephony) method.invoke(tm);
            return telephonyService.endCall();
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
