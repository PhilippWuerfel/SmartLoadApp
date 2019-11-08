package com.htw.smartloadapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.htw.smartloadapp.webservice.login.EnumUserRole;
import com.htw.smartloadapp.webservice.management.order.SelectedFreightOrder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;

public class SignatureActivity extends MenuActivity {

    private static final String TAG = "SignatureActivity";

    private static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 200;

    SignaturePad signaturePad;
    Button btnClearSign;
    Button btnSaveSign;
    EditText etCustomerName;
    private OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        signaturePad = (SignaturePad)findViewById(R.id.signaturePad);
        btnClearSign = (Button)findViewById(R.id.btnClearSign);
        btnSaveSign = findViewById(R.id.btnSaveSign);
        etCustomerName=findViewById(R.id.etCustomerName);

        // reload last signature if user navigates back to SignatureActivity but has signed before
        if (SelectedFreightOrder.getInstance().getTempSignatureBitmap() != null){
            signaturePad.setSignatureBitmap(SelectedFreightOrder.getInstance().getTempSignatureBitmap());
        }
        // reload last signature customerName if user navigates back to SignatureActivity but has signed before
        if (SelectedFreightOrder.getInstance().getTempSignatureCustomerName() != null){
            etCustomerName.setText(SelectedFreightOrder.getInstance().getTempSignatureCustomerName());
        }

        btnClearSign.setEnabled(false);
        btnSaveSign.setEnabled(false);

        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                btnClearSign.setEnabled(true);
                btnSaveSign.setEnabled(true);
            }

            @Override
            public void onClear() {
                btnClearSign.setEnabled(false);
                btnSaveSign.setEnabled(false);
            }
        });

        btnClearSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
            }
        });

        btnSaveSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: StartSave");

                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "onClick: Requesting Permissions");
                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SignatureActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Log.e(TAG, "onClick: shouldShowRequestPermission");
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(SignatureActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE
                        );
                    }
                }else{
                    //The customer writes his name
                    String customerName=etCustomerName.getText().toString();
                    //Spaces will be deleted
                    final String customerName_no_sp= customerName.replaceAll("\\s+", "");

                    Bitmap signBitmap = signaturePad.getSignatureBitmap();
                    File filepath = Environment.getExternalStorageDirectory();
                    //create a folder where the signature will be saved
                    File dir= new File(filepath.getAbsolutePath()+"/SmartLoad/");
                    dir.mkdir();

                    //give the signature photo a name
                    // tag pattern: orderID_CustomerName_timestamp
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    String signatureTag = SelectedFreightOrder.getInstance().getSelectedFreightOrder().getFreightOrderId()+"_"+customerName_no_sp+"_"+timestamp.getTime()+".jpg";
                    File file=new File(dir, signatureTag);

                    try {
                        outputStream = new FileOutputStream(file);
                        signBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                        outputStream.flush();
                        outputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //photo will be shown in gallery
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(file));
                    sendBroadcast(intent);

                    // add signatureTag to freightOrder depending on role
                    if(SelectedFreightOrder.getInstance().getUserRoleOfCurrentEditor() == EnumUserRole.DRIVER){
                        SelectedFreightOrder.getInstance().getSelectedFreightOrder().setSignatureTagDriver(signatureTag);
                    }else if(SelectedFreightOrder.getInstance().getUserRoleOfCurrentEditor() == EnumUserRole.PACKER){
                        SelectedFreightOrder.getInstance().getSelectedFreightOrder().setSignatureTagPacker(signatureTag);
                    }
                    // add signatureFile to signatureFile of SelectedFreightOrder
                    SelectedFreightOrder.getInstance().setUploadSignature(file);

                    // add signatureBitmap to tempSignatureBitmap of SelectedFreightOrder
                    SelectedFreightOrder.getInstance().setTempSignatureBitmap(signBitmap);
                    // add signatureCustomerName to tempSignatureCustomerName of SelectedFreightOrder
                    SelectedFreightOrder.getInstance().setTempSignatureCustomerName(customerName);

                    Toast.makeText(SignatureActivity.this, "Signature Saved", Toast.LENGTH_SHORT).show();
                    //intent back to duty overview
                    finish();
                }
                Log.d(TAG, "onClick: EndSave");
            }
        });

        Log.d(TAG, "onCreate: ends");
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }
    @SuppressLint("ResourceType")//the Resources needed for the Language DriverActivityOrderOverview
    public void updateView(String lang) {
        Context context =LocaleHelper.setLocale(this,lang);
        Resources resources = context.getResources();
        btnClearSign.setText(resources.getString(R.string.btnClearSign));
        btnSaveSign.setText(resources.getString(R.string.btnSaveSign));
        etCustomerName.setHint(resources.getString(R.string.etCustomerName));
        getSupportActionBar().setTitle(resources.getString(R.string.title_activity_signature));

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

}
