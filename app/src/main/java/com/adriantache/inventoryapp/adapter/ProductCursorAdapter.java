package com.adriantache.inventoryapp.adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.adriantache.inventoryapp.EditorActivity;
import com.adriantache.inventoryapp.R;

import static com.adriantache.inventoryapp.db.ProductContract.CONTENT_URI;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry._ID;

/**
 * CursorAdapter implementation
 **/
public class ProductCursorAdapter extends CursorAdapter {
    private int id;
    private Context context;

    private View.OnClickListener itemView = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //add intent for editing entry
            Intent intent = new Intent(context, EditorActivity.class);
            final Uri uri = ContentUris.withAppendedId(CONTENT_URI, id);
            intent.setData(uri);
            context.startActivity(intent);
        }
    };

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView productName = view.findViewById(R.id.productName);
        TextView productPrice = view.findViewById(R.id.productPrice);
        TextView productQuantity = view.findViewById(R.id.productQuantity);

        final String name = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME));
        final int price = cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE));
        this.context = context;

        productName.setText(name);
        productPrice.setText(String.valueOf(price));

        id = cursor.getInt(cursor.getColumnIndex(_ID));

        final int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
        String quantityText = context.getString(R.string.quantity_label)
                + String.valueOf(quantity);
        productQuantity.setText(quantityText);

        Button sale = view.findViewById(R.id.sale);

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, name);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_SUPPLIER_NAME, cursor.getString(cursor.getColumnIndex(COLUMN_SUPPLIER_NAME)));
        values.put(COLUMN_SUPPLIER_PHONE, cursor.getString(cursor.getColumnIndex(COLUMN_SUPPLIER_PHONE)));

        productName.setOnClickListener(itemView);
        productPrice.setOnClickListener(itemView);
        productQuantity.setOnClickListener(itemView);
        setOnClick(sale, quantity, id, values);
    }

    private void setOnClick(View view, final int originalQuantity, final int id, final ContentValues values) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = originalQuantity;

                if (quantity < 0)
                    Toast.makeText(context, "Could not update quantity!", Toast.LENGTH_SHORT).show();
                else {
                    if (quantity > 0) quantity--;

                    updateQuantity(quantity, id, values);
                }
            }
        });
    }

    private void updateQuantity(int quantity, int id, ContentValues values) {
        values.put(COLUMN_QUANTITY, quantity);

        context.getContentResolver()
                .update(ContentUris.withAppendedId(CONTENT_URI, id), values, null, null);
    }
}