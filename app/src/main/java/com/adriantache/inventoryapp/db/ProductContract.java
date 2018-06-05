package com.adriantache.inventoryapp.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract to store SQLite DB structure
 **/
public final class ProductContract {
    static final String CONTENT_AUTHORITY = "com.adriantache.inventoryapp";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_PRODUCTS = "products";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

    public ProductContract(){}

    public static class ProductEntry implements BaseColumns {
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String TABLE_NAME = "products";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME="product_name";
        public static final String COLUMN_PRICE="price";
        public static final String COLUMN_QUANTITY="quantity";
        public static final String COLUMN_SUPPLIER_NAME="supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE="supplier_phone";
    }
}