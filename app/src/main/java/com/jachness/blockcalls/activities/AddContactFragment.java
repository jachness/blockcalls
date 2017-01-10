/*
 * Copyright (C) 2017 Jonatan Cheiro Anriquez
 *
 * This file is part of Block Calls.
 *
 * Block Calls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Block Calls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Block Calls. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jachness.blockcalls.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jachness.blockcalls.R;
import com.jachness.blockcalls.stuff.AppContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jachness on 21/11/2016.
 */

public class AddContactFragment extends ListFragment implements LoaderManager
        .LoaderCallbacks<Cursor> {

    private ContactListAdapter mAdapter;
    private Map<Integer, String[]> numbersSelected;
    private Button btOk;
    private View.OnClickListener okButtonListener;
    private AdapterView.OnItemClickListener itemClickListener;
    private CompoundButton.OnCheckedChangeListener checkedChangeListener;
    private View.OnClickListener cancelButtonListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.add_contact_fragment, null);
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mAdapter = new ContactListAdapter(getContext(), new String[]{ContactsContract
                .Contacts._ID});
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);

        numbersSelected = new HashMap<>();

        itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.contactCbSelected);
                cb.setChecked(!cb.isChecked());
            }
        };

        checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckBox cb = (CheckBox) buttonView;
                int id = (int) cb.getTag(R.string.tag_id);
                if (numbersSelected.containsKey(id)) {
                    numbersSelected.remove(id);
                    cb.setChecked(false);
                } else {
                    numbersSelected.put(id, (String[]) cb.getTag(R.string.tag_data));
                    cb.setChecked(true);
                }

                updateOkButton();
            }
        };

        okButtonListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AppContext appContext = (AppContext) getActivity().getApplicationContext();
                Collection<String[]> data = numbersSelected.values();
                int count = 0;
                for (String[] contact : data) {
                    count += appContext.getBlackListWrapper().addNumberToBlackList(contact[0],
                            contact[1], false);
                }

                Toast.makeText(getActivity(), getResources().getQuantityString(R.plurals
                                .contacts_toast_contacts_added,
                        count, count), Toast.LENGTH_LONG).show();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        };

        cancelButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        };

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemClickListener(itemClickListener);
        btOk = (Button) view.findViewById(R.id.contactBtOk);
        btOk.setOnClickListener(okButtonListener);
        Button btCancel = (Button) view.findViewById(R.id.contactBtCancel);
        btCancel.setOnClickListener(cancelButtonListener);
        this.updateOkButton();
    }

    private void updateOkButton() {
        if (numbersSelected.size() == 0) {
            btOk.setText(R.string.contacts_no_contacts_selected);
        } else {
            btOk.setText(getResources().getQuantityString(R.plurals.contacts_add_x_contacts,
                    numbersSelected.size(),
                    numbersSelected.size()));
        }

        btOk.setEnabled(!numbersSelected.isEmpty());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            uri = uri.buildUpon().appendQueryParameter(ContactsContract.REMOVE_DUPLICATE_ENTRIES,
                    "true").build();
        }

        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.LABEL};

        //Seen at org\thoughtcrime\securesms\contacts\ContactsDatabase.java
        String exclusionSelection = ContactsContract.Data.SYNC2 + " IS NULL OR " +
                ContactsContract.Data.SYNC2 + " != '" + "__TS" + "'";

        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED " +
                "ASC";

        return new CursorLoader(getActivity(),
                uri,
                projection,
                exclusionSelection,
                null,
                sort
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    public class ContactListAdapter extends SimpleCursorAdapter {
        public ContactListAdapter(Context context, String[] from) {
            super(context, R.layout.add_contact_fragment_row, null, from, null, 0);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            ViewHolder holder = (ViewHolder) view.getTag(R.string.tag_viewHolder);
            if (holder == null) {
                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.contactLabelName);
                holder.number = (TextView) view.findViewById(R.id.contactLabelNumber);
                holder.type = (TextView) view.findViewById(R.id.contactLabelNumberType);
                holder.select = (CheckBox) view.findViewById(R.id.contactCbSelected);
                view.setTag(R.string.tag_viewHolder, holder);
            }

            int nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
            int numberIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone
                    .NUMBER);
            int typeIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone
                    .TYPE);
            int labelIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone
                    .LABEL);
            int idIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID);


            holder.name.setText(cursor.getString(nameIndex));
            holder.number.setText(cursor.getString(numberIndex));

            String label = cursor.getString(labelIndex);
            String typeName = ContactsContract.CommonDataKinds.Phone.getTypeLabel(mContext
                    .getResources(), cursor.getInt(typeIndex), label).toString();
            holder.type.setText(typeName);


            holder.select.setTag(R.string.tag_id, cursor.getInt(idIndex));
            holder.select.setTag(R.string.tag_data, new String[]{cursor.getString(numberIndex),
                    cursor.getString(nameIndex)});
            holder.select.setOnCheckedChangeListener(null);
            holder.select.setChecked(numbersSelected.containsKey(cursor.getInt(idIndex)));
            holder.select.setOnCheckedChangeListener(checkedChangeListener);

        }

        class ViewHolder {
            TextView name;
            TextView number;
            TextView type;
            CheckBox select;
        }
    }
}
