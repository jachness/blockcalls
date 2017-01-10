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

import com.jachness.blockcalls.db.dao.BlackListDAO;
import com.jachness.blockcalls.entities.BlackListNumberEntity;
import com.jachness.blockcalls.stuff.AppPreferences;
import com.jachness.blockcalls.stuff.BlockOrigin;

import java.util.HashMap;
import java.util.List;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 11/11/2016.
 */

public class BlackListChecker implements IChecker {
    private final AppPreferences appPreferences;
    private final MatcherService matcherService;
    private final BlackListDAO blackListDAO;

    public BlackListChecker(AppPreferences appPreferences, MatcherService matcherService,
                            BlackListDAO
                                    blackListDAO) {
        this.appPreferences = appPreferences;
        this.matcherService = matcherService;
        this.blackListDAO = blackListDAO;
    }

    @Override
    @DebugLog
    public int isBlockable(Call call) {
        final boolean enableBlackList = appPreferences.isEnableBlackList();
        if (!call.isPrivateNumber() && enableBlackList) {
            final List<BlackListNumberEntity> list = blackListDAO.findForBlock(call);
            for (BlackListNumberEntity entity : list) {
                boolean match = matcherService.isNumberMatch(call, entity);
                if (match) {
                    if (call.getExtraData() == null) {
                        call.setExtraData(new HashMap<String, String>());
                    }
                    call.getExtraData().put("displayName", entity.getDisplayName());
                    call.setBlockOrigin(BlockOrigin.BLACK_LIST);
                    return IChecker.YES;
                }
            }
        }
        return IChecker.NONE;
    }
}
