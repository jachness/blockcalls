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

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jachness on 26/12/2016.
 */
@Module
public class DAOModule {

    private final Context context;

    public DAOModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    ContactDAO providesContactDAO() {
        return new ContactDAO(context);
    }

    @Provides
    @Singleton
    BlackListDAO providesBlackListDAO() {
        return new BlackListDAO(context);
    }
}
