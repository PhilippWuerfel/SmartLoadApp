package com.htw.smartloadapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.htw.smartloadapp.webservice.download.data.DownloadProgressResponseBody;
import com.htw.smartloadapp.webservice.management.order.EnumFreightOrderStatus;
import com.htw.smartloadapp.webservice.management.order.SelectedFreightOrder;
import com.htw.smartloadapp.webservice.restservice.model.FreightOrderModel;
import com.htw.smartloadapp.webservice.task.bodies.FreightOrderStatusTask;
import com.htw.smartloadapp.webservice.upload.freightorder.RestFreightOrderStatusUpload;

import java.sql.Timestamp;

public class PackerActivityOrderOverview extends MenuActivity {

    private static final String TAG = "PackerActivityOrderOver";

    private Button btnConfirmItem;
    // add all TextViews here which are filled with data from clicked item on LandingPage
    private TextView txtCompanyName;
    private TextView txtStreet;
    private TextView txtPostalCity;
    private TextView txtCountry;
    private TextView txtDescription;

    private FreightOrderModel selectedFreightOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packer_order_overview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Order Overview");

        selectedFreightOrder = SelectedFreightOrder.getInstance().getSelectedFreightOrder();
        // Fill all TextViews from above with data from selected item
        String content;

        txtCompanyName = (TextView)findViewById(R.id.txtCompNameOrdOverviewPac);
        txtCompanyName.setText(selectedFreightOrder.getCompanyName());

        txtStreet = (TextView)findViewById(R.id.txtStreetOrdOverviewPac);
        content = selectedFreightOrder.getStreetName() + " " + selectedFreightOrder.getStreetNumber();
        txtStreet.setText(content);

        txtPostalCity = (TextView)findViewById(R.id.txtPostalCityOrdOverviewPac);
        content = selectedFreightOrder.getPostalCode() + ", " + selectedFreightOrder.getCity();
        txtPostalCity.setText(content);

        txtCountry = (TextView)findViewById(R.id.txtCountryOrdOverviewPac);
        txtCountry.setText(selectedFreightOrder.getCountry());

        txtDescription = (TextView)findViewById(R.id.txtDescriptionOrdOverviewPac);
        txtDescription.setText(selectedFreightOrder.getDescription());

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revertUpdateFreightOrderStatus();
            }
        });

        btnConfirmItem = (Button)findViewById(R.id.btnConfirmOrdOverviewPac);
        btnConfirmItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update EnumFreightOrderStatus from BEFORE_PACKING to ON_PACKING
                selectedFreightOrder.setFreightOrderStatus(EnumFreightOrderStatus.ON_PACKING);
                selectedFreightOrder.setUserId(SelectedFreightOrder.getInstance().getUserIdOfCurrentEditor());
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                selectedFreightOrder.setTimestamp(String.valueOf(timestamp.getTime()));

                // uploadFreightOrderStatus(String freightOrderId, EnumFreightOrderStatus freightOrderStatus, String userId, TimeStamp timeStamp)
                // Create Task for Upload
                FreightOrderStatusTask freightOrderStatusTask = new FreightOrderStatusTask(selectedFreightOrder.getFreightOrderId(), selectedFreightOrder.getFreightOrderStatus(), selectedFreightOrder.getUserId(), selectedFreightOrder.getTimestamp());
                // Call to Webserver: Upload FreightOrderStatus
                RestFreightOrderStatusUpload restFreightOrderStatusUpload = new RestFreightOrderStatusUpload(freightOrderStatusTask);
                restFreightOrderStatusUpload.uploadFreightOrderStatus(new DownloadProgressResponseBody.DownloadCallbacks() {
                    @Override
                    public void onProgressUpdate(long bytesRead, long contentLength, boolean done) {
                        Log.d(TAG, "onProgressUpdate");
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(PackerActivityOrderOverview.this, "Error uploading FreightOrderStatus", Toast.LENGTH_SHORT).show();
                        // whole update should be done again
                    }

                    @Override
                    public void onFinish() {
                        Intent intent_orderView_dutyOverview = new Intent(PackerActivityOrderOverview.this, PackerActivityDutyOverview.class);
                        startActivity(intent_orderView_dutyOverview);
                    }
                });
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }
    @SuppressLint("ResourceType")//the Resources needed for the Language DriverActivityOrderOverview
    public void updateView(String lang) {
        Context context =LocaleHelper.setLocale(this,lang);
        Resources resources = context.getResources();
        btnConfirmItem.setText(resources.getString(R.string.btnConfirmOrdOverviewDriv));
        getSupportActionBar().setTitle(resources.getString(R.string.title_activity_packer_order_overview));
    }

    @Override
    public void onBackPressed() {
        revertUpdateFreightOrderStatus();
    }

    private void revertUpdateFreightOrderStatus(){
        // Update EnumFreightOrderStatus from ON_PACKING to BEFORE_PACKING
        selectedFreightOrder.setFreightOrderStatus(EnumFreightOrderStatus.BEFORE_PACKING);
        selectedFreightOrder.setUserId(SelectedFreightOrder.getInstance().getUserIdOfCurrentEditor());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        selectedFreightOrder.setTimestamp(String.valueOf(timestamp.getTime()));
        // Call to Webserver: Update EnumFreightOrderStatus
        // uploadFreightOrderStatus(String freightOrderId, EnumFreightOrderStatus freightOrderStatus, String userId, TimeStamp timeStamp)
        // Create Task for Upload
        FreightOrderStatusTask freightOrderStatusTask = new FreightOrderStatusTask(selectedFreightOrder.getFreightOrderId(), selectedFreightOrder.getFreightOrderStatus(), selectedFreightOrder.getUserId(), selectedFreightOrder.getTimestamp());
        // Call to Webserver: Upload FreightOrderStatus
        RestFreightOrderStatusUpload restFreightOrderStatusUpload = new RestFreightOrderStatusUpload(freightOrderStatusTask);
        restFreightOrderStatusUpload.uploadFreightOrderStatus(new DownloadProgressResponseBody.DownloadCallbacks() {
            @Override
            public void onProgressUpdate(long bytesRead, long contentLength, boolean done) {
                Log.d(TAG, "onProgressUpdate");
            }

            @Override
            public void onError() {
                Toast.makeText(PackerActivityOrderOverview.this, "Error uploading FreightOrderStatus", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                Intent intent_orderView_lpPacker = new Intent(PackerActivityOrderOverview.this, PackerActivityLandingPage.class);
                startActivity(intent_orderView_lpPacker);
            }
        });
    }

}
