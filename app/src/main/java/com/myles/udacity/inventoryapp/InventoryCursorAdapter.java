package com.myles.udacity.inventoryapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.IntegerRes;
import android.support.v4.app.LoaderManager;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.myles.udacity.inventoryapp.data.InventoryContract;
import com.myles.udacity.inventoryapp.data.InventoryContract.InventoryEntry;

import java.util.concurrent.CopyOnWriteArrayList;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v("myles", "invoke newView method");
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        /* debug */ Log.v("myles", "invoke bindView method");
        TextView productNameTextView = (TextView) view.findViewById(R.id.text_product_name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.text_quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.text_price);
        Button trackASellButton = (Button) view.findViewById(R.id.button_track_a_sell);

        int productNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);

        String productName = cursor.getString(productNameColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        int price = cursor.getInt(priceColumnIndex);

        productNameTextView.setText(productName);
        quantityTextView.setText(Integer.toString(quantity) + " pcs ");
        priceTextView.setText("$" + Integer.toString(price));

        /* debug */ Log.v("myles_debug", "current id is:" + cursor.getLong(cursor.getColumnIndex("_id")));
        trackASellButton.setOnClickListener(new TrackSaleButtonListenerWithId(context, cursor.getLong(cursor.getColumnIndex("_id"))));
        view.setOnClickListener(new JumpToEditorListenerWithId(context, cursor.getLong(cursor.getColumnIndex("_id"))));
    }

    /**
     * Customer Abstract Listener to record certain db record id
     */
    private abstract class AbstractListenerWithId implements View.OnClickListener {
        protected long mId;
        protected Context mContext;

        public AbstractListenerWithId() {
            super();
        }

        public AbstractListenerWithId(Context context, long id) {
            this.mContext = context;
            this.mId = id;
        }

        public void setId(Long id) {
            this.mId = id;
        }

        public void setContext(Context context) {
            this.mContext = context;
        }

        public abstract void onClick(View view);
    }

    /**
     * Customer Listener for view that is returned to ListView by CursorAdapter
     */
    private class JumpToEditorListenerWithId extends AbstractListenerWithId {
        public JumpToEditorListenerWithId() {
            super();
        }

        public JumpToEditorListenerWithId(Context context, long id) {
            super(context, id);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(this.mContext, EditorActivity.class);
            Uri currentPetUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, mId);
            intent.setData(currentPetUri);
            this.mContext.startActivity(intent);
        }
    }

    /**
     * Customer Listener for button that is returned to ListView by CursorAdapter
     */
    private class TrackSaleButtonListenerWithId extends AbstractListenerWithId {
        public TrackSaleButtonListenerWithId() {
            super();
        }

        public TrackSaleButtonListenerWithId(Context context, long id) {
            super(context, id);
        }

        @Override
        public void onClick(View view) {
            Log.v("myles_debug", "Invoke Track a sale button onclick method");
        }
    }
}
