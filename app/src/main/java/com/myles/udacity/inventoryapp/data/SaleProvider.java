package com.myles.udacity.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.myles.udacity.inventoryapp.data.InventoryContract.SaleEntry;

public class SaleProvider extends ContentProvider {

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private static final int SALES = 100;

    private static final int SALE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, SaleEntry.TABLE_NAME, SALES);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, SaleEntry.TABLE_NAME + "/#", SALE_ID);
    }

    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case SALES:
                cursor = database.query(SaleEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SALE_ID:
                selection = SaleEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(SaleEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SALES:
                return insertInventory(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertInventory(Uri uri, ContentValues values) {
        //Read and cleanse the productname
        String productName = values.getAsString(SaleEntry.COLUMN_PRODUCT_NAME);
        if (productName == null) {
            throw new IllegalArgumentException("Inventory requires a product name");
        }

        //Read and cleanse the quantity
        Integer quantity = values.getAsInteger(SaleEntry.COLUMN_QUANTITY);
        if (quantity == null || quantity < 0 ) {
            throw new IllegalArgumentException("Quantity should be greater or equal to zero");
        }

        //Read and cleanse the category
        Integer category = values.getAsInteger(SaleEntry.COLUMN_CATEGORY);
        if (category != null || !SaleEntry.isValidCategory(category)) {
            throw new IllegalArgumentException("Invalid category");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(SaleEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SALES:
                return updateInventory(uri, contentValues, selection, selectionArgs);
            case SALE_ID:
                selection = SaleEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateInventory(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(SaleEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(SaleEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Inventory requires a product name");
            }
        }

        if (values.containsKey(SaleEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(SaleEntry.COLUMN_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Quantity should be greater or equal to zero");
            }
        }

        if (values.containsKey(SaleEntry.COLUMN_CATEGORY)) {
            Integer category = values.getAsInteger(SaleEntry.COLUMN_CATEGORY);
            if (category != null || SaleEntry.isValidCategory(category)) {
                throw new IllegalArgumentException("Invalid category");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(SaleEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SALES:
                rowsDeleted = database.delete(SaleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SALE_ID:
                selection = SaleEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(SaleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SALES:
                return SaleEntry.CONTENT_LIST_TYPE;
            case SALE_ID:
                return SaleEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
