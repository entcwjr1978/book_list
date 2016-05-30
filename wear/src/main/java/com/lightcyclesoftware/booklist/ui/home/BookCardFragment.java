package com.lightcyclesoftware.booklist.ui.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.wearable.view.CardFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lightcyclesoftware.booklist.R;
import com.lightcyclesoftware.booklist.library.entity.BookModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;


public class BookCardFragment extends CardFragment {

    private static final String ARG_PARAM1 = "param1";

    @Bind(R.id.title_text)
    public TextView titleText;

    @Bind(R.id.author_text)
    public TextView authorText;

    @Bind(R.id.book_image)
    public ImageView bookImage;

    @Bind(R.id.list_item)
    public PercentRelativeLayout listItemLayout;

    private BookModel mBookModel;
    private int defaultColor;

    @Override
    public View onCreateContentView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.book_item_layout, viewGroup, false);
        ButterKnife.bind(this, view);

        if (mBookModel.getTitle() != null) {
            titleText.setText(mBookModel.getTitle());
        }

        if (mBookModel.getAuthor() != null) {
            authorText.setText(mBookModel.getAuthor());
        }

        return view;
    }

    public static BookCardFragment newInstance(Object bookModel) {
        Gson gson = new Gson();
        BookCardFragment fragment = new BookCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, gson.toJson(bookModel));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        Gson gson = new Gson();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBookModel = gson.fromJson(getArguments().getString(ARG_PARAM1), BookModel.class);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Bitmap bitmap) {
        bookImage.setImageBitmap(bitmap);
        defaultColor = ContextCompat.getColor(getActivity(), android.R.color.black);
        Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                int mutedDark = palette.getDarkMutedColor(defaultColor);
                listItemLayout.setBackgroundColor(mutedDark);
            }
        };
        Palette.from(bitmap).generate(paletteListener);
    }
}
