package com.example.android.bookstoreapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */

public class BookCursorAdapter extends CursorAdapter {

    /**
     * Initialize ViewHolder as a cache mechanism for storing Views
     */
    public static class ViewHolder {
        TextView nameView;
        TextView priceView;
        TextView quantityView;
        Button saleButton;
    }

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Create new ViewHolder for text views.
        final ViewHolder holder = new ViewHolder();

        // Find individual views that we want to modify in the list item layout
        holder.nameView = view.findViewById(R.id.list_name);
        holder.priceView = view.findViewById(R.id.list_price);
        holder.quantityView = view.findViewById(R.id.list_quantity);

        // Find the columns of book attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);

        // Read the book attributes from the Cursor for the current book
        final String bookID = cursor.getString(idColumnIndex);
        final String bookName = cursor.getString(nameColumnIndex);
        final String bookPrice = cursor.getString(priceColumnIndex);
        final String bookQuantity = cursor.getString(quantityColumnIndex);

        // Update the TextViews with the attributes for the current book
        holder.nameView.setText(bookName);
        holder.priceView.setText(bookPrice);
        holder.quantityView.setText(bookQuantity);

        // Find the Sale button and setup ClickListener
        holder.saleButton = view.findViewById(R.id.list_sale);
        holder.saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get quantity
                String quantityString = holder.quantityView.getText().toString();
                int quantityInt = Integer.parseInt(quantityString);

                // Reduce quantity by one
                if (quantityInt > 0) {
                    quantityInt--;

                    // Update quantity in TextView
                    quantityString = Integer.toString(quantityInt);
                    holder.quantityView.setText(quantityString);

                    // Update new quantity in DB
                    Uri currentBookUri =
                            ContentUris.withAppendedId(BookEntry.CONTENT_URI, Long.valueOf(bookID));
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantityInt);
                    context.getContentResolver().update(currentBookUri, values, null, null);
                }
            }
        });
    }

}
