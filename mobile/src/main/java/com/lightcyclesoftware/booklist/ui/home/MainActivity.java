package com.lightcyclesoftware.booklist.ui.home;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.lightcyclesoftware.booklist.R;
import com.lightcyclesoftware.booklist.library.entity.BookModel;
import com.lightcyclesoftware.booklist.library.entity.Constants;
import com.lightcyclesoftware.booklist.service.RestApiManager;
import com.lightcyclesoftware.booklist.ui.adapter.BookAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.google.android.gms.wearable.PutDataMapRequest.create;

public class MainActivity extends AppCompatActivity implements DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    @Bind(R.id.book_list)
    RecyclerView mBookRecyclerView;

    private BookAdapter mAdapter;
    private List<BookModel> mBooks;
    private Subscription mBookSubscription;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    private void onFailure(@NonNull Throwable thowable) {
        System.out.println(thowable.getMessage());
        mBookSubscription.unsubscribe();
    }

    private void onNext(List<BookModel> books) {
        Gson gson = new Gson();
        //setup the recyclerview
        mBooks = books;
        mAdapter = new BookAdapter(mBooks);
        mBookRecyclerView.setAdapter(mAdapter);
        mBookRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //send base book data to watch
        PutDataMapRequest putDataMapReq = create("/json_data");
        putDataMapReq.getDataMap().putString(Constants.FORCE_REFRESH, UUID.randomUUID().toString());
        putDataMapReq.getDataMap().putString(Constants.JSON_DATA, gson.toJson(Arrays.copyOfRange(books.toArray(), 0, 25)));
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        putDataReq.setUrgent();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                Log.d(TAG, dataItemResult.getDataItem().toString());
            }
        });
    }

    private void onSuccess() {
        mBookSubscription.unsubscribe();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        //get the book data
        mBookSubscription =  RestApiManager
                .getBooks(getString(R.string.endpoint),getString(R.string.path))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onNext, this::onFailure, this::onSuccess);
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
                if (item.getUri().getPath().compareTo("/image_request") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    int index = dataMap.getInt(Constants.IMAGE_REQUEST);
                    Picasso.with(this)
                            .load(mBooks.get(index).getImageURL())
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                                    Asset asset = createAssetFromBitmap(bitmap);
                                    PutDataMapRequest dataMap = PutDataMapRequest.create("/image");
                                    dataMap.getDataMap().putAsset(Constants.IMAGE_DATA, asset);
                                    PutDataRequest request = dataMap.asPutDataRequest();
                                    request.setUrgent();

                                    PendingResult<DataApi.DataItemResult> pendingResult =
                                            Wearable.DataApi.putDataItem(mGoogleApiClient, request);

                                    pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                                        @Override
                                        public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                                            Log.d(TAG, dataItemResult.getDataItem().toString());
                                        }
                                    });
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
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

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }
}
