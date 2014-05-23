package com.appunite.guestbook;



import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appunite.guestbook.api.GuestbookApi;
import com.appunite.guestbook.api.model.EntryDetailResponse;
import com.appunite.guestbook.api.model.ResponseEntries;
import com.appunite.guestbook.dagger.ForActivity;
import com.appunite.guestbook.helpers.data.ApiAsyncLoader;
import com.appunite.guestbook.helpers.data.Result;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

import static com.google.common.base.Preconditions.checkNotNull;


public class EntryDetailFragment extends ErrorHelperApiLoaderFragment<Result<EntryDetailResponse>> {
    String mEntryTitle;

    @InjectView(R.id.entry_detail_title)
    TextView mEntryTitleTV;

    @InjectView(R.id.entry_detail_text)
    TextView mEntryTextTV;

    @Inject
    Provider<EntryDetailLoader> mEntriesLoaderProvider;



    static class EntryDetailLoader extends ApiAsyncLoader<EntryDetailResponse> {

        @Inject
        GuestbookApi mGuestBookApi;

        @Inject
        public EntryDetailLoader(@ForActivity Context context) {
            super(context);
        }

        @Override
        protected EntryDetailResponse loadFromApi() throws Exception {
            return mGuestBookApi.entries().get().execute();
        }
    }



    public EntryDetailFragment() {
        // Required empty public constructor
    }

    public static EntryDetailFragment newInstance(String entryTitle){
        EntryDetailFragment detailFragment = new EntryDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putString("title", entryTitle);
        detailFragment.setArguments(bundle);

        return detailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onLoadMainFinished(Result<EntryDetailResponse> result) {
        super.onLoadMainFinished(result);

        if(result.isSuccess()){
            mEntryTextTV.setText(result.getResult().message);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEntryTitle = getTitle();
        if(mEntryTitle != null){
            mEntryTitleTV.setText(mEntryTitle);
        }
    }

    @Override
    protected View onCreateChildView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return checkNotNull(inflater.inflate(R.layout.fragment_entry_detail, container, false));
    }

    @Override
    protected boolean isScreenEmpty(Result<EntryDetailResponse> result) {
        return false;
    }

    @Override
    protected Loader<Result<EntryDetailResponse>> onCreateMainLoader(Bundle bundle) {
        return mEntriesLoaderProvider.get();
    }

    @Override
    protected void onLoadMainReset() {

    }

    private String getTitle(){
        return getArguments().getString("title");
    }
}
