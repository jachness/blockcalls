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
import com.jachness.blockcalls.entities.LogEntity;
import com.jachness.blockcalls.stuff.BlockOrigin;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
//@SmallTest
public class LogProviderTest extends ProviderTestCase2<LogProvider> {
    private PhoneNumberUtil phoneUtil;


    public LogProviderTest() {
        super(LogProvider.class, LogProvider.AUTHORITY);
    }


    @Override
//    @Before
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


        LogEntity logEntity = new LogEntity();
        logEntity.setCallerID(numberE164);
        logEntity.setDisplayNumber(numberInt);
        logEntity.setBlockOrigin(BlockOrigin.CONTACTS);
        logEntity.setTime((new Date()).getTime());

        ContentValues contentValue = new ContentValues();
        logEntity.toContentValues(contentValue);


        Uri newBlockedNumberUri = getProvider().insert(LogTable.CONTENT_URI, contentValue);
        assertThat(newBlockedNumberUri, is(notNullValue()));

        Cursor c = getProvider().query(LogTable.CONTENT_URI, null, null, null, null);
        assertThat(c.moveToNext(), is(true));
        LogEntity logEntityStored = new LogEntity(c);
        c.close();

        assertEquals(logEntity.getCallerID(), logEntityStored.getCallerID());
        assertEquals(logEntity.getTime(), logEntityStored.getTime());
        assertEquals(logEntity.getDisplayNumber(), logEntityStored.getDisplayNumber());


        int rows = getProvider().delete(newBlockedNumberUri, null, null);
        assertThat(rows, is(1));

        // ======================
        // Test individual delete

        number = getRandomPhoneNumber();
        numberE164 = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        numberInt = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);


        long time = (new Date()).getTime();
        logEntity = new LogEntity();
        logEntity.setCallerID(numberE164);
        logEntity.setDisplayNumber(numberInt);
        logEntity.setTime(time);
        logEntity.setBlockOrigin(BlockOrigin.CONTACTS);

        contentValue = new ContentValues();
        logEntity.toContentValues(contentValue);

        newBlockedNumberUri = getProvider().insert(LogTable.CONTENT_URI, contentValue);
        assertThat(newBlockedNumberUri, is(notNullValue()));

        c = getProvider().query(LogTable.CONTENT_URI, null, null, null, null);
        assertThat(c.moveToNext(), is(true));
        c.close();

        rows = getProvider().delete(newBlockedNumberUri,
                LogTable.CALLER_ID + "=? " + LogTable.DISPLAY_NUMBER + "=? " + LogTable.DATE +
                        "=? ",
                new String[]{numberE164, numberInt, Long.toString(time)});
        assertThat(rows, is(1));

        // ======================
        // Test delete all

        number = getRandomPhoneNumber();
        numberE164 = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        numberInt = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);


        time = (new Date()).getTime();
        logEntity = new LogEntity();
        logEntity.setCallerID(numberE164);
        logEntity.setDisplayNumber(numberInt);
        logEntity.setTime(time);
        logEntity.setBlockOrigin(BlockOrigin.CONTACTS);

        contentValue = new ContentValues();
        logEntity.toContentValues(contentValue);

        newBlockedNumberUri = getProvider().insert(LogTable.CONTENT_URI, contentValue);
        assertThat(newBlockedNumberUri, is(notNullValue()));

        c = getProvider().query(LogTable.CONTENT_URI, null, null, null, null);
        assertThat(c.moveToNext(), is(true));
        c.close();

        rows = getProvider().delete(newBlockedNumberUri, null, null);
        assertThat(rows, is(1));

    }
}

