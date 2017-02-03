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

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import com.jachness.blockcalls.AndroidTest;
import com.jachness.blockcalls.db.BlackListTable;
import com.jachness.blockcalls.db.QuickBlackListTable;
import com.jachness.blockcalls.entities.BlackListNumberEntity;
import com.jachness.blockcalls.stuff.BlockOrigin;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by jachness on 2/2/2017.
 */
@RunWith(AndroidJUnit4.class)
//@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class QuickBlackListCheckerTest extends AndroidTest {
    private static final String TAG = QuickBlackListCheckerTest.class.getSimpleName();

    @Inject
    BlackListWrapper blackListWrapper;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        getComponent().inject(this);
        getAppPreferences().deleteAll();
    }

    @Test
    public void testFistTime() {
        deleteAll();

        BlackListNumberEntity dummy = new BlackListNumberEntity();
        dummy.setBeginWith(false);
        dummy.setEnabled(true);
        dummy.setDisplayName("test9");
        dummy.setNormalizedNumber("98978978978");
        dummy.setDisplayNumber("98978978978");
        insert(dummy);

        BlackListNumberEntity entity = new BlackListNumberEntity();
        entity.setBeginWith(false);
        entity.setEnabled(true);
        entity.setDisplayName("test1");
        entity.setNormalizedNumber("1231234567");
        entity.setDisplayNumber("1231234567");

        insert(entity);
        QuickBlackListChecker checker = new QuickBlackListChecker(getContext(), getAppPreferences
                ());
        checker.refresh();
        Call call = new Call();
        call.setNumber("1234567");

        int res = checker.isBlockable(call);
        assertTrue(res == IChecker.NONE);
        assertFalse(BlockOrigin.BLACK_LIST.equals(call.getBlockOrigin()));
        assertFalse(call.getExtraData().containsKey("displayName"));

        getAppPreferences().setQuick(entity.getUid() + ":" + call.getNumber());
        checker.doLast();

        call = new Call();
        call.setNumber("1234567");

        res = checker.isBlockable(call);
        assertTrue(res == IChecker.YES);
        assertTrue(BlockOrigin.BLACK_LIST.equals(call.getBlockOrigin()));
        assertTrue(call.getExtraData().containsKey("displayName"));
        assertTrue(call.getExtraData().containsValue("test1"));

        entity.setEnabled(false);
        update(entity);
        checker.refresh();

        call = new Call();
        call.setNumber("1234567");

        res = checker.isBlockable(call);
        assertTrue(res == IChecker.NONE);
        assertFalse(BlockOrigin.BLACK_LIST.equals(call.getBlockOrigin()));
        assertFalse(call.getExtraData().containsKey("displayName"));

        call = new Call();
        call.setNumber("1234567");

        entity.setDisplayName(null);
        entity.setEnabled(true);
        update(entity);
        checker.refresh();
        res = checker.isBlockable(call);
        assertTrue(res == IChecker.YES);
        assertTrue(BlockOrigin.BLACK_LIST.equals(call.getBlockOrigin()));
        assertTrue(TextUtils.isEmpty(call.getExtraData().get("displayName")));

    }

    private void insert(BlackListNumberEntity entity) {

        ContentValues contentValue = new ContentValues();
        entity.toContentValues(contentValue);
        Uri newUri = getContext().getContentResolver().insert(BlackListTable.CONTENT_URI,
                contentValue);
        entity.setUid(ContentUris.parseId(newUri));
    }

    private void update(BlackListNumberEntity entity) {

        ContentValues contentValue = new ContentValues();
        entity.toContentValues(contentValue);
        getContext().getContentResolver().update(ContentUris.withAppendedId(BlackListTable
                .CONTENT_URI, entity.getUid()), contentValue, null, null);
    }

    private void deleteAll() {
        getContext().getContentResolver().delete(QuickBlackListTable.CONTENT_URI,
                null, null);
        getContext().getContentResolver().delete(BlackListTable.CONTENT_URI,
                null, null);
    }
}
