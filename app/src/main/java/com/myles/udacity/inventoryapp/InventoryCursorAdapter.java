package com.myles.udacity.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.myles.udacity.inventoryapp.data.InventoryContract.InventoryEntry;
import com.myles.udacity.inventoryapp.data.InventoryProvider;

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
            Uri currentPetUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, mId);
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
            /**
             * First, check if the remind quantity is >= 1, if yes, update the record to quantity = quantity -1; if no, give a toast to user
             */
            /* General Checking*/
            Uri currentInventroyUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, mId);
            if (currentInventroyUri == null){
                Toast.makeText(mContext, "No items found", Toast.LENGTH_SHORT).show();
                return ;
            }

            /* Get current quantity */
            String[] projection = {
                    InventoryEntry._ID,
                    InventoryEntry.COLUMN_QUANTITY
            };
            Cursor cursor = mContext.getContentResolver().query(currentInventroyUri, projection, null,null,null);
            int quantity = 0;
            if(cursor==null){
                Toast.makeText(mContext, "Item not found in table", Toast.LENGTH_SHORT).show();
                return ;
            }else if (cursor.getCount()<1){
                Toast.makeText(mContext, "No record fetch from table", Toast.LENGTH_SHORT).show();
                cursor.close();
                return ;
            }else{
                cursor.moveToFirst();
                quantity = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY));
                cursor.close();
            }
            if (quantity < 1){
                Toast.makeText(mContext, "Not enought remained stock", Toast.LENGTH_SHORT).show();
                return ;
            }
            /* Set Update quantity */
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_QUANTITY, quantity - 1);

            int rowsAffected = mContext.getContentResolver().update(currentInventroyUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(mContext, "Update failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Update succeed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
