package com.htw.smartloadapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.htw.smartloadapp.webservice.management.order.SelectedFreightOrder;
import com.htw.smartloadapp.webservice.restservice.model.FreightOrderModel;

public class DriverActivityDutyOverview extends MenuActivity {
    private static final String TAG = "DriverActivityDutyOver";

    Button btnPhotoBefore;
    Button btnPhotoAfter;
    Button btnSignature;
    Button btnFeedback;
    Button btnFinish;
    Button btnMapsNavigation;

    // add all TextViews here which are filled with data from clicked item on LandingPage
    TextView txtCompanyName;
    TextView txtStreet;
    TextView txtPostalCity;
    TextView txtCountry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_duty_overview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Duty Overview");


        // Link buttons to matching Layout
        btnPhotoBefore = (Button)findViewById(R.id.btnPhotoBeforeDriv);
        btnPhotoAfter = (Button)findViewById(R.id.btnPhotoAfterDriv);
        btnSignature = (Button)findViewById(R.id.btnSignatureDriv);
        btnFeedback = (Button)findViewById(R.id.btnFeedbackDriv);
        btnFinish = (Button)findViewById(R.id.btnDutyFinishDriv);
        btnMapsNavigation = (Button)findViewById(R.id.btnMapNavigationDriv);

        // Link TextViews and fill with text from selected FreightOrder
        FreightOrderModel selectedFreightOrder = SelectedFreightOrder.getInstance().getSelectedFreightOrder();
        String content;

        txtCompanyName = (TextView)findViewById(R.id.txtCompNameOrdOverviewDriv);
        txtCompanyName.setText(selectedFreightOrder.getCompanyName());

        txtStreet = (TextView)findViewById(R.id.txtStreetOrdOverviewDriv);
        content = selectedFreightOrder.getStreetName() + " " + selectedFreightOrder.getStreetNumber();
        txtStreet.setText(content);

        txtPostalCity = (TextView)findViewById(R.id.txtPostalCityOrdOverviewDriv);
        content = selectedFreightOrder.getPostalCode() + ", " + selectedFreightOrder.getCity();
        txtPostalCity.setText(content);

        txtCountry = (TextView)findViewById(R.id.txtCountryOrdOverviewDriv);
        txtCountry.setText(selectedFreightOrder.getCountry());



        // Set OnClick Listeners

        btnPhotoBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_photo = new Intent(DriverActivityDutyOverview.this,PhotomakerActivity.class);
                intent_photo.putExtra("keyword", "DELIVERY_BEFORE" );
                startActivity(intent_photo);
            }
        });

        btnPhotoAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_photo = new Intent(DriverActivityDutyOverview.this,PhotomakerActivity.class);
                intent_photo.putExtra("keyword", "DELIVERY_AFTER" );
                startActivity(intent_photo);
            }
        });

        btnSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_signature = new Intent(DriverActivityDutyOverview.this, SignatureActivity.class);
                startActivity(intent_signature);
            }
        });

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_feedback = new Intent(DriverActivityDutyOverview.this, FeedbackActivity.class);
                startActivity(intent_feedback);
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_finish = new Intent(DriverActivityDutyOverview.this, DriverActivityFinish.class);
                startActivity(intent_finish);
            }
        });

        btnMapsNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Destination = SelectedFreightOrder.getInstance().getSelectedFreightOrder().getStreetName()+" "+
                        SelectedFreightOrder.getInstance().getSelectedFreightOrder().getStreetNumber()+" "+
                        SelectedFreightOrder.getInstance().getSelectedFreightOrder().getPostalCode();
                String Addressquery="google.navigation:q="+Destination+"&mode=d";
                Uri gmmIntentUri=Uri.parse(Addressquery);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }

            }
        });

        Log.d(TAG, "onCreate ends");
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }
    @SuppressLint("ResourceType")
    public void updateView(String lang) {
        Context context =LocaleHelper.setLocale(this,lang);
        Resources resources = context.getResources();
        btnPhotoAfter.setText(resources.getString(R.string.btnPhotoAfterDriv));
        btnPhotoBefore.setText(resources.getString(R.string.btnPhotoBeforeDriv));
        btnFeedback.setText(resources.getString(R.string.btnFeedbackDriv));
        btnSignature.setText(resources.getString(R.string.btnSignatureDriv));
        btnFinish.setText(resources.getString(R.string.btnFinishdutyDriv));
        getSupportActionBar().setTitle(resources.getString(R.string.title_activity_driver_duty_overview));
      //  btnFinish.setText(resources.getString(R.string.btnDutyFinishPac));
    }

}
