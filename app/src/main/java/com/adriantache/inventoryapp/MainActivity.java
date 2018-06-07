package com.adriantache.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adriantache.inventoryapp.adapter.ProductCursorAdapter;

import static com.adriantache.inventoryapp.db.ProductContract.CONTENT_URI;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry._ID;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ProductCursorAdapter productCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);

        // Setup FAB to open EditorActivity
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(1, null, this).forceLoad();

        productCursorAdapter = new ProductCursorAdapter(this, null);
        listView.setAdapter(productCursorAdapter);

        TextView errorText = findViewById(R.id.errorText);
        listView.setEmptyView(errorText);
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {_ID, COLUMN_PRODUCT_NAME, COLUMN_PRICE, COLUMN_QUANTITY, COLUMN_SUPPLIER_NAME, COLUMN_SUPPLIER_PHONE};

        return new CursorLoader(this, CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        productCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        productCursorAdapter.swapCursor(null);
    }
}