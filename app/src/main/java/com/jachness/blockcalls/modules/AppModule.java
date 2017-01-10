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
import com.jachness.blockcalls.services.BlackListWrapper;
import com.jachness.blockcalls.services.ImportExportWrapper;
import com.jachness.blockcalls.services.NormalizerService;
import com.jachness.blockcalls.services.ValidatorService;
import com.jachness.blockcalls.stuff.AppPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jachness on 11/11/2016.
 */
@Module
public class AppModule {
    private final Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    AppPreferences providesSettings() {
        return new AppPreferences(context);
    }

    @Provides
    @Singleton
    ValidatorService providerValidatorService() {
        return new ValidatorService(context);
    }

    @Provides
    @Singleton
    NormalizerService providerNormalizerService() {
        return new NormalizerService(context);
    }

    @Provides
    @Singleton
    ImportExportWrapper provideImportExportWrapper(BlackListWrapper blackListWrapper) {
        return new ImportExportWrapper(context, blackListWrapper);
    }

    @Provides
    @Singleton
    BlackListWrapper provideBlackListWrapper(ValidatorService validatorService, NormalizerService
            normalizerService, ContactDAO contactDAO, BlackListDAO blackListDAO) {
        return new BlackListWrapper(context, validatorService, normalizerService, contactDAO,
                blackListDAO);
    }
}
