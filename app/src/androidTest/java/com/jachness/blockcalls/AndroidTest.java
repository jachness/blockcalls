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
    private Context targetContext;
    private AllComponentTest component;
    private AppPreferences appPreferences;
    private Context context;

    protected void setUp() throws Exception {
        targetContext = InstrumentationRegistry.getTargetContext();
        context = InstrumentationRegistry.getContext();
        component = DaggerAllComponentTest.builder().blockModule(new BlockModule(targetContext))
                .appModule(new AppModule(targetContext))
                .dAOModule(new DAOModule(targetContext)).build();
        appPreferences = new AppPreferences(targetContext);
    }

    protected Context getTargetContext() {
        return targetContext;
    }

    protected AllComponentTest getComponent() {
        return component;
    }

    protected AppPreferences getAppPreferences() {
        return appPreferences;
    }

    public Context getContext() {
        return context;
    }
}
