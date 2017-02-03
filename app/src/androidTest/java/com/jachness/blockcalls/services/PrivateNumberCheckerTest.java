package com.jachness.blockcalls.services;

import android.support.test.runner.AndroidJUnit4;

import com.jachness.blockcalls.AndroidTest;
import com.jachness.blockcalls.exceptions.PhoneNumberException;
import com.jachness.blockcalls.exceptions.TooShortNumberException;
import com.jachness.blockcalls.stuff.AppPreferences;
import com.jachness.blockcalls.stuff.BlockOrigin;
import com.jachness.blockcalls.stuff.Util;

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
public class PrivateNumberCheckerTest extends AndroidTest {
    private static final String TAG = PrivateNumberCheckerTest.class.getSimpleName();
    @Inject

    PrivateNumberChecker checker;
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
            Call call = new Call();
            call.setCountryISO(Util.getDeviceCountryISO(getTargetContext()));
            normalizerService.normalizeCall(call);

            int res = checker.isBlockable(call);
            Assert.assertEquals(IChecker.NONE, res);
            Assert.assertEquals(null, call.getBlockOrigin());

            AppPreferences sett = new AppPreferences(getTargetContext());
            sett.setBlockPrivateNumbers(true);

            res = checker.isBlockable(call);
            Assert.assertEquals(IChecker.YES, res);
            Assert.assertEquals(BlockOrigin.PRIVATE, call.getBlockOrigin());

            call.setNumber("1154914711");
            normalizerService.normalizeCall(call);
            res = checker.isBlockable(call);
            Assert.assertEquals(IChecker.NONE, res);
            Assert.assertEquals(null, call.getBlockOrigin());


        } catch (PhoneNumberException e) {
            Assert.fail(e.getMessage());
        } catch (TooShortNumberException e) {
            Assert.fail(e.getMessage());
        }
    }
}
