package com.appunite.guestbook;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.InjectView;


public class EntryDetailFragment extends BaseFragment {
    String mEntryTitle;

    @InjectView(R.id.entry_detail_title)
    TextView mEntryTitleTV;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entry_detail, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEntryTitle = getTitle();
        if(mEntryTitle != null){
            mEntryTitleTV.setText(mEntryTitle);
        }
    }

    private String getTitle(){
        return getArguments().getString("title");
    }
}
