package com.myles.udacity.inventoryapp.data;

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;

public final class InventoryContract {

    private InventoryContract() {}

    public static final String CONTENT_AUTHORITY = "com.myles.udacity.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class InventoryEntry implements BaseColumns {

        public final static String TABLE_NAME = "inventory";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_NAME);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "productname";
        public final static String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_PRICE = "price";
        public final static String COLUMN_PICTURE = "picture";
        public final static String COLUMN_EMAIL = "email";

    }

}

