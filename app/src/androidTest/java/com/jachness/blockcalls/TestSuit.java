package com.jachness.blockcalls;

/**
 * Created by jachness on 12/11/2016.
 */

import com.jachness.blockcalls.db.BlackListProviderTest;
import com.jachness.blockcalls.db.LogProviderTest;
import com.jachness.blockcalls.services.BlackListCheckerTest;
import com.jachness.blockcalls.services.ContactCheckerTest;
import com.jachness.blockcalls.services.MasterCheckerTest;
import com.jachness.blockcalls.services.MatcherServiceTest;
import com.jachness.blockcalls.services.PrivateNumberCheckerTest;
import com.jachness.blockcalls.stuff.AppPreferencesTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({BlackListCheckerTest.class, ContactCheckerTest.class, MasterCheckerTest
        .class, MatcherServiceTest.class,
        PrivateNumberCheckerTest.class,
        LogProviderTest.class, BlackListProviderTest.class,
        AppPreferencesTest.class})
public class TestSuit {
}
