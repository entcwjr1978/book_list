package com.lightcyclesoftware.booklist.ui.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.FragmentGridPagerAdapter;

import com.lightcyclesoftware.booklist.library.entity.BookModel;
import com.lightcyclesoftware.booklist.ui.home.BookCardFragment;

import java.util.List;

public class BookFragmentGridPagerAdapter extends FragmentGridPagerAdapter {

   private List<BookModel> mBooks;

    public BookFragmentGridPagerAdapter(FragmentManager fm, List<BookModel> books) {
        super(fm);
        mBooks = books;
    }

    @Override
    public Fragment getFragment(int i, int i1) {
        return BookCardFragment.newInstance(mBooks.get(i));
    }

    @Override
    public int getRowCount() {
        return mBooks.size();
    }

    @Override
    public int getColumnCount(int i) {
        return 1;
    }
}
