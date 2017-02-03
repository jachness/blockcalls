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

import com.jachness.blockcalls.exceptions.PhoneNumberException;
import com.jachness.blockcalls.exceptions.TooShortNumberException;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 11/11/2016.
 */

public class MasterChecker {
    private final IChecker[] checkers;

    public MasterChecker(PrivateNumberChecker privateNumberChecker, QuickBlackListChecker
            quickBlackListChecker, BlackListChecker blackListChecker, ContactChecker
                                 contactChecker) {
        this.checkers = new IChecker[]{privateNumberChecker, quickBlackListChecker,
                blackListChecker, contactChecker};
    }

    @DebugLog
    public boolean isBlockable(Call call) throws TooShortNumberException, PhoneNumberException {
        for (IChecker checker : checkers) {
            int res = checker.isBlockable(call);
            switch (res) {
                case IChecker.YES:
                    return true;
                case IChecker.NO:
                    return false;
            }
        }
        return false;
    }

    @DebugLog
    public void doLast() {
        for (IChecker checker : checkers) {
            checker.doLast();
        }
    }

    @DebugLog
    public void refresh() {
        for (IChecker checker : checkers) {
            checker.refresh();
        }
    }
}
