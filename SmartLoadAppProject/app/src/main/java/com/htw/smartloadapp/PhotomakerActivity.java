package com.htw.smartloadapp;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.htw.smartloadapp.layout.adapter.PhotoRecViewAdapter;
import com.htw.smartloadapp.layout.adapter.RecyclerItemClickListener;
import com.htw.smartloadapp.webservice.login.EnumUserRole;
import com.htw.smartloadapp.webservice.management.order.SelectedFreightOrder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;

public class PhotomakerActivity extends MenuActivity implements RecyclerItemClickListener.OnRecyclerClickListener {
    private static final String TAG = "PhotomakerActivity";

    RecyclerView recViewPhotomaker;
    PhotoRecViewAdapter photoRecViewAdapter;
    ArrayList<File> recViewPhotoList;
    String photoListKeyword;
    Button btnTakePhoto;
    Bitmap bitmap;
    Button btnFinishPhoto;
    File file;
    OutputStream outputStream;
    private static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 200;
    private static final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 300;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    SharedPreferences sharedPreferencesPhoto;
    int PhotoQuality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photomaker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setTitle("Photo");
        }catch (NullPointerException e){
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }

        btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
        btnFinishPhoto = (Button) findViewById(R.id.btnFinish_foto);
        recViewPhotomaker = (RecyclerView) findViewById(R.id.rvPhotoMaker);


        // Check if READ_EXTERNAL_STORAGE Permission granted
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onClick: Requesting Permissions");
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(PhotomakerActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.e(TAG, "onClick: shouldShowRequestPermission READ_EXTERNAL_STORAGE");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(PhotomakerActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
                );
            }
        }else{
            Log.d(TAG, "onCreate: READ_EXTERNAL_STORAGE permission already granted");
        }

        recViewPhotomaker.setLayoutManager(new LinearLayoutManager(this));

        recViewPhotomaker.addOnItemTouchListener(new RecyclerItemClickListener(this, recViewPhotomaker, this));
        // tbd depend recViewPhotoList on Button and Role (maybe send via intent)
        Intent prevIntent = getIntent();
        try{
            photoListKeyword = prevIntent.getExtras().getString("keyword");

            switch (photoListKeyword) {
                case "PACKING":
                    recViewPhotoList = SelectedFreightOrder.getInstance().getUploadPhotoList();
                    Log.d(TAG, "onCreate: Photolist for Packer");
                    break;
                case "DELIVERY_BEFORE":
                    recViewPhotoList = SelectedFreightOrder.getInstance().getUploadPhotoListBeforeDelivery();
                    Log.d(TAG, "onCreate: Photolist for Driver Before Delivery");
                    break;
                case "DELIVERY_AFTER":
                    recViewPhotoList = SelectedFreightOrder.getInstance().getUploadPhotoListAfterDelivery();
                    Log.d(TAG, "onCreate: Photolist for Driver After Delivery");
                    break;
                default:
                    Log.e(TAG, "onCreate: keyword " + photoListKeyword + " not implemented yet" );
            }
        }catch (Exception e){
            Log.e(TAG, "onCreate: " + e.getMessage() );
            photoListKeyword = "empty";
            recViewPhotoList = SelectedFreightOrder.getInstance().getUploadPhotoList();
        }


        //recViewPhotoList = SelectedFreightOrder.getInstance().getUploadPhotoList();
        photoRecViewAdapter = new PhotoRecViewAdapter(this, recViewPhotoList);
        recViewPhotomaker.setAdapter(photoRecViewAdapter);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onClick: Requesting Permissions");
                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(PhotomakerActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Log.e(TAG, "onClick: shouldShowRequestPermission");
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(PhotomakerActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE
                        );
                    }
                }
                else {
                    dispatchTakePictureIntent();
                }

            }
        });

        btnFinishPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }

    @SuppressLint("ResourceType")//the Resources needed for the Language DriverActivityOrderOverview
    public void updateView(String lang) {
        Context context = LocaleHelper.setLocale(this, lang);
        Resources resources = context.getResources();
        btnFinishPhoto.setText(resources.getString(R.string.btnFinish_foto));
        btnTakePhoto.setText(resources.getString(R.string.btnTakePhoto));
        getSupportActionBar().setTitle(resources.getString(R.string.title_activity_photomaker));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 0:
                Log.d(TAG, "onActivityResult: Photomaking canceled");
                break;
            case -1:
                //Bundle extras = data.getExtras();
                //bitmap = (Bitmap) extras.get("data");
                try{
                    bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                    saveToInternalStorage(bitmap);

                    recViewPhotoList = SelectedFreightOrder.getInstance().getUploadPhotoList();
                    photoRecViewAdapter.loadNewData(recViewPhotoList);
                }catch(Exception e){
                    Log.e(TAG, "onActivityResult: " + e.getMessage());
                }

                break;
            default:
                Log.e(TAG, "onActivityResult: result code " + resultCode + " not implemented yet");
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
    protected void onResume() {
        super.onResume();
        switch (photoListKeyword) {
            case "PACKING":
                recViewPhotoList = SelectedFreightOrder.getInstance().getUploadPhotoList();
                Log.d(TAG, "onResume: Photolist for Packer");
                break;
            case "DELIVERY_BEFORE":
                recViewPhotoList = SelectedFreightOrder.getInstance().getUploadPhotoListBeforeDelivery();
                Log.d(TAG, "onResume: Photolist for Driver Before Delivery");
                break;
            case "DELIVERY_AFTER":
                recViewPhotoList = SelectedFreightOrder.getInstance().getUploadPhotoListAfterDelivery();
                Log.d(TAG, "onResume: Photolist for Driver After Delivery");
                break;
            default:
                Log.e(TAG, "onResume: keyword " + photoListKeyword + " not implemented yet" );
        }
        photoRecViewAdapter.loadNewData(recViewPhotoList);
    }

    public void saveToInternalStorage(Bitmap bmap) {

        File filepath = Environment.getExternalStorageDirectory();
        //create a folder where the signature will be saved
        File dir = new File(filepath.getAbsolutePath() + "/SmartLoad/");
        if (!dir.exists()) {
            try {
                dir.mkdir();
            } catch (Exception e) {
                Log.e(TAG, "saveToInternalStorage: " + e.getMessage());
            }
        }

        //give the photo a filename
        // tag pattern: orderID_userID_Timestamp
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String photoTag = SelectedFreightOrder.getInstance().getSelectedFreightOrder().getFreightOrderId() + "_" + SelectedFreightOrder.getInstance().getUserIdOfCurrentEditor() + "_" + timestamp.getTime() + ".jpg";
        sharedPreferencesPhoto=getSharedPreferences(this.getResources().getString(R.string.Pref_photo_quality),MODE_PRIVATE);
        PhotoQuality=sharedPreferencesPhoto.getInt("photo",100);

        file = new File(dir, photoTag);
        try {
            outputStream = new FileOutputStream(file);
            bmap.compress(Bitmap.CompressFormat.JPEG, PhotoQuality, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.d(TAG, "saveToInternalStorage: Photo created and compressed " + file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);

        // add photoTag to freightOrder depending on role
        if (SelectedFreightOrder.getInstance().getUserRoleOfCurrentEditor() == EnumUserRole.DRIVER) {
            SelectedFreightOrder.getInstance().getSelectedFreightOrder().addPhotoTagDriver(photoTag);
            switch (photoListKeyword) {
                case "DELIVERY_BEFORE":
                    SelectedFreightOrder.getInstance().addPhotoToPhotoListBeforeDelivery(file);
                    //recViewPhotoList = SelectedFreightOrder.getInstance().getUploadPhotoListBeforeDelivery();
                    break;
                case "DELIVERY_AFTER":
                    SelectedFreightOrder.getInstance().addPhotoToPhotoListAfterDelivery(file);
                    //recViewPhotoList = SelectedFreightOrder.getInstance().getUploadPhotoListAfterDelivery();
                    break;
                default:
                    Log.e(TAG, "saveToInternalStorage: keyword " + photoListKeyword + " not implemented yet" );
            }

        } else if (SelectedFreightOrder.getInstance().getUserRoleOfCurrentEditor() == EnumUserRole.PACKER) {
            SelectedFreightOrder.getInstance().getSelectedFreightOrder().addPhotoTagPacker(photoTag);
        }
        // add photoFile to recViewPhotoList of SelectedFreightOrder
        SelectedFreightOrder.getInstance().addPhotoToUploadPhotoList(file);

        Toast.makeText(PhotomakerActivity.this, "Photo temporally saved", Toast.LENGTH_SHORT).show();
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image selPhotoFile name
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String imageFileName = SelectedFreightOrder.getInstance().getSelectedFreightOrder().getFreightOrderId() + "_" + SelectedFreightOrder.getInstance().getUserIdOfCurrentEditor() + "_" + timestamp.getTime();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a selPhotoFile: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "dispatchTakePictureIntent: " + ex.getMessage() );
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.htw.smartloadapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: starts " + position);
        // implementation for short tap could be here
        /*
        Intent intent = new Intent(this, PhotoItemActivity.class);
        intent.putExtra("position",position);
        intent.putExtra("keyword", photoListKeyword);
        startActivity(intent);
        */
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: starts " + position);
        Intent intent = new Intent(this, PhotoItemActivity.class);
        intent.putExtra("position",position);
        intent.putExtra("keyword", photoListKeyword);
        startActivity(intent);
    }
}
