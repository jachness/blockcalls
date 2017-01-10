package com.jachness.blockcalls.services;

import android.annotation.SuppressLint;
import android.preference.PreferenceManager;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.jachness.blockcalls.AndroidTest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(AndroidJUnit4.class)
//@RunWith(MockitoJUnitRunner.class)
public class ValidatorServiceTest extends AndroidTest {
    private static final String TAG = ValidatorServiceTest.class.getSimpleName();

    @Inject

    ValidatorService validatorService;

    @SuppressLint("CommitPrefEdits")
    @Before
    public void setUp() throws Exception {
        super.setUp();
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().clear().commit();
        getComponent().inject(this);
    }

    @Test
    public void isValidPhoneNumberTest() {
        Assert.assertTrue(isValid("+541148556045", "AR"));
        Assert.assertTrue(isValid("541148556045", "AR"));
        Assert.assertTrue(isValid("1148556045", "AR"));
        Assert.assertTrue(isValid("48556045", "AR"));
        Assert.assertTrue(isValid("+541148556045", ""));
        Assert.assertTrue(isValid("+541148556045", null));
        Assert.assertTrue(isValid("+541148556045", "abc"));

        Assert.assertFalse(isValid("541148556045", ""));
        Assert.assertFalse(isValid("541148556045", null));
        Assert.assertFalse(isValid("541148556045", "abc"));
        Assert.assertTrue(isValid("541148556045", "AR"));

        Assert.assertFalse(isValid("1148556045", ""));
        Assert.assertFalse(isValid("1148556045", null));
        Assert.assertFalse(isValid("1148556045", "abc"));
        Assert.assertTrue(isValid("1148556045", "AR"));

        Assert.assertFalse(isValid("48556045", ""));
        Assert.assertFalse(isValid("48556045", null));
        Assert.assertFalse(isValid("48556045", "abc"));
        Assert.assertTrue(isValid("48556045", "AR"));
    }

    @Test
    public void isValidBeginWithNumberTest() {
        Assert.assertTrue(isValid("+541148556045"));
        Assert.assertTrue(isValid("541148556045"));
        Assert.assertTrue(isValid("1148556045"));
        Assert.assertTrue(isValid("48556045"));
        Assert.assertTrue(isValid("+541148556045"));
        Assert.assertTrue(isValid("+541148556045"));
        Assert.assertTrue(isValid("+541148556045"));


        Assert.assertFalse(isValid("e1148556045"));
        Assert.assertFalse(isValid("11*48556045"));
        Assert.assertFalse(isValid("e11485#56045"));
        Assert.assertFalse(isValid("#e1148556045"));
    }

    @SuppressWarnings("EmptyCatchBlock")
    private boolean isValid(String callerID, String defaultRegion) {
        boolean res = false;
        try {
            res = validatorService.checkUserInput(callerID, defaultRegion);
        } catch (NumberParseException e) {
        }
        Log.i(TAG, callerID + " | " + defaultRegion + " | " + " | [" + res + "]");
        return res;
    }

    private boolean isValid(String callerID) {
        boolean res = validatorService.checkUserInput(callerID, true);
        Log.i(TAG, callerID + " | " + " | [" + res + "]");
        return res;
    }


}



