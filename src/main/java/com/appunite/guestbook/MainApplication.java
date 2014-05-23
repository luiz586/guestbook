package com.appunite.guestbook;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.appunite.guestbook.dagger.ApplicationModule;
import com.appunite.guestbook.dagger.ForActivity;
import com.appunite.guestbook.dagger.ForApplication;
import com.crashlytics.android.Crashlytics;
import com.google.api.client.http.HttpTransport;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import dagger.ObjectGraph;

public class MainApplication extends Application {

    private ObjectGraph mApplicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        initializeDagger();

        if (BuildConfig.RELEASE) {
            Crashlytics.start(this);
        }
        if (BuildConfig.DEBUG) {
            Logger.getLogger(HttpTransport.class.getName()).setLevel(Level.CONFIG);
        }
    }

    private void initializeDagger() {
        ApplicationModule module = new ApplicationModule(this);
        mApplicationGraph = ObjectGraph.create(module);
        mApplicationGraph.inject(this);
    }

    public ObjectGraph getApplicationGraph() {
        return mApplicationGraph;
    }

    public static MainApplication fromApplication(Application application) {
        return (MainApplication) application;
    }
}
