package com.adriantache.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.adriantache.inventoryapp.db.ProductHelper;

import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.TABLE_NAME;

public class MainActivity extends AppCompatActivity {
    private ProductHelper productHelper;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text_view);

        //todo maybe move this?
        productHelper = new ProductHelper(this);

        //todo remove these test calls after Udacity review
        //insert some test data (will probably get duplicated, who cares)
        insertData("Product",1.11f,10,"Supplier","004 0777 777 777");
        //read the data to update the TextView
        readData();
    }

    //todo do something with the data
    private void readData() {
        SQLiteDatabase db = productHelper.getReadableDatabase();

        String[] columns = {COLUMN_PRODUCT_NAME,COLUMN_PRICE,COLUMN_QUANTITY,COLUMN_SUPPLIER_NAME,COLUMN_SUPPLIER_PHONE};

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);

            int count = cursor.getCount();
            textView.setText(String.valueOf(count));
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    //return result to detect errors (-1)
    //todo checks for empty fields in method calling this method
    private long insertData(String product_name, float price, int quantity, String supplier_name, String supplier_phone) {
        SQLiteDatabase db = productHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, product_name);
        //multiply price by 100 to store it as int in the database
        values.put(COLUMN_PRICE, (int) price*100);
        values.put(COLUMN_QUANTITY,quantity);
        values.put(COLUMN_SUPPLIER_NAME,supplier_name);
        values.put(COLUMN_SUPPLIER_PHONE,supplier_phone);

        return db.insert(TABLE_NAME, null, values);
    }
}