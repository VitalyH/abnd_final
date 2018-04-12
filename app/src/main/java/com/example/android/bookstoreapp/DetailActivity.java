package com.example.android.bookstoreapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

import java.util.List;

/**
 * Allows the user to view details of the book, change quantity and initiate a call to the supplier.
 */

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri mCurrentBookUri;

    /**
     * EditText field to enter the book's name
     */
    private TextView mNameDetailText;

    /**
     * EditText field to enter the book's price
     */
    private TextView mPriceDetailText;

    /**
     * EditText field to enter the book's quantity
     */
    private TextView mQuantityDetailText;

    /**
     * EditText field to enter the books supplier's name
     */
    private TextView mSupplierDetailText;

    /**
     * EditText field to enter the books supplier's phone
     */
    private TextView mPhoneDetailText;

    /**
     * Helping variables for buttons in this activity
     */
    int mQuantity;                      // Quantity of the books in the store
    boolean mQuantityChanged = false;   // Check wherever quantity was changed at all
    String mPhoneNumber;                // Phone number for using in the DIAL intent


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Examine the intent that was used to launch this activity
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // Initialize a loader to read the book data from the database
        // and display the current values in the editor
        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);

        // Find all relevant views that we will need to read user input from
        mNameDetailText = findViewById(R.id.detail_book_name);
        mPriceDetailText = findViewById(R.id.detail_book_price);
        mQuantityDetailText = findViewById(R.id.detail_book_quantity);
        mSupplierDetailText = findViewById(R.id.detail_book_supplier);
        mPhoneDetailText = findViewById(R.id.detail_book_phone);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_detail.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Edit" menu option
            case R.id.action_edit:
                Intent intent = new Intent(DetailActivity.this, EditorActivity.class);
                // Set the URI on the data field of the intent
                intent.setData(mCurrentBookUri);
                // Update quantity before exiting
                updateQuantity();
                // Launch the {@link EditorActivity} to display the data for the current book.
                startActivity(intent);
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Update quantity before exiting
                updateQuantity();
                // Continue with navigating up to parent activity
                // which is the {@link StoreActivity}.
                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // Update quantity before exiting
        updateQuantity();
        super.onBackPressed();
    }

    /**
     * Loader methods.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the activity shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER,
                BookEntry.COLUMN_BOOK_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current book
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);

            // Updating helping variables
            mQuantity = quantity;
            mPhoneNumber = phone;

            // Update the views on the screen with the values from the database
            mNameDetailText.setText(name);
            mPriceDetailText.setText(String.valueOf(price));
            mQuantityDetailText.setText(String.valueOf(quantity));
            mSupplierDetailText.setText(supplier);
            mPhoneDetailText.setText(phone);
        }

        // Buttons which change quantity
        // For increasing quantity.
        final TextView plusQuantity = findViewById(R.id.detail_plus_quantity);
        plusQuantity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Increase quantity of books by one
                mQuantity++;
                // Show new quantity
                mQuantityDetailText.setText(String.valueOf(mQuantity));
                // Change variable for @Link updateQuantity
                mQuantityChanged = true;
            }
        });

        // For decreasing quantity.
        final TextView minusQuantity = findViewById(R.id.detail_minus_quantity);
        minusQuantity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mQuantity > 0) {
                    // Decrease quantity of books by one
                    mQuantity--;
                    // Show new quantity
                    mQuantityDetailText.setText(String.valueOf(mQuantity));
                    // Change variable for @Link updateQuantity
                    mQuantityChanged = true;
                    // Quantity couldn't be negative, so do not proceed and show user a warning
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.detail_activity_bellow_zero),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Button to make an order (phone call to the supplier)
        final TextView makeOrder = findViewById(R.id.detail_make_order);
        makeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use DIAL instead of CALL so user have an opportunity to confirm the call
                // Also we need less permissions on OS level this way
                Intent dialIntent = new Intent(Intent.ACTION_DIAL,
                        Uri.fromParts("tel", mPhoneNumber, null));

                // Check whatever is an activity available that can respond to the intent
                // (Phone app usually installed but if not, app will crash)
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(dialIntent,
                        PackageManager.MATCH_DEFAULT_ONLY);
                boolean isIntentSafe = activities.size() > 0;

                if (isIntentSafe) {
                    // Send the intent to launch a new activity
                    startActivity(dialIntent);
                } else {
                    // If there is no phone app installed
                    // Show Toast message with warning
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.detail_activity_no_phone), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // Call the ContentResolver to delete the book at the given content URI.
        // Pass in null for the selection and selection args because the mCurrentBookUri
        // content URI already identifies the book that we want.
        int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                    Toast.LENGTH_SHORT).show();
        }

        // Close the activity
        finish();
    }

    /**
     * Update quantity of the book
     */
    private void updateQuantity() {
        // Check if mQuantity was changed
        if (!mQuantityChanged) {
            // Since field wasn't modified, we can return early without updating anything
            return;
        }

        // Read from the field
        // Create a ContentValues object
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_QUANTITY,
                Integer.parseInt(mQuantityDetailText.getText().toString()));

        // Update the book with content URI: mCurrentBookUri
        // and pass in the new ContentValues. Pass in null for the selection and selection args
        // because mCurrentBookUri will already identify the correct row in the database that
        // we want to modify.
        getContentResolver().update(mCurrentBookUri, values, null, null);
    }
}