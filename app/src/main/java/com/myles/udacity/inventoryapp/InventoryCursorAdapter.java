package com.myles.udacity.inventoryapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
        Log.v("myles", "invoke bindView method");
        TextView productNameTextView = (TextView) view.findViewById(R.id.text_product_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.text_price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.text_quantity);
        Button trackASellButton = (Button)view.findViewById(R.id.button_track_a_sell);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PET_NAME);
        int breedColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PET_BREED);

        String petName = cursor.getString(nameColumnIndex);
        String petBreed = cursor.getString(breedColumnIndex);

        if (TextUtils.isEmpty(petBreed)) {
            petBreed = context.getString(R.string.unknown_breed);
        }

        productNameTextView.setText(petName);
        priceTextView.setText(petBreed);
        quantityTextView.setText(petBreed);

        Log.v("myles_debug", "current id is:" + cursor.getLong(cursor.getColumnIndex("_id")));
        IdLocationListener buttonOnClickListener = new IdLocationListener();
        buttonOnClickListener.setId(cursor.getLong(cursor.getColumnIndex("_id"))).setContext(context);
        trackASellButton.setOnClickListener(buttonOnClickListener);

        IdLocationListener viewOnClickListener = new IdLocationListener();
        viewOnClickListener.setId(cursor.getLong(cursor.getColumnIndex("_id"))).setContext(context);
        view.setOnClickListener(viewOnClickListener);
    }

    /**
     * Customer Listener to record certain db record id
     */
    private class IdLocationListener implements View.OnClickListener{
        private Long mId;
        private Context mContext;

        public IdLocationListener setId(Long id){
            this.mId = id;
            return this;
        }

        public IdLocationListener setContext(Context context){
            this.mContext = context;
            return this;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(this.mContext, EditorActivity.class);
            Uri currentPetUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, mId);
            intent.setData(currentPetUri);
            this.mContext.startActivity(intent);
        }
    }
}
