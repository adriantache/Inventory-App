package com.adriantache.inventoryapp.db;

import android.provider.BaseColumns;

/**
 * Contract to store SQLite DB structure
 **/
public final class ProductContract {

    public ProductContract(){}

    public static class ProductEntry implements BaseColumns {

        public static final String TABLE_NAME = "products";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME="product_name";
        public static final String COLUMN_PRICE="price";
        public static final String COLUMN_QUANTITY="quantity";
        public static final String COLUMN_SUPPLIER_NAME="supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE="supplier_phone";
    }
}