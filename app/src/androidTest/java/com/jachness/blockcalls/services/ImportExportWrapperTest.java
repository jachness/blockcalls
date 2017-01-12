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

import android.support.test.runner.AndroidJUnit4;

import com.jachness.blockcalls.AndroidTest;
import com.jachness.blockcalls.exceptions.FileException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Created by jachness on 13/11/2016.
 */
@RunWith(AndroidJUnit4.class)
public class ImportExportWrapperTest extends AndroidTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void importBlackListTest() {
        BlackListWrapper blackListWrapper = mock(BlackListWrapper.class);
        when(blackListWrapper.addNumberToBlackList(Matchers.any(String.class), Matchers.any
                (String.class), Matchers.any(Boolean.class))).thenReturn(1);

        ImportExportWrapper wrapper = new ImportExportWrapper(getContext(), blackListWrapper);
        try {
            wrapper.importBlackList(null);
            Assert.fail("Should have had throw FileException exception");
        } catch (FileException e) {
            Assert.assertEquals(e.getMessage(), "Uri is null");
            Assert.assertEquals(e.getErrorCode(), FileException.READ_FILE_ERROR);
        }
    }
}
