package com.htw.smartloadapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.htw.smartloadapp.webservice.download.data.DownloadProgressResponseBody;
import com.htw.smartloadapp.webservice.management.order.EnumFreightOrderStatus;
import com.htw.smartloadapp.webservice.management.order.SelectedFreightOrder;
import com.htw.smartloadapp.webservice.upload.file.UploadProgressRequestBody;
import com.htw.smartloadapp.webservice.upload.file.RestFileUpload;
import com.htw.smartloadapp.webservice.upload.freightorder.RestFreightOrderUpload;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;

public class PackerActivityFinish extends MenuActivity {

    private static final String TAG = "PackerActivityFinish";

    TextView tvCompleteMission;
    private Button btnBack;
    private Button btnUpload;

    ArrayList<File> fileList;

    ProgressDialog progressDialog;

    private static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packer_finish);
        getSupportActionBar().setTitle("Duty Finish");

        // ProgressDialog for upload
        progressDialog = new ProgressDialog(this);

        btnBack = (Button)this.findViewById(R.id.btnBackFinishPack);
        btnUpload = (Button)this.findViewById(R.id.btnUploadFinishPack);
        tvCompleteMission =(TextView) this.findViewById(R.id.tvCompleteDummyPack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check/Request Permissions
                if(ContextCompat.checkSelfPermission(PackerActivityFinish.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "onClick: Requesting Permissions");
                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(PackerActivityFinish.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Log.e(TAG, "onClick: shouldShowRequestPermission");
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(PackerActivityFinish.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE
                        );
                    }
                }else{
                    Log.d(TAG, "onClick: Uploading files");

                    fileList = new ArrayList<>();
                    // add files from Photo- and SignatureActivity (and check if null)
                    boolean hasPhoto = false;
                    boolean hasSign = false;

                    // check if signature were taken
                    if(SelectedFreightOrder.getInstance().getUploadSignature()!=null){
                        fileList.add(SelectedFreightOrder.getInstance().getUploadSignature());
                        hasSign = true;
                    }

                    // check if photos were taken
                    if(SelectedFreightOrder.getInstance().getUploadPhotoList()!=null){
                        if(SelectedFreightOrder.getInstance().getUploadPhotoList().size() != 0){
                            fileList = SelectedFreightOrder.getInstance().getUploadPhotoList();
                            hasPhoto = true;
                        }
                    }

                    if(fileList.size()>0 & hasPhoto & hasSign){
                        Log.d(TAG, "onClick: uploading following files: " + fileList.toString());

                        // Upload freightOrderStatus
                        // Update EnumFreightOrderStatus from ON_PACKING to READY_FOR_DELIVERY
                        SelectedFreightOrder.getInstance().getSelectedFreightOrder().setFreightOrderStatus(EnumFreightOrderStatus.READY_FOR_DELIVERY);
                        // Update timestamp
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        SelectedFreightOrder.getInstance().getSelectedFreightOrder().setTimestamp(String.valueOf(timestamp.getTime()));
                        // Call to Webserver: Update FreightOrder
                        // uploadFreightOrder(FreightOrder freightOrder)
                        RestFreightOrderUpload restFreightOrderUpload = new RestFreightOrderUpload(SelectedFreightOrder.getInstance().getSelectedFreightOrder());
                        restFreightOrderUpload.uploadFreightOrder(new DownloadProgressResponseBody.DownloadCallbacks() {
                            @Override
                            public void onProgressUpdate(long bytesRead, long contentLength, boolean done) {
                                Log.d(TAG, "onProgressUpdate");
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(PackerActivityFinish.this, "Error uploading FreightOrder", Toast.LENGTH_SHORT).show();
                                // whole update should be done again
                            }

                            @Override
                            public void onFinish() {
                                // Now start uploading files
                                uploadFiles(fileList, "file");
                            }
                        });
                    }else{
                        Log.d(TAG, "onClick: Not all necessary files available hasPhoto=" + hasPhoto + " hasSign=" + hasSign);
                        if (!hasPhoto && !hasSign){
                            Toast.makeText(PackerActivityFinish.this, "No photo and no signature found, please finish freight order before uploading", Toast.LENGTH_LONG).show();
                        }else if(!hasPhoto){
                            Toast.makeText(PackerActivityFinish.this, "No photo found, please finish freight order before uploading", Toast.LENGTH_LONG).show();
                        }else if (!hasSign){
                            Toast.makeText(PackerActivityFinish.this, "No signature found, please finish freight order before uploading", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }
    @SuppressLint("ResourceType")//the Resources needed for the Language DriverActivityFinish
    public void updateView(String lang) {
        Context context =LocaleHelper.setLocale(this,lang);
        Resources resources = context.getResources();
        btnUpload.setText(resources.getString(R.string.btnUploadFinishPack));
        btnBack.setText(resources.getString(R.string.btnBackFinishPack));
        tvCompleteMission.setText(resources.getString(R.string.tvCompleteDummyPack));
        getSupportActionBar().setTitle(resources.getString(R.string.title_activity_packer_finish));
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
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void uploadFiles(ArrayList<File> fileList, String filekey){
        RestFileUpload restFileUpload = new RestFileUpload(progressDialog);
        restFileUpload.uploadFiles(filekey, fileList, new UploadProgressRequestBody.UploadCallbacks() {
            // Override methods for Progress-requestbody
            @Override
            public void onProgressUpdate(int percentage) {
                // set current progress
                Log.d(TAG, "onProgressUpdate: " + String.valueOf(percentage) + " %");
                progressDialog.setProgress(percentage);
            }

            @Override
            public void onError() {
                // do something on error
                Log.e(TAG, "onError: " + "Error whilst uploading" );
                Toast.makeText(getApplicationContext(), "Error on Upload, try again", Toast.LENGTH_LONG).show();
                hideProgressDialog();

            }

            @Override
            public void onFinish() {
                // do something on upload finished
                hideProgressDialog();
                Log.d(TAG, "onFinish: " + "finished uploading files");

                // Finish with intent to Landing Page
                Intent intentFinishedUpload = new Intent(PackerActivityFinish.this, PackerActivityLandingPage.class);
                startActivity(intentFinishedUpload);
                Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void hideProgressDialog(){
        try{
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }catch (Exception e){
            Log.d(TAG, "hideProgressDialog: " + e.getMessage());
        }
    }

}
