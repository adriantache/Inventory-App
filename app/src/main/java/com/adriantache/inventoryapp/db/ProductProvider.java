package com.adriantache.inventoryapp.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.adriantache.inventoryapp.db.ProductContract.CONTENT_AUTHORITY;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.TABLE_NAME;

/**
 * ContentProvider for database access
 **/
public class ProductProvider extends ContentProvider {
    public static final int ERROR_VALUE = -1;
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(CONTENT_AUTHORITY, "products", PRODUCTS);
        uriMatcher.addURI(CONTENT_AUTHORITY, "products/#", PRODUCT_ID);
    }

    private ProductHelper productHelper;

    @Override
    public boolean onCreate() {
        productHelper = new ProductHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = productHelper.getReadableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case PRODUCTS:
                cursor = db.query(TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case PRODUCTS:
                SQLiteDatabase db = productHelper.getWritableDatabase();

                long id = ERROR_VALUE;
                if (testValues(values))
                    id = db.insert(TABLE_NAME, null, values);

                if (id == ERROR_VALUE)
                    return null;

                getContext().getContentResolver().notifyChange(uri, null);

                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Cannot insert unknown URI" + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int deletedRows;

        switch (uriMatcher.match(uri)) {
            case PRODUCTS:
                SQLiteDatabase db = productHelper.getWritableDatabase();
                deletedRows = db.delete(TABLE_NAME, selection, selectionArgs);
                if (deletedRows != 0) getContext().getContentResolver().notifyChange(uri, null);
                return deletedRows;
            case PRODUCT_ID:
                SQLiteDatabase db2 = productHelper.getWritableDatabase();
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = db2.delete(TABLE_NAME, selection, selectionArgs);
                if (deletedRows != 0) getContext().getContentResolver().notifyChange(uri, null);
                return deletedRows;
            default:
                throw new IllegalArgumentException("Cannot delete unknown URI" + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case PRODUCTS:
                SQLiteDatabase db = productHelper.getWritableDatabase();

                int updateRows;

                if (testValues(values))
                    updateRows = db.update(TABLE_NAME, values, selection, selectionArgs);
                else
                    return ERROR_VALUE;

                if (updateRows != 0) getContext().getContentResolver().notifyChange(uri, null);

                return updateRows;
            case PRODUCT_ID:
                SQLiteDatabase db2 = productHelper.getWritableDatabase();

                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                int updateRows2;

                if (testValues(values))
                    updateRows2 = db2.update(TABLE_NAME, values, selection, selectionArgs);
                else
                    return ERROR_VALUE;

                if (updateRows2 != 0) getContext().getContentResolver().notifyChange(uri, null);

                return updateRows2;
            default:
                throw new IllegalArgumentException("Cannot update unknown URI" + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + uriMatcher.match(uri));
        }
    }

    private boolean testValues(@Nullable ContentValues values) {
        if (values == null) throw new IllegalArgumentException("No values received");

        String name = values.getAsString(COLUMN_PRODUCT_NAME);
        if (name == null)
            throw new IllegalArgumentException("Product requires a name");

        int price = values.getAsInteger(COLUMN_PRICE);
        if (price<=0)
            throw new IllegalArgumentException("Product price cannot be less than 1");

        int quantity = values.getAsInteger(COLUMN_QUANTITY);
        if (quantity<0)
            throw new IllegalArgumentException("Product quantity cannot be less than 0");

        String sName = values.getAsString(COLUMN_SUPPLIER_NAME);
        if (sName == null)
            throw new IllegalArgumentException("Supplier requires a name");

        //supplier phone can be empty, theoretically, although the editor won't let them

        return true;
    }
}

