package com.appunite.guestbook.dagger;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;

import com.appunite.guestbook.MainActivity;
import com.appunite.guestbook.SplashActivity;
import com.appunite.guestbook.api.content.GsonObjectParser;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                SplashActivity.class,
                MainActivity.class
        },
        addsTo = ApplicationModule.class,
        library = true
)

public class ActivityModule {

    private final Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    @Singleton
    @ForActivity
    Context provideActivityContext() {
        return mActivity;
    }

    @Provides
    @Singleton
    Resources provideResources(@ForActivity Context context) {
        return mActivity.getResources();
    }

    @Provides
    @Singleton
    Picasso providePicasso(@ForApplication Context context) {
        return Picasso.with(context);
    }

    @Provides
    @Singleton
    LayoutInflater provideLayoutInflater(@ForActivity Context context) {
        return LayoutInflater.from(context);
    }

    @Provides
    @Singleton
    GoogleApiClient provideGoogleApiClient(@ForActivity Context context){
        return new GoogleApiClient.Builder(context)
                .addApi(Plus.API, null)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }
}
