package com.lightcyclesoftware.booklist.library.event;

import com.lightcyclesoftware.booklist.library.entity.BookImageModel;

public class ImageLoadedEvent {
    private BookImageModel bookImageModel;

    public static ImageLoadedEvent newInstance(BookImageModel bookImageModel) {
        ImageLoadedEvent imageLoadedEvent = new ImageLoadedEvent();
        imageLoadedEvent.setBookImageModel(bookImageModel);

        return imageLoadedEvent;
    }

    public BookImageModel getBookImageModel() {
        return bookImageModel;
    }

    public ImageLoadedEvent setBookImageModel(BookImageModel bookImageModel) {
        this.bookImageModel = bookImageModel;
        return this;
    }
}
