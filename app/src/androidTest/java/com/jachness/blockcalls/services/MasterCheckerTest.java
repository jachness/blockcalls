package com.jachness.blockcalls.services;

import android.support.test.runner.AndroidJUnit4;

import com.jachness.blockcalls.AndroidTest;
import com.jachness.blockcalls.exceptions.PhoneNumberException;
import com.jachness.blockcalls.exceptions.TooShortNumberException;
import com.jachness.blockcalls.stuff.AppPreferences;
import com.jachness.blockcalls.stuff.BlockOrigin;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

/**
 * Created by jachness on 11/11/2016.
 */
@SuppressWarnings("TryWithIdenticalCatches")
@RunWith(AndroidJUnit4.class)
public class MasterCheckerTest extends AndroidTest {
    private static final String TAG = MasterCheckerTest.class.getSimpleName();
    @Inject

    MasterChecker checker;
    @Inject

    NormalizerService normalizerService;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        getComponent().inject(this);
        getAppPreferences().deleteAll();
    }

    @Test
    public void test() {
        try {
            Call call = normalizerService.normalizeCallerID("");


            Assert.assertFalse(checker.isBlockable(call));
            Assert.assertEquals(null, call.getBlockOrigin());

            AppPreferences sett = new AppPreferences(getContext());
            sett.setBlockPrivateNumbers(true);

            Assert.assertTrue(checker.isBlockable(call));
            Assert.assertEquals(BlockOrigin.PRIVATE, call.getBlockOrigin());

            call = normalizerService.normalizeCallerID("1154914711");
            Assert.assertFalse(checker.isBlockable(call));
            Assert.assertEquals(null, call.getBlockOrigin());

            //TODO test blacklist and contact


        } catch (PhoneNumberException e) {
            Assert.fail(e.getMessage());
        } catch (TooShortNumberException e) {
            Assert.fail(e.getMessage());
        }
    }
}
