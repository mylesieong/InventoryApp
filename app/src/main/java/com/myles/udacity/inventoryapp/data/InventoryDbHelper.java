package com.myles.udacity.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.myles.udacity.inventoryapp.data.InventoryContract.InventoryEntry;
import com.myles.udacity.inventoryapp.data.InventoryContract.SaleEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "shelter.db";

    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * Create table "inventory"
         */
        String SQL_CREATE_INVENTORY_TABLE =  "CREATE TABLE " + InventoryContract.InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL UNIQUE, "
                + InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_PRICE+ " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_PICTURE + " TEXT);";

        db.execSQL(SQL_CREATE_INVENTORY_TABLE);

        /**
         * Create table "sale"
         */
        String SQL_CREATE_SALE_TABLE =  "CREATE TABLE " + InventoryContract.SaleEntry.TABLE_NAME + " ("
                + SaleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SaleEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + SaleEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + SaleEntry.COLUMN_CATEGORY + " INTEGER NOT NULL, "
                + SaleEntry.COLUMN_DATE + " INTEGER, "
                + SaleEntry.COLUMN_TIME + " INTEGER);";

        db.execSQL(SQL_CREATE_SALE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //to be filled
    }
}