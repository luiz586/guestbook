package com.appunite.guestbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appunite.guestbook.adapter.EntryAdapter;
import com.appunite.guestbook.api.GuestbookApi;
import com.appunite.guestbook.api.model.ResponseEntries;
import com.appunite.guestbook.content.UserPreferences;
import com.appunite.guestbook.dagger.ForActivity;
import com.appunite.guestbook.helpers.data.ApiAsyncLoader;
import com.appunite.guestbook.helpers.data.Result;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.google.common.base.Preconditions.checkNotNull;

public class EntriesFragment extends ErrorHelperApiLoaderFragment<Result<ResponseEntries>>
        implements AdapterView.OnItemClickListener {

     static class EntriesLoader extends ApiAsyncLoader<ResponseEntries> {

        @Inject
        GuestbookApi mGuestBookApi;

        @Inject
        public EntriesLoader(@ForActivity Context context) {
            super(context);
        }

        @Override
        protected ResponseEntries loadFromApi() throws Exception {
            return mGuestBookApi.entries().list().execute();
        }
    }

    private static final String INFO_DIALOG = "info_dialog";

    @InjectView(R.id.profile_header)
    RelativeLayout mProfileHeader;
    @InjectView(R.id.guest_information)
    RelativeLayout mGuestInformation;
    @InjectView(R.id.user_avatar)
    ImageView mUserAvatar;
    @InjectView(R.id.user_name)
    TextView mUserName;
    @InjectView(R.id.entry_button)
    Button mEntryButton;
    @InjectView(R.id.entries_list)
    ListView mEntriesList;
    @Inject
    EntryAdapter mAdapter;
    @Inject
    UserPreferences mUserPreferences;
    @Inject
    Picasso mPicasso;
    @Inject
    GoogleApiClient mGoogleApiClient;
    @Inject
    Provider<EntriesLoader> mEntriesLoaderProvider;

    private boolean mIsUserLogged;


    public static EntriesFragment newInstance() {
        return new EntriesFragment();
    }

    // TODO Change this with API.
    @Override
    protected boolean isScreenEmpty(Result<ResponseEntries> result) {
        return false;
    }

    @Override
    protected View onCreateChildView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return checkNotNull(inflater.inflate(R.layout.entries_fragment, container, false));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEntriesList.setAdapter(mAdapter);
        mEntriesList.setOnItemClickListener(this);
        mIsUserLogged = mUserPreferences.isLoggedIn();
        mGoogleApiClient.connect();
    }

    private void setUserInfo() {
        mUserName.setText(mUserPreferences.getUserName());
        mPicasso.load(Uri.parse(mUserPreferences.getUserPhoto()))
                .fit()
                .placeholder(R.drawable.no_photo)
                .error(R.drawable.no_photo)
                .into(mUserAvatar);


//        Bitmap avatar = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ava);
//
//        mUserAvatar.setImageBitmap(getRoundedBitmap(avatar));

    }

    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    @Override
    protected Loader<Result<ResponseEntries>> onCreateMainLoader(Bundle bundle) {
        return mEntriesLoaderProvider.get();
    }

    @Override
    protected void onLoadMainFinished(Result<ResponseEntries> result) {
        super.onLoadMainFinished(result);

        if (result.isSuccess()) {
            mAdapter.swapData(result.getResult().entries);
            updateUiComponents();
            if (mIsUserLogged) {
                setUserInfo();
            }
        }
    }

    private void updateUiComponents() {
        mProfileHeader.setVisibility(mIsUserLogged ? View.VISIBLE : View.GONE);
        mGuestInformation.setVisibility(mIsUserLogged ? View.GONE : View.VISIBLE);
        mEntryButton.setVisibility(mIsUserLogged ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onLoadMainReset() {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String title = mAdapter.getItem(position).title;
        Intent intent = new Intent(AppConsts.ACTION_SHOW_ENTRY_DETAIL);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    @OnClick(R.id.entry_button)
    public void onEntryClick() {
        Intent intent = new Intent(AppConsts.ACTION_SHOW_NEW_ENTRY);
        startActivity(intent);
    }

    @OnClick(R.id.logout_button)
    public void onLogoutClick() {
        mIsUserLogged = false;
        updateUiComponents();
        mUserPreferences.edit().clear().commit();
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    @OnClick(R.id.settings_button)
    public void onSettingsClick() {
        Intent intent = new Intent(AppConsts.ACTION_SHOW_SETTINGS);
        startActivity(intent);
    }

    @OnClick(R.id.refresh_button)
    public void onRefreshClick() {
        if (!getLoaderManager().hasRunningLoaders()) {
            forceReload();
        }
    }

    @OnClick(R.id.not_logged_label)
    public void onLabelClick() {
        Intent intent = new Intent(AppConsts.ACTION_SHOW_LOGIN);
        startActivity(intent);
    }

    @OnClick(R.id.not_logged_information)
    public void onInformationClick(){
        InfoDialog infoDialog = new InfoDialog();
        infoDialog.show(getFragmentManager(), INFO_DIALOG);
    }

    public class InfoDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.info_dialog_title))
                    .setMessage(getString(R.string.info_dialog_message))
                    .setPositiveButton(getString(R.string.info_dialog_dismiss_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
            return builder.create();
        }
    }
}
