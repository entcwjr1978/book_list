package com.lightcyclesoftware.booklist.service;

import com.lightcyclesoftware.booklist.entity.BookModel;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface BookRestApi {

        @GET("{path}")
        public Observable<List<BookModel>> getConfiguration(@Path(value = "path", encoded = true) String path);
}
