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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jachness.blockcalls.BuildConfig;
import com.jachness.blockcalls.R;
import com.jachness.blockcalls.db.BlackListTable;
import com.jachness.blockcalls.entities.BlackListNumberEntity;
import com.jachness.blockcalls.exceptions.FileException;
import com.jachness.blockcalls.stuff.AppContext;
import com.jachness.blockcalls.stuff.PermUtil;
import com.jachness.blockcalls.stuff.Util;
import com.jachness.blockcalls.tasks.AsyncTaskResult;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.ArrayList;

import hugo.weaving.DebugLog;

/**
 * Created by jachness on 26/9/2016.
 */
public class BlackListFragment extends ListFragment implements LoaderManager
        .LoaderCallbacks<Cursor> {
    private static final String TAG = BlackListFragment.class.getSimpleName();
    private static final int PERM_CODE_WRITE_EXTERNAL_STORAGE = 3;
    private static final int PERM_CODE_READ_EXTERNAL_STORAGE = 4;
    private static final int FILE_CODE = 5;

    private BlackListAdapter mAdapter;
    private View.OnClickListener enableListener;
    private AppContext appContext;
    private View.OnClickListener deleteListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.black_list_main, null);
        ListView lv = (ListView) rootView.findViewById(android.R.id.list);
        lv.addFooterView(getLayoutInflater(savedInstanceState).inflate(R.layout
                .black_list_footer, null), null, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = (AppContext) getContext().getApplicationContext();
        mAdapter = new BlackListAdapter(getContext(), new String[]{BlackListTable.UID});
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);

        deleteListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BlackListNumberEntity entity = (BlackListNumberEntity) v.getTag();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), 0);
                builder.setTitle(R.string.common_delete);
                builder.setMessage((TextUtils.isEmpty(entity.getDisplayName()) ? entity
                        .getDisplayNumber() : entity.getDisplayName()));
                builder.setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener
                        () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appContext.getBlackListWrapper().delete(entity);
                    }
                });
                builder.setNegativeButton(R.string.common_cancel, new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        };

        enableListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final BlackListNumberEntity entity = (BlackListNumberEntity) v.getTag();
                appContext.getBlackListWrapper().updateEnabled(entity, ((SwitchCompat) v).isChecked());
            }
        };
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sort = BlackListTable.DISPLAY_NAME + ", CASE begin_with WHEN 0 THEN substr(" +
                BlackListTable.NORMALIZED_NUMBER +
                ", 2, 100) ELSE " + BlackListTable.NORMALIZED_NUMBER + " END";
        return new CursorLoader(getActivity(), BlackListTable.CONTENT_URI,
                null, null, null, sort);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.black_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.blackListMnDeleteAll:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 0);
                builder.setTitle(R.string.delete_black_list_title);
                builder.setMessage(R.string.delete_black_list_message);
                builder.setPositiveButton(R.string.common_delete, new DialogInterface.OnClickListener
                        () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appContext.getBlackListWrapper().deleteAll();
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
            case R.id.blackListMnExport:
                if (!PermUtil.checkWriteExternalStorage(getContext())) {
                    requestPermissions(new String[]{PermUtil.WRITE_EXTERNAL_STORAGE},
                            PERM_CODE_WRITE_EXTERNAL_STORAGE);
                } else {
                    doExport();
                }
                return true;
            case R.id.blackListMnImport:
                if (!PermUtil.checkReadExternalStorage(getContext())) {
                    requestPermissions(new String[]{PermUtil.READ_EXTERNAL_STORAGE},
                            PERM_CODE_READ_EXTERNAL_STORAGE);
                } else {
                    doImport();
                }
                return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @DebugLog
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERM_CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doExport();
                } else {
                    showSnackForPermission(true);
                }
                break;
            case PERM_CODE_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doImport();
                } else {
                    showSnackForPermission(false);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();
                    if (clip != null) {
                        Uri uri = clip.getItemAt(0).getUri();
                        importFile(uri);
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra(FilePickerActivity
                            .EXTRA_PATHS);
                    if (paths != null) {
                        Uri uri = Uri.parse(paths.get(0));
                        importFile(uri);
                    }
                }

            } else {
                Uri uri = data.getData();
                importFile(uri);
            }
        }

    }

    @DebugLog
    private void doExport() {
        try {
            boolean exist = appContext.getImportExportWrapper().isAlreadyExistFile();
            if (exist) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.export_title);
                builder.setMessage(R.string.export_already_exist_message);
                builder.setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener
                        () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exportAndOverrideBlackList();
                    }
                });
                builder.setNegativeButton(R.string.common_cancel, new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            } else {
                exportAndOverrideBlackList();
            }
        } catch (FileException e) {
            if (BuildConfig.DEBUG) Log.d(TAG, e.getMessage(), e);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(Util.getStringId(e)).setTitle(R.string.export_title);
            builder.setNeutralButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            builder.show();
        }
    }

    @DebugLog
    private void exportAndOverrideBlackList() {
        new ExportAsyncTask().execute();
    }

    private void doImport() {
        Intent i = new Intent(getActivity(), FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory()
                .getPath());
        startActivityForResult(i, FILE_CODE);
    }

    @DebugLog
    private void importFile(@NonNull Uri uri) {
        new ImportAsyncTask().execute(uri);
    }

    @DebugLog
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showSnackForPermission(boolean write) {
        int message;
        if (write) {
            message = R.string.common_write_media_message;
        } else {
            message = R.string.common_read_media_message;
        }
        Snackbar snackBar = Snackbar.make(getActivity().findViewById(R.id
                .main_coordinator_layout), message, Snackbar
                .LENGTH_LONG);
        snackBar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,
                getActivity().getTheme()));
        snackBar.setAction(R.string.common_grant, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        snackBar.show();
    }

    public class BlackListAdapter extends SimpleCursorAdapter {
        public BlackListAdapter(Context context, String[] from) {
            super(context, R.layout.black_list_row, null, from, null, 0);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag(R.string.tag_viewHolder);
            if (holder == null) {
                holder = new ViewHolder();
                holder.displayName = (TextView) view.findViewById(R.id.blackListLabelName);
                holder.number = (TextView) view.findViewById(R.id.blackListLabelNumber);
                holder.enable = (SwitchCompat) view.findViewById(R.id.blackListCbEnabled);
                holder.delete = (ImageView) view.findViewById(R.id.blackListIvDelete);
                view.setTag(R.string.tag_viewHolder, holder);
            }

            BlackListNumberEntity phoneNumber = new BlackListNumberEntity(cursor);

            view.setTag(R.string.tag_data, phoneNumber);

            if (TextUtils.isEmpty(phoneNumber.getDisplayName()) || phoneNumber.isBeginWith()) {
                holder.displayName.setText(phoneNumber.getDisplayNumber());
                holder.number.setVisibility(View.GONE);
            } else {
                holder.displayName.setText(phoneNumber.getDisplayName());
                holder.number.setVisibility(View.VISIBLE);
                holder.number.setText(phoneNumber.getDisplayNumber());
            }

            holder.enable.setTag(phoneNumber);
            holder.enable.setOnClickListener(null);
            holder.enable.setChecked(phoneNumber.isEnabled());
            holder.enable.setOnClickListener(enableListener);
            holder.delete.setTag(phoneNumber);
            holder.delete.setOnClickListener(deleteListener);
        }

        class ViewHolder {
            TextView number;
            TextView displayName;
            SwitchCompat enable;
            ImageView delete;
        }
    }

    public class ImportAsyncTask extends AsyncTask<Uri, Void, AsyncTaskResult<Integer>> {
        private Dialog dialog;

        @Override
        protected void onPreExecute() {
            Util.lockScreenOrientation(BlackListFragment.this.getActivity());
            dialog = ProgressDialog.show(BlackListFragment.this.getContext(), "", getString(R.string
                    .import_progress_bar_message), true, false);
            dialog.show();
        }

        @Override
        @DebugLog
        protected AsyncTaskResult<Integer> doInBackground(Uri... params) {
            try {
                int count = appContext.getImportExportWrapper().importBlackList(params[0]);
                return new AsyncTaskResult<>(count);
            } catch (FileException e) {
                return new AsyncTaskResult<>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<Integer> result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.import_title);
            builder.setNeutralButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Util.unlockScreenOrientation(BlackListFragment.this.getActivity());
                }
            });

            if (result.getError() != null) {
                Exception e = result.getError();
                builder.setMessage(Util.getStringId((FileException) e));
            } else if (isCancelled()) {
                builder.setMessage(R.string.common_async_task_canceled);
            } else {
                if (result.getResult() == 0) {
                    builder.setMessage(R.string.import_message_quantity_zero);
                } else {
                    builder.setMessage(getResources().getQuantityString(R.plurals
                                    .import_message_quantity, result.getResult(),
                            result.getResult()));
                }
            }
            dialog.dismiss();
            builder.show();
        }
    }

    public class ExportAsyncTask extends AsyncTask<Void, Void, AsyncTaskResult<String>> {
        private Dialog dialog;

        @Override
        protected void onPreExecute() {
            Util.lockScreenOrientation(BlackListFragment.this.getActivity());
            dialog = ProgressDialog.show(getContext(), "", getString(R.string
                    .export_progress_bar_message), true, false);
            dialog.show();
        }

        @DebugLog
        @Override
        protected AsyncTaskResult<String> doInBackground(Void... params) {
            try {
                String fullPath = appContext.getImportExportWrapper().exportBlackList();
                return new AsyncTaskResult<>(fullPath);
            } catch (FileException e) {
                return new AsyncTaskResult<>(e);
            }
        }


        @Override
        protected void onPostExecute(AsyncTaskResult<String> integerAsyncTaskResult) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.export_title);
            builder.setNeutralButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Util.unlockScreenOrientation(BlackListFragment.this.getActivity());
                }
            });

            if (integerAsyncTaskResult.getError() != null) {
                Exception e = integerAsyncTaskResult.getError();
                builder.setMessage(Util.getStringId((FileException) e));
            } else if (isCancelled()) {
                builder.setMessage(R.string.common_async_task_canceled);
            } else {
                builder.setMessage(getResources().getString(R.string.export_success_message,
                        integerAsyncTaskResult.getResult()));
            }
            dialog.dismiss();
            builder.show();
        }
    }
}
