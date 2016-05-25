package com.lightcyclesoftware.booklist.ui.home;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.lightcyclesoftware.booklist.R;
import com.lightcyclesoftware.booklist.entity.BookModel;
import com.lightcyclesoftware.booklist.service.RestApiManager;
import com.lightcyclesoftware.booklist.ui.adapter.BookAdapter;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.book_list)
    RecyclerView mBookRecyclerView;

    private BookAdapter adapter;
    private Subscription mBookSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //get the book data
        mBookSubscription =  RestApiManager
                .getBooks(getString(R.string.endpoint),getString(R.string.path))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onNext, this::onFailure, this::onSuccess);
    }

    private void onFailure(@NonNull Throwable thowable) {
        System.out.println(thowable.getMessage());
        mBookSubscription.unsubscribe();
    }

    private void onNext(List<BookModel> books) {
        //setup the recyclerview
        adapter = new BookAdapter(books);
        mBookRecyclerView.setAdapter(adapter);
        mBookRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void onSuccess() {
        mBookSubscription.unsubscribe();
    }
}
