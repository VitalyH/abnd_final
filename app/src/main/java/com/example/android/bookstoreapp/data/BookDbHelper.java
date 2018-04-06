package com.example.android.bookstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;


/**
 * Database helper for Book Store app. Manages database creation and version management.
 */

public class BookDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "store.db";

    /**
     * Database version. Increment this after schema change.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Database upgrade statement. Keep it next to the version for easier access.
     */

    private static final String DATABASE_UPGRADE_TO_V2 = "ALTER TABLE "
            + BookEntry.TABLE_NAME + " ADD COLUMN " + BookEntry.COLUMN_BOOK_AUTHOR + " TEXT ";

    /**
     * Constructs a new instance of {@link BookDbHelper}.
     *
     * @param context of the app
     */
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the books table
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
               // + BookEntry.COLUMN_BOOK_AUTHOR + " TEXT, " // V2 Uncomment this if BookDb.Helper.DATABASE_VERSION = 2
                + BookEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 1, "
                + BookEntry.COLUMN_BOOK_SUPPLIER + " TEXT NOT NULL, "
                + BookEntry.COLUMN_BOOK_PHONE + " TEXT NOT NULL )";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    // More powerful and elegant implementation - https://riggaroo.co.za/android-sqlite-database-use-onupgrade-correctly/
    // Waaaay other my head for now. Keeping link here for myself - something to try in the future.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(DATABASE_UPGRADE_TO_V2);
        }
    }
}