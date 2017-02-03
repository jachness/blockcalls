package com.jachness.blockcalls.services;

import android.content.ContentValues;
import android.net.Uri;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.jachness.blockcalls.AndroidTest;
import com.jachness.blockcalls.db.BlackListTable;
import com.jachness.blockcalls.db.dao.BlackListDAO;
import com.jachness.blockcalls.entities.BlackListNumberEntity;
import com.jachness.blockcalls.exceptions.PhoneNumberException;
import com.jachness.blockcalls.exceptions.TooShortNumberException;
import com.jachness.blockcalls.stuff.AppPreferences;
import com.jachness.blockcalls.stuff.BlockOrigin;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)
//@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class BlackListCheckerTest extends AndroidTest {
    private static final String TAG = BlackListCheckerTest.class.getSimpleName();
    @Inject

    BlackListChecker checker;
    @Inject

    BlackListDAO blackListDAO;
    @Inject

    NormalizerService normalizerService;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        getComponent().inject(this);
        getAppPreferences().deleteAll();
    }

    @Test
    public void testBeginWith() throws PhoneNumberException, TooShortNumberException {
        Log.i(TAG, "Init");

    }

    @Test
    public void testIsBlockable() throws PhoneNumberException, TooShortNumberException {
        Log.i(TAG, "Init");

        List<String> numbersMatch = new ArrayList<>();
        List<String> numbersNotMatch = new ArrayList<>();
        numbersMatch.add("+541148556078");
        numbersMatch.add("+5448556078");
        numbersMatch.add("+5458556078");
        numbersNotMatch.add("+5448551234");
        insertData(numbersMatch, numbersNotMatch);

        AppPreferences sett = new AppPreferences(getTargetContext());
        sett.setEnableBlackList(false);
        Call call = new Call();
        call.setNumber("48556078");
        call.setCountryISO("AR");
        normalizerService.normalizeCall(call);
        int res = checker.isBlockable(call);
        Assert.assertEquals(IChecker.NONE, res);
        Assert.assertEquals(null, call.getBlockOrigin());


        sett.setEnableBlackList(true);
        call.setNumber("48556078");
        call.setCountryISO("AR");
        normalizerService.normalizeCall(call);
        res = checker.isBlockable(call);
        Assert.assertEquals(IChecker.YES, res);
        Assert.assertEquals(BlockOrigin.BLACK_LIST, call.getBlockOrigin());

        call.setNumber("99999999");
        call.setCountryISO("AR");
        normalizerService.normalizeCall(call);
        res = checker.isBlockable(call);
        Assert.assertEquals(IChecker.NONE, res);
        Assert.assertEquals(null, call.getBlockOrigin());

    }

    @Test
    public void testMethodFindInBlackList() throws PhoneNumberException, TooShortNumberException {
        Log.i(TAG, "Init");

        List<String> numbersMatch = new ArrayList<>();
        List<String> numbersNotMatch = new ArrayList<>();
        numbersMatch.add("+541148556078");
        numbersMatch.add("+5448556078");
        numbersMatch.add("+5458556078");
        numbersNotMatch.add("+5448551234");
        insertData(numbersMatch, numbersNotMatch);

        Call call = new Call();
        call.setNumber("48556078");
        call.setCountryISO("AR");
        normalizerService.normalizeCall(call);

        List<BlackListNumberEntity> list = blackListDAO.findForBlock(call);
        Assert.assertTrue(list.size() > 0);

        List<String> databaseNumbers = new ArrayList<>();
        for (BlackListNumberEntity entity : list) {
            databaseNumbers.add(entity.getNormalizedNumber());
        }

        Assert.assertTrue(databaseNumbers.containsAll(numbersMatch));
        Assert.assertFalse(databaseNumbers.containsAll(numbersNotMatch));


    }

    private void insertData(List<String> matches, List<String> notMatches) {

        getTargetContext().getContentResolver().delete(BlackListTable.CONTENT_URI, null, null);
        for (String num : matches) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(BlackListTable.NORMALIZED_NUMBER, num);
            contentValue.put(BlackListTable.BEGIN_WITH, Boolean.FALSE);
            contentValue.put(BlackListTable.ENABLED, Boolean.TRUE);

            Uri newBlockedNumberUri = getTargetContext().getContentResolver().insert(BlackListTable
                    .CONTENT_URI, contentValue);
            assertThat(newBlockedNumberUri, is(notNullValue()));
        }

        for (String num : notMatches) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(BlackListTable.NORMALIZED_NUMBER, num);
            contentValue.put(BlackListTable.BEGIN_WITH, Boolean.FALSE);
            contentValue.put(BlackListTable.ENABLED, Boolean.TRUE);

            Uri newBlockedNumberUri = getTargetContext().getContentResolver().insert(BlackListTable
                    .CONTENT_URI, contentValue);
            assertThat(newBlockedNumberUri, is(notNullValue()));
        }
    }
}