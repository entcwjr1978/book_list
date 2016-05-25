package com.lightcyclesoftware.booklist.service;


import com.lightcyclesoftware.booklist.entity.BookModel;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class RestApiManager {

    //book webservice call
    public static Observable<List<BookModel>> getBooks(String endpoint, String path) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(endpoint)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(httpClient.build())
                    .build();

            return retrofit.create(BookRestApi.class).getConfiguration(path);
    }
}
