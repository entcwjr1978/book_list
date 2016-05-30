package com.lightcyclesoftware.booklist.ui.home;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.GridViewPager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lightcyclesoftware.booklist.R;
import com.lightcyclesoftware.booklist.library.entity.BookModel;
import com.lightcyclesoftware.booklist.library.entity.Constants;
import com.lightcyclesoftware.booklist.ui.adapter.BookFragmentGridPagerAdapter;

import org.greenrobot.eventbus.EventBus;

import java.io.InputStream;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.google.android.gms.wearable.PutDataMapRequest.create;


public class MainActivity extends WearableActivity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GridViewPager.OnPageChangeListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final long TIMEOUT_MS = 30000;
    @Bind(R.id.grid_view_pager)
    GridViewPager mGridViewPager;
    private BookFragmentGridPagerAdapter mBookFragmentGridPagerAdapter;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();
        ButterKnife.bind(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {

        } else {

        }
    }

    private void requestImage(int index) {
        PutDataMapRequest putDataMapReq = create("/image_request");
        putDataMapReq.getDataMap().putInt(Constants.IMAGE_REQUEST, index);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Gson gson = new Gson();
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/image") == 0) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(item);
                    Asset profileAsset = dataMapItem.getDataMap().getAsset(Constants.IMAGE_DATA);
                    loadBitmapFromAsset(profileAsset);
                } else if (item.getUri().getPath().compareTo("/json_data") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    initGridPager((List<BookModel>) gson.fromJson(dataMap.getString(Constants.JSON_DATA),
                            new TypeToken<List<BookModel>>(){}
                                    .getType()));
                }
             }
            else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void initGridPager(List<BookModel> books) {
        mBookFragmentGridPagerAdapter = new BookFragmentGridPagerAdapter(getFragmentManager(), books);
        mGridViewPager.setAdapter(mBookFragmentGridPagerAdapter);
        requestImage(0);
        mGridViewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int i, int i1, float v, float v1, int i2, int i3) {

    }

    @Override
    public void onPageSelected(int i, int i1) {
        requestImage(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public void loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        // convert asset into a file descriptor and block until it's ready
        Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).setResultCallback(new ResultCallback<DataApi.GetFdForAssetResult>() {
            @Override
            public void onResult(@NonNull DataApi.GetFdForAssetResult getFdForAssetResult) {
                InputStream assetInputStream = getFdForAssetResult.getInputStream();
                if (assetInputStream == null) {
                    Log.w(TAG, "Requested an unknown Asset.");
                    return;
                }
                // decode the stream into a bitmap
                EventBus.getDefault().post(BitmapFactory.decodeStream(assetInputStream));
            }
        });
    }
}
