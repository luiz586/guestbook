package com.appunite.guestbook;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.appunite.guestbook.dagger.ActivityGraphProvider;
import com.appunite.guestbook.dagger.ActivityModule;
import com.appunite.guestbook.dagger.ForActivity;
import com.appunite.guestbook.dagger.ForApplication;

import javax.inject.Inject;

import dagger.ObjectGraph;

public class BaseActivity extends ActionBarActivity implements ActivityGraphProvider {

    private ObjectGraph mActivityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityGraph().inject(this);
    }

    @Override
    public ObjectGraph getActivityGraph() {
        if (mActivityGraph == null) {
            mActivityGraph = MainApplication.fromApplication(getApplication())
                    .getApplicationGraph()
                    .plus(new ActivityModule(this));
        }

        return mActivityGraph;
    }
}
