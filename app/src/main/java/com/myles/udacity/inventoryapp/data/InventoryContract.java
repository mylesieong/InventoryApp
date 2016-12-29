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

    }

    public static final class SaleEntry implements BaseColumns {

        public final static String TABLE_NAME = "sale";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_NAME);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "productname";
        public final static String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_CATEGORY = "category";
        public final static String COLUMN_DATE = "date";
        public final static String COLUMN_TIME = "time";

        public static final int CATEGORY_SALES = 0;
        public static final int CATEGORY_INPUTS = 1;

        public static boolean isValidCategory (int category) {
            if (category == CATEGORY_SALES || category == CATEGORY_INPUTS) {
                return true;
            }
            return false;
        }
    }

}

