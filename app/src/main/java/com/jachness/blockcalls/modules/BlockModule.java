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

package com.jachness.blockcalls.modules;

import android.content.Context;

import com.jachness.blockcalls.db.dao.BlackListDAO;
import com.jachness.blockcalls.db.dao.ContactDAO;
import com.jachness.blockcalls.services.BlackListChecker;
import com.jachness.blockcalls.services.BlockWrapper;
import com.jachness.blockcalls.services.ContactChecker;
import com.jachness.blockcalls.services.EndCallService;
import com.jachness.blockcalls.services.MasterChecker;
import com.jachness.blockcalls.services.MatcherService;
import com.jachness.blockcalls.services.NormalizerService;
import com.jachness.blockcalls.services.PrivateNumberChecker;
import com.jachness.blockcalls.services.QuickBlackListChecker;
import com.jachness.blockcalls.stuff.AppPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jachness on 11/11/2016.
 */
@Module
public class BlockModule {
    private final Context context;

    public BlockModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    PrivateNumberChecker providesPrivateNumberChecker(AppPreferences preferences) {
        return new PrivateNumberChecker(preferences);
    }

    @Provides
    @Singleton
    QuickBlackListChecker providesQuickBlackListChecker(AppPreferences preferences) {
        return new QuickBlackListChecker(context, preferences);
    }

    @Provides
    @Singleton
    BlackListChecker providesBlackListChecker(AppPreferences preferences, MatcherService
            matcherService, BlackListDAO blackListDAO, NormalizerService normalizerService) {
        return new BlackListChecker(preferences, matcherService, blackListDAO, normalizerService);
    }

    @Provides
    @Singleton
    ContactChecker providesContactChecker(AppPreferences preferences, ContactDAO contactDAO) {
        return new ContactChecker(context, preferences, contactDAO);
    }

    @Provides
    @Singleton
    MasterChecker providesMasterChecker(PrivateNumberChecker privateNumberChecker,
                                        QuickBlackListChecker quickBlackListChecker,
                                        BlackListChecker blackListChecker,
                                        ContactChecker contactChecker) {
        return new MasterChecker(privateNumberChecker, quickBlackListChecker, blackListChecker,
                contactChecker);
    }

    @Provides
    @Singleton
    MatcherService providerMatcherService() {
        return new MatcherService();
    }

    @Provides
    @Singleton
    EndCallService provideEndCallService() {
        return new EndCallService(context);
    }

    @Provides
    @Singleton
    BlockWrapper provideBlockWrapper(MasterChecker masterChecker, EndCallService
            endCallService, NormalizerService normalizerService, AppPreferences appPreferences) {
        return new BlockWrapper(context, masterChecker, endCallService, normalizerService,
                appPreferences);
    }

}
