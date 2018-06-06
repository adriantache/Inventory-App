package com.adriantache.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import static com.adriantache.inventoryapp.db.ProductContract.CONTENT_URI;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ERROR_VALUE = -1;
    private static final int MISSING_FIELD = -10;


    private EditText productName;
    private EditText productPrice;
    private TextView productQuantity;
    private EditText supplierName;
    private EditText supplierPhone;
    private Button deleteProduct;
    private Button callSupplier;
    private String supplierPhoneNumber;

    private Uri currentProductUri = null;
    private boolean valuesChanged = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            valuesChanged = true;

            if (event.getAction() == MotionEvent.ACTION_UP)
                v.performClick();

            return false;
        }
    };

    @Override
    public void onBackPressed() {
        if (!valuesChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        EditorActivity.this.finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You have unsaved changes.");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productQuantity = findViewById(R.id.productQuantity);
        ImageButton quantityMinus = findViewById(R.id.quantityMinus);
        ImageButton quantityPlus = findViewById(R.id.quantityPlus);
        supplierName = findViewById(R.id.supplierName);
        supplierPhone = findViewById(R.id.supplierPhone);
        deleteProduct = findViewById(R.id.deleteProduct);
        callSupplier = findViewById(R.id.callSupplier);

        currentProductUri = getIntent().getData();

        if (currentProductUri == null) {
            setTitle("Add a Product");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Product");
            getSupportLoaderManager().initLoader(1, null, this);
        }

        productName.setOnTouchListener(touchListener);
        productPrice.setOnTouchListener(touchListener);
        productQuantity.setOnTouchListener(touchListener);
        quantityMinus.setOnTouchListener(touchListener);
        quantityPlus.setOnTouchListener(touchListener);
        supplierName.setOnTouchListener(touchListener);
        supplierPhone.setOnTouchListener(touchListener);
        deleteProduct.setOnTouchListener(touchListener);

        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentProductUri != null) {
                    showDeleteConfirmationDialog();
                }
            }
        });

        //todo figure out why this is incrementing/decrementing twice
        quantityMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.valueOf(productQuantity.getText().toString());
                if (currentProductUri == null && quantity > 1) quantity--;
                else if (quantity > 0) quantity--;
                productQuantity.setText(String.valueOf(quantity));
            }
        });

        quantityPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.valueOf(productQuantity.getText().toString());
                quantity++;
                productQuantity.setText(String.valueOf(quantity));
            }
        });

        callSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                if (TextUtils.isEmpty(supplierPhoneNumber))
                    supplierPhoneNumber = supplierPhone.getText().toString();
                intent.setData(Uri.parse("tel:" + supplierPhoneNumber));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (currentProductUri == null) {
            deleteProduct = findViewById(R.id.deleteProduct);
            deleteProduct.setVisibility(View.INVISIBLE);
            callSupplier = findViewById(R.id.callSupplier);
            callSupplier.setVisibility(View.INVISIBLE);
        }
        return true;
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
        if (item.getItemId() == R.id.action_save) {
            if (currentProductUri == null) {
                int insertResult = insertData();

                if (insertResult == ERROR_VALUE)
                    Toast.makeText(this, "Error inserting data!", Toast.LENGTH_SHORT).show();
                else if (insertResult == MISSING_FIELD)
                    Toast.makeText(this, "Missing info!", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(this, "Successfully added product.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                int updateResult = updateData();

                if (updateResult == ERROR_VALUE)
                    Toast.makeText(this, "Error updating data!", Toast.LENGTH_SHORT).show();
                else if (updateResult == MISSING_FIELD)
                    Toast.makeText(this, "Missing info!", Toast.LENGTH_SHORT).show();
                else if (!valuesChanged) {
                    Toast.makeText(this, "Nothing changed.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Successfully edited product.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            if (!valuesChanged) {
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                return true;
            }

            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, navigate to parent activity.
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        }
                    };


            showUnsavedChangesDialog(discardButtonClickListener);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int insertData() {
        String pName = productName.getText().toString().trim();
        if (TextUtils.isEmpty(pName)) return -1;

        Integer pPrice = ERROR_VALUE;
        try {
            String price = productPrice.getText().toString().trim();
            if (TextUtils.isEmpty(price)) pPrice = 0;
            else pPrice = Integer.valueOf(price);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ERROR_VALUE;
        }

        Integer pQuantity = ERROR_VALUE;
        try {
            String quantity = productQuantity.getText().toString().trim();
            if (Integer.valueOf(quantity) < 1) pQuantity = 1;
            else pQuantity = Integer.valueOf(quantity);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ERROR_VALUE;
        }

        String sName = supplierName.getText().toString().trim();
        if (TextUtils.isEmpty(sName)) return -1;

        String sPhone = supplierPhone.getText().toString().trim();

        if (TextUtils.isEmpty(pName) || pPrice == ERROR_VALUE || pQuantity == ERROR_VALUE || TextUtils.isEmpty(sName) || TextUtils.isEmpty(sPhone))
            return MISSING_FIELD;

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, pName);
        values.put(COLUMN_PRICE, pPrice);
        values.put(COLUMN_QUANTITY, pQuantity);
        values.put(COLUMN_SUPPLIER_NAME, sName);
        values.put(COLUMN_SUPPLIER_PHONE, sPhone);

        if (getContentResolver().insert(CONTENT_URI, values) == null) return ERROR_VALUE;
        else return 1;
    }


    private int updateData() {
        String pName = productName.getText().toString().trim();
        if (TextUtils.isEmpty(pName)) return -1;

        int pPrice = ERROR_VALUE;
        try {
            String price = productPrice.getText().toString().trim();
            if (TextUtils.isEmpty(price)) pPrice = 0;
            else pPrice = Integer.valueOf(price);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ERROR_VALUE;
        }

        int pQuantity = ERROR_VALUE;
        try {
            String quantity = productQuantity.getText().toString().trim();
            if (TextUtils.isEmpty(quantity)) pQuantity = 0;
            else pQuantity = Integer.valueOf(quantity);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ERROR_VALUE;
        }

        String sName = supplierName.getText().toString().trim();
        if (TextUtils.isEmpty(sName)) return -1;

        String sPhone = supplierPhone.getText().toString().trim();

        if (TextUtils.isEmpty(pName) || pPrice == ERROR_VALUE || pQuantity == ERROR_VALUE || TextUtils.isEmpty(sName) || TextUtils.isEmpty(sPhone))
            return MISSING_FIELD;

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, pName);
        values.put(COLUMN_PRICE, pPrice);
        values.put(COLUMN_QUANTITY, pQuantity);
        values.put(COLUMN_SUPPLIER_NAME, sName);
        values.put(COLUMN_SUPPLIER_PHONE, sPhone);

        if (getContentResolver().update(currentProductUri, values, null, null) == 0)
            return ERROR_VALUE;
        else return 1;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Really delete product?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditorActivity.this.deleteData();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteData() {
        if (getContentResolver().delete(currentProductUri, null, null) == 0)
            Toast.makeText(this, "Error deleting product!", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(this, "Successfully deleted product.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, currentProductUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() == 0) {//do nothing
        } else {
            data.moveToPosition(0);
            productName.setText(data.getString(data.getColumnIndex(COLUMN_PRODUCT_NAME)));
            productPrice.setText(String.valueOf(data.getInt(data.getColumnIndex(COLUMN_PRICE))));
            productQuantity.setText(String.valueOf(data.getInt(data.getColumnIndex(COLUMN_QUANTITY))));
            supplierName.setText(data.getString(data.getColumnIndex(COLUMN_SUPPLIER_NAME)));
            supplierPhoneNumber = data.getString(data.getColumnIndex(COLUMN_SUPPLIER_PHONE));
            supplierPhone.setText(supplierPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        productName.setText("");
        productPrice.setText("");
        productQuantity.setText("1");
        supplierName.setText("");
        supplierPhoneNumber = "";
        supplierPhone.setText("");
    }
}