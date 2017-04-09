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

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.jachness.blockcalls.R;
import com.jachness.blockcalls.db.LogTable;
import com.jachness.blockcalls.entities.LogEntity;
import com.jachness.blockcalls.services.Call;
import com.jachness.blockcalls.stuff.AppContext;
import com.jachness.blockcalls.stuff.DateUtil;
import com.jachness.blockcalls.stuff.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jachness on 26/9/2016.
 */
public class LogListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private LogListAdapter mAdapter;
    private AppContext appContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.log_list_main, null);
        ListView lv = (ListView) rootView.findViewById(android.R.id.list);
        lv.addFooterView(getLayoutInflater(savedInstanceState).inflate(R.layout.log_list_footer,
                null), null, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = (AppContext) getContext().getApplicationContext();

        SimpleDateFormat formatter = DateUtil.getDetailedDateFormatter(getActivity(), Util
                .getDefaultLocale());
        mAdapter = new LogListAdapter(getContext(), new String[]{LogTable.UID}, formatter);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), LogTable.CONTENT_URI, null, null, null, LogTable
                .DATE + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public class LogListAdapter extends SimpleCursorAdapter {


        private final SimpleDateFormat formatter;

        public LogListAdapter(Context context, String[] from, SimpleDateFormat
                formatter) {
            super(context, R.layout.log_list_row, null, from, null, 0);
            this.formatter = formatter;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            LogEntity logEntity = new LogEntity(cursor);

            ViewHolder holder = (ViewHolder) view.getTag(R.string.tag_viewHolder);
            if (holder == null) {
                holder = new ViewHolder();
                holder.number = (TextView) view.findViewById(R.id.logLabelNumber);
                holder.date = (TextView) view.findViewById(R.id.logLabelDate);
                holder.blockMethod = (TextView) view.findViewById(R.id.logLabelMethod);
                view.setTag(R.string.tag_viewHolder, holder);
            }


            if (logEntity.getDisplayNumber().equals(Call.PRIVATE_NUMBER)) {
                holder.number.setText(R.string.common_private);
            } else if (!TextUtils.isEmpty(logEntity.getDisplayName())) {
                holder.number.setText(logEntity.getDisplayName());
            } else {
                holder.number.setText(logEntity.getDisplayNumber());
            }

            holder.date.setText(formatter.format(new Date(logEntity.getTime())));
            switch (logEntity.getBlockOrigin()) {
                case PRIVATE:
                    holder.blockMethod.setText(R.string.enum_block_method_private_number);
                    return;
                case BLACK_LIST:
                    holder.blockMethod.setText(R.string.enum_block_method_blacklist);
                    return;
                case CONTACTS:
                    holder.blockMethod.setText(R.string.enum_block_method_contacts);
            }

        }

        class ViewHolder {
            TextView number;
            TextView date;
            TextView blockMethod;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.log_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.callLogDeleteAll:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 0);
                builder.setTitle(R.string.delete_log_title);
                builder.setMessage(R.string.delete_log_message);
                builder.setPositiveButton(R.string.common_delete, new DialogInterface.OnClickListener
                        () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appContext.getContentResolver().delete(LogTable.CONTENT_URI, null, null);
                    }
                });
                builder.setNegativeButton(R.string.common_cancel, new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return true;
            default:
                return false;
        }
    }

}