package com.appunite.guestbook.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appunite.guestbook.R;
import com.appunite.guestbook.api.model.EntriesType;
import com.appunite.guestbook.dagger.ForApplication;
import com.google.api.client.util.Lists;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.google.common.base.Preconditions.checkNotNull;

public class EntryAdapter extends BaseAdapter {
    @Inject
    Picasso mPicasso;
    @Inject
    LayoutInflater mInflater;


    //TODO Change to use actual entry class
    private List<EntriesType> mEntries;
    private int mImageSize;
    private String mAuthorDateFormat;

    @Inject
    public EntryAdapter(@ForApplication Context context){
        Resources rs = context.getResources();
        mImageSize = rs.getDimensionPixelSize(R.dimen.entry_photo_size);
        mAuthorDateFormat = rs.getString(R.string.entry_author_and_date);
    }

    public void swapData(List<EntriesType> entries) {
        mEntries = entries;
        notifyDataSetChanged();
    }



    @Override
    public int getCount() {
        if(mEntries != null){
            return mEntries.size();
        } else {
            return 0;
        }
    }

    @Override
    public EntriesType getItem(int position) {
        return mEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    public static class ViewHolder {
        @InjectView(R.id.entry_photo)
        ImageView mPhoto;
        @InjectView(R.id.content)
        TextView mContent;
        @InjectView(R.id.entry_author)
        TextView mAuthor;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(view == null){
            view = checkNotNull(mInflater.inflate(R.layout.entry_layout, parent, false));
            viewHolder = new ViewHolder();
            view.setTag(viewHolder);
            ButterKnife.inject(viewHolder, view);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        viewHolder.mContent.setText(getItem(position).message);
        viewHolder.mAuthor.setText(String.format(mAuthorDateFormat, "AUTHOR", "05.03.2014"));
        mPicasso.load(R.drawable.ava)
                .resize(mImageSize, mImageSize)
                .into(viewHolder.mPhoto);

        return view;
    }
}
