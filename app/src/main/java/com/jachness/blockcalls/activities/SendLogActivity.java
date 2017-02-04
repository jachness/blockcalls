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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jachness.blockcalls.R;
import com.jachness.blockcalls.stuff.Util;

/**
 * Created by jachness on 3/2/2017.
 */

public class SendLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_log_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sendLogToolBar);
        setSupportActionBar(toolbar);
        setTitle(getText(R.string.pref_submit_log));

        TextView logView = (TextView) findViewById(R.id.sendLogTfDebugLog);
        Button okView = (Button) findViewById(R.id.sendLogBtOk);
        Button cancelView = (Button) findViewById(R.id.sendLogBtCancel);

        final String log = Util.retrieveDebugLog(this);
        logView.setText(log);
        okView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = getString(R.string.support_email_address);
                String subject = getString(R.string.support_email_subject);

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("vnd.android.cursor.dir/email");
                String to[] = {email};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                emailIntent.putExtra(Intent.EXTRA_TEXT, log);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                startActivity(Intent.createChooser(emailIntent, getString(R.string
                        .send_log_chooser)));
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
