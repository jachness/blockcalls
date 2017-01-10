package com.jachness.blockcalls.services;

import com.jachness.blockcalls.AndroidTest;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertFalse;

/**
 * Created by jachness on 13/11/2016.
 */

@SuppressWarnings("TryWithIdenticalCatches")
public class EndCallServiceTest extends AndroidTest {
    @Inject

    EndCallService endCallService;
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
        assertFalse(endCallService.endCall());
    }
}
