package com.example.android.bookstoreapp.data;

import android.provider.BaseColumns;

/**
 * API Contract for the Book Store App.
 */

public class BookContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BookContract() {
    }

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public static final class BookEntry implements BaseColumns {

        /**
         * Name of database table for books
         */
        public final static String TABLE_NAME = "books";

        /**
         * Unique ID number for the book (only for use in the database table).
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the book.
         * Type: TEXT
         */
        public final static String COLUMN_BOOK_NAME = "name";

        /**
         * Price of the book.
         * Type: INTEGER
         */
        public final static String COLUMN_BOOK_PRICE = "price";

        /**
         * Quantity of the book.
         * Type: INTEGER
         */
        public final static String COLUMN_BOOK_QUANTITY = "quantity";

        /**
         * Supplier name.
         * Type: TEXT
         */
        public final static String COLUMN_BOOK_SUPPLIER = "supplier";

        /**
         * Supplier phone number.
         * Type: TEXT
         */
        public final static String COLUMN_BOOK_PHONE = "phone";
    }

}
