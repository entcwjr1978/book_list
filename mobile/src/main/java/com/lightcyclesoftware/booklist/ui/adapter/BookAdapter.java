package com.lightcyclesoftware.booklist.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lightcyclesoftware.booklist.R;
import com.lightcyclesoftware.booklist.entity.BookModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private List<BookModel> mBooks;
    private Context mContext;
    private int defaultColor;

    public BookAdapter(List<BookModel> books) {
        mBooks = books;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.book_item_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookModel book = mBooks.get(position);

        // Set item views based on the data model
        TextView titleText = holder.titleText;
        TextView authorText = holder.authorText;
        ImageView bookImage = holder.bookImage;
        PercentRelativeLayout listItemLayout = holder.listItemLayout;

        if (book.getTitle() != null) {
            titleText.setText(book.getTitle());
        }

        if (book.getAuthor() != null) {
            authorText.setText(book.getAuthor());
        }

        if (book.getImageURL() != null) {
            defaultColor = ContextCompat.getColor(mContext, android.R.color.black);
            Picasso.with(mContext)
                    .load(book.getImageURL())
                    .into(bookImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable)bookImage.getDrawable()).getBitmap();
                            Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette palette) {
                                    int mutedDark = palette.getDarkMutedColor(defaultColor);
                                    listItemLayout.setBackgroundColor(mutedDark);
                                }
                            };
                            Palette.from(bitmap).generate(paletteListener);
                        }

                        @Override
                        public void onError() {
                            listItemLayout.setBackgroundColor(defaultColor);
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.title_text)
        public TextView titleText;

        @Bind(R.id.author_text)
        public TextView authorText;

        @Bind(R.id.book_image)
        public ImageView bookImage;

        @Bind(R.id.list_item)
        public PercentRelativeLayout listItemLayout;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
