package com.jachness.blockcalls;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.jachness.blockcalls.modules.AppModule;
import com.jachness.blockcalls.modules.BlockModule;
import com.jachness.blockcalls.modules.DAOModule;
import com.jachness.blockcalls.stuff.AppPreferences;

/**
 * Created by jachness on 11/11/2016.
 */

public abstract class AndroidTest {
    private Context context;
    private AllComponentTest component;
    private AppPreferences appPreferences;

    protected void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
        component = DaggerAllComponentTest.builder().blockModule(new BlockModule(context))
                .appModule(new AppModule(context))
                .dAOModule(new DAOModule(context)).build();
        appPreferences = new AppPreferences(context);
    }

    protected Context getContext() {
        return context;
    }

    protected AllComponentTest getComponent() {
        return component;
    }

    protected AppPreferences getAppPreferences() {
        return appPreferences;
    }
}
