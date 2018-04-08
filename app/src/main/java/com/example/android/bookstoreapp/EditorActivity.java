package com.example.android.bookstoreapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;
import com.example.android.bookstoreapp.data.BookDbHelper;

/**
 * Allows user to create a new bookor edit an existing one.
 */

public class EditorActivity extends AppCompatActivity {

    /**
     * EditText field to enter the book's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the book's price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the book's quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the books supplier's name
     */
    private EditText mSupplierEditText;

    /**
     * EditText field to enter the books supplier's phone
     */
    private EditText mPhoneEditText;

    /**
     * Boolean variable which helps to check if any fields was left empty
     */
    private boolean isNullInput = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_book_name);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mQuantityEditText = findViewById(R.id.edit_book_quantity);
        mSupplierEditText = findViewById(R.id.edit_book_supplier);
        mPhoneEditText = findViewById(R.id.edit_book_phone);
    }

    /**
     * Get user input from editor and save new book into database.
     */
    private void insertBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();

        // Check empty fields
        if (TextUtils.isEmpty(nameString)
                || TextUtils.isEmpty(priceString)
                || TextUtils.isEmpty(quantityString)
                || TextUtils.isEmpty(supplierString)
                || TextUtils.isEmpty(phoneString)) {
            // Show Toast with error message
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.error_empty_field), Toast.LENGTH_LONG).show();
            // Set to "true" and block exit activity in @Link onOptionsItemSelected
            isNullInput = true;
            return;
        } else {
            // Set to "false" if user filled in all fields
            // We need this in case "if" above was triggered and isNullInput was set to true
            isNullInput = false;
        }

        // Convert Strings
        double priceDouble = Double.parseDouble(priceString);
        int quantityInt = Integer.parseInt(quantityString);

        // Create database helper
        BookDbHelper mDbHelper = new BookDbHelper(this);

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookEntry.COLUMN_BOOK_PRICE, priceDouble);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantityInt);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, supplierString);
        values.put(BookEntry.COLUMN_BOOK_PHONE, phoneString);

        // Insert a new row for the book in the database, returning the ID of that new row.
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving the book", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Book saved with the id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save the book to database
                insertBook();
                // Exit activity if all fields was filled in
                if (!isNullInput) {
                    finish();
                    return true;
                }
                // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (StoreActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}