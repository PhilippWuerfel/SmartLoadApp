package com.htw.smartloadapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.htw.smartloadapp.webservice.login.EnumUserRole;
import com.htw.smartloadapp.webservice.management.order.SelectedFreightOrder;

import java.io.File;

public class PhotoItemActivity extends MenuActivity {
    private static final String TAG = "PhotoItemActivity";

    Button btnDeletePhoto;
    ImageView imageView;
    File selPhotoFile;
    private static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_item);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch(Exception e){
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }

        btnDeletePhoto = (Button) findViewById(R.id.btnDeletePhoto);
        imageView = (ImageView) findViewById(R.id.ivPhotoItem);

        try{
            // load photo depending on role and photoList
            Intent prevIntent = getIntent();
            int position = prevIntent.getExtras().getInt("position");
            String photoListKeyword = prevIntent.getExtras().getString("keyword");

            switch (photoListKeyword) {
                case "PACKING":
                    selPhotoFile = SelectedFreightOrder.getInstance().getUploadPhotoList().get(position);
                    Log.d(TAG, "onResume: Photolist for Packer");
                    break;
                case "DELIVERY_BEFORE":
                    selPhotoFile = SelectedFreightOrder.getInstance().getUploadPhotoListBeforeDelivery().get(position);
                    Log.d(TAG, "onResume: Photolist for Driver Before Delivery");
                    break;
                case "DELIVERY_AFTER":
                    selPhotoFile = SelectedFreightOrder.getInstance().getUploadPhotoListAfterDelivery().get(position);
                    Log.d(TAG, "onResume: Photolist for Driver After Delivery");
                    break;
                default:
                    Log.e(TAG, "onResume: keyword " + photoListKeyword + " not implemented yet" );
                    imageView.setImageResource(R.drawable.baseline_image_black_48dp);
            }
            Bitmap photoBitmap = BitmapFactory.decodeFile(selPhotoFile.getPath());
            imageView.setImageBitmap(photoBitmap);
        }catch (Exception e){
            Log.e(TAG, "onCreate: " + e.getMessage() );
            imageView.setImageResource(R.drawable.baseline_image_black_48dp);
        }

        btnDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFile();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public void removeFile() {

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onClick: Requesting Permissions");
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(PhotoItemActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.e(TAG, "onClick: shouldShowRequestPermission");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(PhotoItemActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE
                );
            }
        }
        else {
            File filepath = Environment.getExternalStorageDirectory();
            //create a folder where the photo will be saved
            File dir = new File(filepath.getAbsolutePath() + "/SmartLoad/");
            if (dir.exists()) {
                if (selPhotoFile != null) {
                    if (selPhotoFile.delete()) {
                        // delete photoTag from photoTags in freightOrder object depending on role of current editor
                        if (SelectedFreightOrder.getInstance().getUserRoleOfCurrentEditor() == EnumUserRole.DRIVER) {
                            SelectedFreightOrder.getInstance().getSelectedFreightOrder().deletePhotoTagDriver(selPhotoFile.getName());
                        } else if (SelectedFreightOrder.getInstance().getUserRoleOfCurrentEditor() == EnumUserRole.PACKER) {
                            SelectedFreightOrder.getInstance().getSelectedFreightOrder().deletePhotoTagPacker(selPhotoFile.getName());
                        }
                        // delete photoFile from recViewPhotoList of SelectedFreightOrder
                        SelectedFreightOrder.getInstance().deletePhotoFromUploadPhotoList(selPhotoFile);

                        Toast.makeText(PhotoItemActivity.this, "Photo deleted", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Log.e(TAG, "removeFile: error removing selPhotoFile! selPhotoFile.delete is false");
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "onRequestPermissionsResult: Permission granted");
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.e(TAG, "onRequestPermissionsResult: Permission denied");
                }
                return;
            }

        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }

    @SuppressLint("ResourceType")//the Resources needed for the Language DriverActivityOrderOverview
    public void updateView(String lang) {
        Context context = LocaleHelper.setLocale(this, lang);
        Resources resources = context.getResources();
        btnDeletePhoto.setText(resources.getString(R.string.btnDeletePhoto));
        getSupportActionBar().setTitle(resources.getString(R.string.title_activity_photoitem));

    }
}
