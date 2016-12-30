package com.myles.udacity.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.EGLDisplay;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;

import com.myles.udacity.inventoryapp.data.InventoryContract.InventoryEntry;

import org.w3c.dom.Text;

import java.io.File;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_LOADER = 0;

    private Uri mCurrentInventoryUri;
    private EditText mProductNameEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private EditText mEmailEditText;
    private ImageView mPictureImage;
    private boolean mInventoryHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInventoryHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();

        Button modifyQuantityButton = (Button)findViewById(R.id.button_modify_quantity);
        Button orderMoreButton = (Button)findViewById(R.id.button_order_more);
        Button deleteItemButton = (Button)findViewById(R.id.button_delete_item);

        if (mCurrentInventoryUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_inventory));

            modifyQuantityButton.setVisibility(View.GONE);
            orderMoreButton.setVisibility(View.GONE);
            deleteItemButton.setVisibility(View.GONE);

            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_inventory));

            modifyQuantityButton.setVisibility(View.VISIBLE);
            orderMoreButton.setVisibility(View.VISIBLE);
            deleteItemButton.setVisibility(View.VISIBLE);

            modifyQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {showModifyQuantityDialog();}
            });
            orderMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderMoreAction();
                }
            });
            deleteItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {showDeleteConfirmationDialog();}
            });

            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        mProductNameEditText = (EditText) findViewById(R.id.edit_inventory_name);
        mQuantityEditText = (EditText) findViewById(R.id.edit_inventory_quantity);
        mPriceEditText = (EditText) findViewById(R.id.edit_inventory_price);
        mEmailEditText = (EditText) findViewById(R.id.edit_inventory_email);

        mPictureImage = (ImageView) findViewById(R.id.image_show_picture);

        mProductNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mEmailEditText.setOnTouchListener(mTouchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentInventoryUri == null) {
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_modify_quantity).setVisible(false);
            menu.findItem(R.id.action_order_more).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveInventory();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_modify_quantity:
                showModifyQuantityDialog();
                return true;
            case R.id.action_order_more:
                orderMoreAction();
                return true;
            case android.R.id.home:
                if (!mInventoryHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mInventoryHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_EMAIL,
                InventoryEntry.COLUMN_PICTURE};

        return new CursorLoader(this, mCurrentInventoryUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int productNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int emailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_EMAIL);
            int pictureColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PICTURE);

            String productName = cursor.getString(productNameColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            String picture = cursor.getString(pictureColumnIndex);

            mProductNameEditText.setText(productName);
            mQuantityEditText.setText(quantity);
            mPriceEditText.setText(Integer.toString(price));
            mEmailEditText.setText(email);

            if(picture!= null && !picture.equals("")){
                File imageFile = new File(this.getFilesDir()+"/"+picture+".jpg");
                if (imageFile.exists()) {
                    Log.v("myles_debug", "image file exists");
                    Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    mPictureImage.setImageBitmap(imageBitmap);
                }else{
                    Log.v("myles_debug", "image file not exists");
                    mPictureImage.setVisibility(View.GONE);
                }
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mEmailEditText.setText("");
    }

    private void saveInventory() {
        String productNameString = mProductNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String emailString = mEmailEditText.getText().toString().trim();

        if (mCurrentInventoryUri == null && TextUtils.isEmpty(productNameString) && TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(priceString)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(InventoryEntry.COLUMN_QUANTITY, TextUtils.isEmpty(quantityString) ? 0 : Integer.parseInt(quantityString));
        values.put(InventoryEntry.COLUMN_PRICE, TextUtils.isEmpty(priceString) ? 0 : Integer.parseInt(priceString));
        values.put(InventoryEntry.COLUMN_EMAIL, emailString);

        if (mCurrentInventoryUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_inventory_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_inventory_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_inventory_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_inventory_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteInventory();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * to be update
     */
    private void showModifyQuantityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.modify_dialog_msg);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_modify_quantity, null);
        final EditText editText = (EditText) view.findViewById(R.id.edit_new_quantity);
        builder.setView(view);
        builder.setPositiveButton(R.string.modify, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int newQuantity = Integer.parseInt(editText.getText().toString().trim());
                        if (newQuantity >= 0) {
                            mQuantityEditText.setText(Integer.toString(newQuantity));
                            saveInventory();
                            finish();
                        } else {
                            Toast.makeText(EditorActivity.this, getString(R.string.dialog_modify_quantity_less_than_zero), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                }

        );
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                }
        );
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void orderMoreAction() {
        String emailAddress = mEmailEditText.getText().toString().trim();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void deleteInventory() {
        if (mCurrentInventoryUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_inventory_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_inventory_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}