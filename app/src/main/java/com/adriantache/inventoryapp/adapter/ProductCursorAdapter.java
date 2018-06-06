package com.adriantache.inventoryapp.adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.adriantache.inventoryapp.MainActivity;
import com.adriantache.inventoryapp.R;

import static com.adriantache.inventoryapp.db.ProductContract.CONTENT_URI;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.adriantache.inventoryapp.db.ProductContract.ProductEntry._ID;

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
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView productName = view.findViewById(R.id.productName);
        TextView productPrice = view.findViewById(R.id.productPrice);
        TextView productQuantity = view.findViewById(R.id.productQuantity);

        final Context context1 = context;

        productName.setText(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME)));
        productPrice.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE))));

        final int id = cursor.getInt(cursor.getColumnIndex(_ID));

        final int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
        String quantityText = context.getString(R.string.quantity_label)
                + String.valueOf(quantity);
        productQuantity.setText(quantityText);

        //onClickListener for SALE button
        //todo make this work, too tired now
        Button sale = view.findViewById(R.id.sale);
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] projection = {_ID, COLUMN_QUANTITY};
                int quantity1 = quantity;

                Uri uri = ContentUris.withAppendedId(CONTENT_URI, id);

                if (quantity1 < 0)
                    Toast.makeText(context1, "Could not update quantity!", Toast.LENGTH_SHORT).show();
                else {
                    if (quantity1 > 0) quantity1--;

                    ContentValues values = new ContentValues();
                    values.put(COLUMN_QUANTITY, quantity1);

                    if (context1.getContentResolver().update(uri, values, null, null) == 0)
                        Toast.makeText(context1, "Could not update quantity!", Toast.LENGTH_SHORT).show();
//                    else context1.getLoaderManager().initLoader(1, null, context1).forceLoad();
                }
            }
        });
    }
}