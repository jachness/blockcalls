package com.jachness.blockcalls.services;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.jachness.blockcalls.AndroidTest;
import com.jachness.blockcalls.exceptions.PhoneNumberException;
import com.jachness.blockcalls.exceptions.TooShortNumberException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by jachness on 13/11/2016.
 */
@RunWith(AndroidJUnit4.class)
public class MainWrapperTest extends AndroidTest {
    private static final String TAG = MainWrapperTest.class.getSimpleName();


    @Before
    public void setUp() throws Exception {
        super.setUp();
        getAppPreferences().deleteAll();

    }

    @Test
    public void testCheckAndBlock() throws NumberParseException, PhoneNumberException,
            TooShortNumberException {
        Log.i(TAG, "Init");

//        MasterChecker masterChecker = mock(MasterChecker.class);
//        EndCallService endCallService = mock(EndCallService.class);
//        ContactDAO contactDAO = mock(ContactDAO.class);
//
//        when(masterChecker.isBlockable(Matchers.any(Call.class))).thenReturn(true);
//        when(endCallService.endCall()).thenReturn(true);
//        when(contactDAO.findContact(Matchers.any(String.class))).thenReturn(null);
//
//        BlockWrapper mainWrapper = new BlockWrapper(getContext(), masterChecker,
//                endCallService, new NormalizerService(getContext()), new AppPreferences
//                (getContext()));
//
//        String res = mainWrapper.checkAndBlock(false, "1234");
//        Log.i(TAG, "Response: " + res);
//        assertNotNull(res);
    }
}
