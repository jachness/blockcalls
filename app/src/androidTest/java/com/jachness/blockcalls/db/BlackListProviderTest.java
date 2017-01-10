package com.jachness.blockcalls.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
//@SmallTest
public class BlackListProviderTest extends ProviderTestCase2<BlackListProvider> {
    private PhoneNumberUtil phoneUtil;

    public BlackListProviderTest() {
        super(BlackListProvider.class, BlackListProvider.AUTHORITY);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());
        phoneUtil = PhoneNumberUtil.getInstance();
        super.setUp();
    }

    private PhoneNumber getRandomPhoneNumber() throws NumberParseException {
        int min = 10000000;
        int max = 99999999;

        Random r = new Random();
        int num = r.nextInt(max - min + 1) + min;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        return phoneUtil.parseAndKeepRawInput(Integer.toString(num), "AR");
    }

    @Test
    public void test1() throws NumberParseException {
        PhoneNumber number = getRandomPhoneNumber();
        String numberE164 = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        String numberInt = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat
                .INTERNATIONAL);


        ContentValues contentValue = new ContentValues();
        contentValue.put(BlackListTable.NORMALIZED_NUMBER, numberE164);
        contentValue.put(BlackListTable.DISPLAY_NUMBER, numberInt);
        contentValue.put(BlackListTable.BEGIN_WITH, Boolean.TRUE);
        contentValue.put(BlackListTable.ENABLED, Boolean.TRUE);

        Uri newBlockedNumberUri = getProvider().insert(BlackListTable.CONTENT_URI, contentValue);
        assertThat(newBlockedNumberUri, is(notNullValue()));

        Cursor c = getProvider().query(BlackListTable.CONTENT_URI, null, null, null, null);
        assertThat(c.moveToNext(), is(true));
        c.close();

        int rows = getProvider().delete(newBlockedNumberUri, null, null);
        assertThat(rows, is(1));

        // ======================

        number = getRandomPhoneNumber();
        numberE164 = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        numberInt = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);


        contentValue = new ContentValues();
        contentValue.put(BlackListTable.NORMALIZED_NUMBER, numberE164);
        contentValue.put(BlackListTable.DISPLAY_NUMBER, numberInt);
        contentValue.put(BlackListTable.BEGIN_WITH, Boolean.FALSE);
        contentValue.put(BlackListTable.ENABLED, Boolean.FALSE);

        newBlockedNumberUri = getProvider().insert(BlackListTable.CONTENT_URI, contentValue);
        assertThat(newBlockedNumberUri, is(notNullValue()));

        c = getProvider().query(BlackListTable.CONTENT_URI, null, null, null, null);
        assertThat(c.moveToNext(), is(true));
        c.close();

        rows = getProvider().delete(newBlockedNumberUri, BlackListTable.NORMALIZED_NUMBER + "=?",
                new String[]{numberE164});
        assertThat(rows, is(1));


//        cr.insert(CitizenTable.CONTENT_URI, contentValue);
//        contentValue = new ContentValues();
//        contentValue.put(CitizenTable.NAME, "James Lee");
//        contentValue.put(CitizenTable.STATE, "NY");
//        contentValue.put(CitizenTable.INCOME, 120000);
//        cr.insert(CitizenTable.CONTENT_URI, contentValue);
//        contentValue = new ContentValues();
//        contentValue.put(CitizenTable.NAME, "Daniel Lee");
//        contentValue.put(CitizenTable.STATE, "NY");
//        contentValue.put(CitizenTable.INCOME, 80000);
//        cr.insert(CitizenTable.CONTENT_URI, contentValue);
//// QUERY TABLE FOR ALL COLUMNS AND ROWS
//        Cursor c = cr.query(CitizenTable.CONTENT_URI, null, null, null, CitizenTable.INCOME + "
// ASC");
//// LET THE ACTIVITY MANAGE THE CURSOR
////        startManagingCursor(c);
//        int idCol = c.getColumnIndex(CitizenTable.ID);


//        ContentResolver cr = getProvider().;
//        ContentValues cv = new ContentValues();
//        cv.put(BlackListTable.NORMALIZED_NUMBER, "1154456558");
//        cv.put(BlackListTable.BEGIN_WITH, 1);


//            assertThat(res, is(not(Long.valueOf(-1))));
//            assertThat(c.moveToNext(), is(true));

    }
}

