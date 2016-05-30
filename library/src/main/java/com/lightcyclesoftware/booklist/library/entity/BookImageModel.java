package com.lightcyclesoftware.booklist.library.entity;

import android.graphics.Bitmap;

public class BookImageModel {
    private int index = -1;
    private Bitmap bitmap = null;

    public int getIndex() {
        return index;
    }

    public BookImageModel setIndex(int index) {
        this.index = index;
        return this;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public BookImageModel setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        return this;
    }
}
