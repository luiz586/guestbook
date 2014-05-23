package com.appunite.guestbook.api;

import com.appunite.guestbook.api.content.GsonHttpContent;
import com.appunite.guestbook.api.content.GsonObjectParser;
import com.appunite.guestbook.api.content.StreamingGsonHttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = GuestbookApi.class,
        complete = false,
        library = true
)
public class GuestbookApiModule {

    @Provides
    @Singleton
    GsonHttpContent provideGsonHttpContent(Gson gson) {
        return new StreamingGsonHttpContent(gson);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    @Provides
    @Singleton
    HttpUnsuccessfulResponseHandler providesHttpUnsuccessfulResponseHandler() {
        return new GuestbookHttpUnsuccessfulResponseHandler();
    }

    @Provides
    @Singleton
    HttpTransport provideHttpTransport() {
        //return new ApacheHttpTransport();
        return new MockHttpTransport() {

            @Override
            public LowLevelHttpRequest buildRequest(String method, final String url) throws IOException {


                 return new MockLowLevelHttpRequest() {
                    @Override
                    public LowLevelHttpResponse execute() throws IOException {
                        final MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
                        response.addHeader("custom_header", "value");
                        response.setStatusCode(200);                      response.setContentType(Json.MEDIA_TYPE);


                        if( url.endsWith(GuestbookApi.REST_PATH_ENTRIES) ){
                            response.setContent("\n" +
                                    "{\"entries\": [{\"title\": \"dfgsdfgsdg\"}]}");
                        }
                        else if( url.endsWith(GuestbookApi.REST_PATH_DETAILS) ){
                            response.setContent("\n" +
                                    "{\"message\": \"I am a detail message of an entry, jupi!\"}");
                        }

                        return response;

                    }
                };
            }


        };
    }

    @Provides
    @Singleton
    HttpRequestFactory provideHttpRequestFactory(final Gson mGson, final HttpTransport transport){
        return transport.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                final HttpHeaders headers = request.getHeaders();
                headers.setAccept("application/json");
                request.setParser(new GsonObjectParser(mGson));
            }
        });
    }


}
