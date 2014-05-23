package com.appunite.guestbook.api;

import com.appunite.guestbook.api.content.GsonHttpContent;
import com.appunite.guestbook.api.content.GsonObjectParser;
import com.appunite.guestbook.api.model.EntryDetailResponse;
import com.appunite.guestbook.api.model.ResponseEntries;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.UriTemplate;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

public class GuestbookApi {
    public static final String REST_PATH_ENTRIES = "v1/entries/entries.json";
    public static final String REST_PATH_DETAILS = "v1/entries/details.json";

    @Inject
    Provider<GsonHttpContent> mGsonHttpContentProvider;

/*    final Gson mGson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();*/

    @Inject
    Gson mGson;

    @Inject
    HttpRequestFactory mRequestFactory;

    public GuestbookApi() {
       //mRequestFactory = getRequestFactory();
    }

    public abstract class GuestbookRequest<T> {
        private final String mUriTemplate;
        private final String mRequestMethod;
        private HttpContent mContent;
        private final Class<T> mResponseClass;

        public GuestbookRequest(String requestMethod, String uriTemplate, HttpContent content,
                            Class<T> responseClass) {
            mUriTemplate = checkNotNull(uriTemplate, "Uri could not be null");
            mRequestMethod = checkNotNull(requestMethod);
            mResponseClass = checkNotNull(responseClass);
            setObjectContent(content);
        }

        public void setObjectContent(Object content) {
            setContent(content == null ? null : mGsonHttpContentProvider.get().setObject(content));
        }

        public void setContent(HttpContent content){
            mContent = content;
        }

        public T execute() throws IOException {
            String baseUrl = "http://guestbook.appunite.com/api";

            GenericUrl url = new GenericUrl(UriTemplate.expand(baseUrl, mUriTemplate, this, true));
            final HttpRequest httpRequest = mRequestFactory.buildRequest(mRequestMethod, url, mContent);
            setupRequest(httpRequest);
            final T data = httpRequest.execute().parseAs(mResponseClass);
            return data;
        }

        protected void setupRequest(HttpRequest httpRequest) {

        }

    }


    public EntriesApi entries() { return new EntriesApi(); }

        public class EntriesApi {
            public List list() {
                return new List();
            }

            public Details get() {
                return new Details();
            }

            public class List extends GuestbookRequest<ResponseEntries> {

                private List() {
                    super(HttpMethods.GET, GuestbookApi.REST_PATH_ENTRIES, null, ResponseEntries.class);
                }
            }

            public class Details extends GuestbookRequest<EntryDetailResponse> {

                private Details() {
                    super(HttpMethods.GET, GuestbookApi.REST_PATH_DETAILS, null, EntryDetailResponse.class);
                }
            }

    }

}
