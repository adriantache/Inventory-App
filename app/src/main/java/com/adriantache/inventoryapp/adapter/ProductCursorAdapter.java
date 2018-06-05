package com.adriantache.inventoryapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.adriantache.inventoryapp.R;

import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_QUANTITY;

/**
 * CursorAdapter implementation
 **/
public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView productName = view.findViewById(R.id.productName);
        TextView productPrice = view.findViewById(R.id.productPrice);
        TextView productQuantity = view.findViewById(R.id.productQuantity);

        productName.setText(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME)));
        productPrice.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE))));

        String quantityText = context.getString(R.string.quantity_label)
                + String.valueOf(cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)));
        productQuantity.setText(quantityText);
    }
}