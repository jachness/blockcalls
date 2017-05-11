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

import android.app.Application;

import com.jachness.blockcalls.modules.AllComponent;
import com.jachness.blockcalls.modules.AppModule;
import com.jachness.blockcalls.modules.BlockModule;
import com.jachness.blockcalls.modules.DAOModule;
import com.jachness.blockcalls.modules.DaggerAllComponent;
import com.jachness.blockcalls.services.BlackListWrapper;
import com.jachness.blockcalls.services.ImportExportWrapper;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by jachness on 10/11/2016.
 */
public class AppContext extends Application {
    public static AllComponent dagger;
    @Inject
    AppPreferences appPreferences;
    @Inject
    Lazy<ImportExportWrapper> importExportWrapper;
    @Inject
    Lazy<BlackListWrapper> blackListWrapper;

    @Override
    public void onCreate() {
        super.onCreate();
        //If DaggerAllComponent gives an error, just rebuild the project
        dagger = DaggerAllComponent.builder().blockModule(new BlockModule(this)).appModule(new
                AppModule(this)).dAOModule(new DAOModule(this)).build();
        dagger.inject(this);

    }

    public AppPreferences getAppPreferences() {
        return appPreferences;
    }

    public ImportExportWrapper getImportExportWrapper() {
        return importExportWrapper.get();
    }

    public BlackListWrapper getBlackListWrapper() {
        return blackListWrapper.get();
    }


}
