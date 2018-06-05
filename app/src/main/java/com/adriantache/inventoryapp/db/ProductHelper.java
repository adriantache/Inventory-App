package com.adriantache.inventoryapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.TABLE_NAME;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry._ID;

/**
 * Helper class to build the products table
 **/
public class ProductHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "inventory.db";
    private final static int DATABASE_VERSION = 1;

    ProductHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME +
                " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                COLUMN_PRICE + " INTEGER NOT NULL, " +
                COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
                COLUMN_SUPPLIER_PHONE + " TEXT);";

        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String SQL_DESTROY = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        db.execSQL(SQL_DESTROY);
        onCreate(db);
    }
}