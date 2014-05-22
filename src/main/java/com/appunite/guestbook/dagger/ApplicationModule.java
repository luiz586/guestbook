package com.appunite.guestbook.dagger;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.appunite.guestbook.MainApplication;
import com.appunite.guestbook.api.GuestbookApiModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = MainApplication.class,
        includes = GuestbookApiModule.class,
        library = true
)
public class ApplicationModule {

    private final Application mMainApplication;

    public ApplicationModule(Application mainApplication) {
        mMainApplication = mainApplication;
    }

    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return mMainApplication;
    }

    @Provides
    @ForApplication
    Resources provideResources(@ForApplication Context context) {
        return context.getResources();
    }

}
