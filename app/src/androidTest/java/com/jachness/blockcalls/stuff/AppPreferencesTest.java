package com.jachness.blockcalls.stuff;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.jachness.blockcalls.AndroidTest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AppPreferencesTest extends AndroidTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        getAppPreferences().deleteAll();
    }

    @Test
    public void test1() {

        AppPreferences set = getAppPreferences();

        Assert.assertTrue(set.isFirstTime());
        Assert.assertTrue(set.isEnableBlackList());

        Assert.assertFalse(set.isAllowOnlyContacts());
        Assert.assertFalse(set.isBlockPrivateNumbers());
        //--

        set.setFirstTime(false);
        set.setEnableBlackList(false);
        set.setAllowOnlyContacts(true);
        set.setBlockPrivateNumbers(true);

        Assert.assertFalse(set.isFirstTime());
        Assert.assertFalse(set.isEnableBlackList());

        Assert.assertTrue(set.isAllowOnlyContacts());
        Assert.assertTrue(set.isBlockPrivateNumbers());
    }
}
