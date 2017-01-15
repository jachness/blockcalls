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
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jachness.blockcalls.BuildConfig;
import com.jachness.blockcalls.R;
import com.jachness.blockcalls.stuff.AppContext;
import com.jachness.blockcalls.stuff.DateUtil;
import com.jachness.blockcalls.stuff.Util;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by jachness on 6/12/2016.
 */

public class AddCallLogFragment extends ListFragment implements LoaderManager
        .LoaderCallbacks<Cursor> {
    private static final String TAG = AddCallLogFragment.class.getSimpleName();
    private CallLogListAdapter mAdapter;
    private HashMap<Integer, String[]> numbersSelected;
    private View.OnClickListener okButtonListener;
    private Button btOk;
    private AdapterView.OnItemClickListener itemClickListener;
    private CompoundButton.OnCheckedChangeListener checkedChangeListener;
    private String tableName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.add_call_log_fragment, null);
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        tableName = getCorrectCallLogTableName();
        SimpleDateFormat formatter = DateUtil.getShortDateFormatter(getActivity(), Util
                .getDefaultLocale());
        mAdapter = new CallLogListAdapter(getContext(), new String[]{CallLog.Calls._ID}, formatter);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);

        numbersSelected = new HashMap<>();

        itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.callLogCbSelected);
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
                int count = 0;
                Collection<String[]> data = numbersSelected.values();
                for (String[] log : data) {
                    count += appContext.getBlackListWrapper().addNumberToBlackList(log[0],
                            log[1], false);
                }

                Toast.makeText(getActivity(), getResources().getQuantityString(R.plurals
                                .call_log_toast_contacts_added,
                        count, count), Toast.LENGTH_LONG).show();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        };

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemClickListener(itemClickListener);
        btOk = (Button) view.findViewById(R.id.callLogBtOk);
        btOk.setOnClickListener(okButtonListener);

        Button btCancel = (Button) view.findViewById(R.id.callLogBtCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });
        this.updateOkButton();
    }

    private void updateOkButton() {
        if (numbersSelected.size() == 0) {
            btOk.setText(R.string.call_log_no_contacts_selected);
        } else {
            btOk.setText(getResources().getQuantityString(R.plurals.call_log_add_x_contacts,
                    numbersSelected.size(),
                    numbersSelected.size()));
        }

        btOk.setEnabled(!numbersSelected.isEmpty());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = CallLog.Calls.CONTENT_URI;

        final String[] projection = new String[]{
                CallLog.Calls._ID,
                CallLog.Calls.DATE,
                CallLog.Calls.NUMBER,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.CACHED_NUMBER_TYPE
        };

        String exclusionNullNumber = CallLog.Calls.NUMBER + " IS NOT NULL AND " + CallLog.Calls
                .NUMBER + " <> \'\' AND ";

        String types = CallLog.Calls.OUTGOING_TYPE + "," + CallLog.Calls.VOICEMAIL_TYPE + "," +
                CallLog.Calls.BLOCKED_TYPE;

        String exclusionCallType = CallLog.Calls.TYPE + " NOT IN (" + types + ") AND ";
        //See CallLog.Calls.NUMBER_PRESENTATION for this filter
        String exclusionUnavailableNumber = "'-' IS NOT substr(" + CallLog.Calls.NUMBER + ",0,2) " +
                "AND ";

        String inclusionLastCall = CallLog.Calls._ID + " in (select " + CallLog.Calls._ID + " " +
                "from " + tableName + " group by " +
                CallLog.Calls.NUMBER + " having max(" + CallLog.Calls.DATE + "))";

        String sort = CallLog.Calls.DATE + " DESC";

        return new CursorLoader(getActivity(),
                uri,
                projection,
                exclusionNullNumber + exclusionCallType + exclusionUnavailableNumber +
                        inclusionLastCall,
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

    //It seems that Samsung devices has 'logs' name instead 'calls' for call logs
    private String getCorrectCallLogTableName() {
        Uri uri = CallLog.Calls.CONTENT_URI;
        String testTableName = CallLog.Calls._ID + " in (select " + CallLog.Calls._ID +
                " from calls group by " + CallLog.Calls.NUMBER + ")";

        Cursor cursor = null;
        try {
            cursor = getContext().getContentResolver().query(uri,
                    new String[]{CallLog.Calls._ID}, testTableName, null, null);
            cursor.moveToNext();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().contains("no such table")) {
                return "logs";
            } else if (BuildConfig.DEBUG) {
                Log.d(TAG, "Exception while checking table name", e);
            }
        } finally {
            Util.close(cursor);
        }
        return "calls";
    }

    public class CallLogListAdapter extends SimpleCursorAdapter {
        private final SimpleDateFormat formatter;

        public CallLogListAdapter(Context context, String[] from, SimpleDateFormat
                formatter) {
            super(context, R.layout.add_call_log_fragment_row, null, from, null, 0);
            this.formatter = formatter;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            ViewHolder holder = (ViewHolder) view.getTag(R.string.tag_viewHolder);
            if (holder == null) {
                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.callLogLabelName);
                holder.number = (TextView) view.findViewById(R.id.callLogLabelNumber);
                holder.type = (TextView) view.findViewById(R.id.callLogLabelType);
                holder.select = (CheckBox) view.findViewById(R.id.callLogCbSelected);
                holder.date = (TextView) view.findViewById(R.id.callLogLabelDate);
                holder.details = (LinearLayout) view.findViewById(R.id.callLogDetails);
                view.setTag(R.string.tag_viewHolder, holder);
            }

            int idValue = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
            long dateValue = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            String numberValue = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            String cachedNameValue = cursor.getString(cursor.getColumnIndex(CallLog.Calls
                    .CACHED_NAME));
            int callTypeValue = cursor.getInt(cursor.getColumnIndex(CallLog.Calls
                    .CACHED_NUMBER_TYPE));


            if (!TextUtils.isEmpty(cachedNameValue)) {
                holder.name.setText(cachedNameValue);
                holder.number.setText(numberValue);
                String typeName = ContactsContract.CommonDataKinds.Phone.getTypeLabel(mContext
                        .getResources(), callTypeValue, "")
                        .toString();
                holder.type.setText(typeName);
                holder.details.setVisibility(View.VISIBLE);
            } else {
                holder.name.setText(numberValue);
                holder.details.setVisibility(View.GONE);
            }

            holder.select.setTag(R.string.tag_id, idValue);
            holder.select.setTag(R.string.tag_data, new String[]{numberValue, cachedNameValue});
            holder.select.setOnCheckedChangeListener(null);
            holder.select.setChecked(numbersSelected.containsKey(idValue));
            holder.select.setOnCheckedChangeListener(checkedChangeListener);

            holder.date.setText(formatter.format(new Date(dateValue)));

        }

        class ViewHolder {
            TextView name;
            TextView number;
            TextView type;
            TextView date;
            CheckBox select;
            LinearLayout details;
        }
    }
}
