package com.jachness.blockcalls.services;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.jachness.blockcalls.AndroidTest;
import com.jachness.blockcalls.db.dao.ContactDAO;
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

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ContactCheckerTest extends AndroidTest {
    private static final String TAG = ContactCheckerTest.class.getSimpleName();
    @Inject

    ContactChecker checker;
    @Inject

    ContactDAO contactDAO;
    @Inject

    NormalizerService normalizerService;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        getComponent().inject(this);
        getAppPreferences().deleteAll();

    }

    @Override
    protected Context getTargetContext() {
        return InstrumentationRegistry.getContext();
    }

    @Test
    public void test4Digitos() throws RemoteException, OperationApplicationException,
            PhoneNumberException, TooShortNumberException {
        Log.i(TAG, "Init");

        List<String> numbersMatch = new ArrayList<>();
        List<String> numbersNotMatch = new ArrayList<>();
        numbersNotMatch.add("6078");
        insertData(numbersMatch, numbersNotMatch);

        AppPreferences sett = new AppPreferences(getTargetContext());
        sett.setAllowOnlyContacts(true);

        Call call = new Call();
        call.setNumber("48556078");
        call.setCountryISO("AR");
        normalizerService.normalizeCall(call);
        int res = checker.isBlockable(call);
        Assert.assertEquals(IChecker.YES, res);
        Assert.assertEquals(BlockOrigin.CONTACTS, call.getBlockOrigin());

        call.setNumber("99999999");
        call.setCountryISO("AR");
        normalizerService.normalizeCall(call);
        res = checker.isBlockable(call);
        Assert.assertEquals(IChecker.YES, res);
        Assert.assertEquals(BlockOrigin.CONTACTS, call.getBlockOrigin());
    }

    @Test
    public void testIsBlockable() throws NumberParseException, RemoteException,
            OperationApplicationException,
            PhoneNumberException, TooShortNumberException {
        Log.i(TAG, "Init");

        List<String> numbersMatch = new ArrayList<>();
        List<String> numbersNotMatch = new ArrayList<>();
        numbersMatch.add("+541148556078");
        numbersMatch.add("+5448556078");
        numbersMatch.add("+5458556078");
        numbersNotMatch.add("+5448551234");
        insertData(numbersMatch, numbersNotMatch);

        //NOT Allow only contacts (that's means allow any number)
        AppPreferences sett = new AppPreferences(getTargetContext());
        sett.setAllowOnlyContacts(false);

        Call call = new Call();
        call.setNumber("48556078");
        call.setCountryISO("AR");

        normalizerService.normalizeCall(call);
        int res = checker.isBlockable(call);
        Assert.assertEquals(IChecker.NONE, res);
        Assert.assertEquals(null, call.getBlockOrigin());

        call.setNumber("99999999");
        call.setCountryISO("AR");
        normalizerService.normalizeCall(call);
        res = checker.isBlockable(call);
        Assert.assertEquals(IChecker.NONE, res);
        Assert.assertEquals(null, call.getBlockOrigin());

        //Allow ONLY contacts
        sett.setAllowOnlyContacts(true);
        call.setNumber("48556078");
        call.setCountryISO("AR");
        normalizerService.normalizeCall(call);
        res = checker.isBlockable(call);
        Assert.assertEquals(IChecker.NO, res);
        Assert.assertEquals(BlockOrigin.CONTACTS, call.getBlockOrigin());

        call.setNumber("99999999");
        call.setCountryISO("AR");
        normalizerService.normalizeCall(call);
        res = checker.isBlockable(call);
        Assert.assertEquals(IChecker.YES, res);
        Assert.assertEquals(BlockOrigin.CONTACTS, call.getBlockOrigin());
    }

    @Test
    public void testMethodFindInBlackList() throws NumberParseException, RemoteException,
            OperationApplicationException, PhoneNumberException, TooShortNumberException {
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

        String[] par = contactDAO.findContact(call.getNumber());
        Assert.assertNotNull(par);
        Assert.assertTrue(par.length > 0);

        List<String> databaseNumbers = new ArrayList<>();
        databaseNumbers.add(par[0]);

        Assert.assertTrue(databaseNumbers.containsAll(numbersMatch));
        Assert.assertFalse(databaseNumbers.containsAll(numbersNotMatch));


    }

    private void insertData(List<String> matches, List<String> notMatches) throws
            RemoteException, OperationApplicationException {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Contacts.CONTENT_URI).build());
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .build());
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI).build());
        getTargetContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        ops.clear();
        //================
        //Data for matches
        //================
        String DisplayName = "TestMatches";

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        DisplayName).build());


        //------------------------------------------------------ Mobile Number
        for (String number : matches) {

            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());

        }
        getTargetContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        //================
        //Data for NOT matches
        //================
        ops.clear();
        DisplayName = "TestNOTMatches";

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        DisplayName).build());


        //------------------------------------------------------ Mobile Number
        for (String number : notMatches) {

            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());

        }

        //================

        getTargetContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

    }
}