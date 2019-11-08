package com.htw.smartloadapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;

import com.htw.smartloadapp.webservice.login.EnumUserRole;
import com.htw.smartloadapp.webservice.management.order.SelectedFreightOrder;

public class FeedbackActivity extends MenuActivity {

    private static final String TAG = "FeedbackActivity";
    EditText txtFeedback;
    Button btnSavefeedback;
    private static final String FILE_NAME = "Feedback.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtFeedback = findViewById(R.id.txtFeedback);
        btnSavefeedback =findViewById(R.id.btnSaveFeedback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Feedback");

        // fill Text in feedBack with last feedback depending on role
        if(SelectedFreightOrder.getInstance().getUserRoleOfCurrentEditor() == EnumUserRole.DRIVER){
            txtFeedback.setText(SelectedFreightOrder.getInstance().getSelectedFreightOrder().getFeedbackDriver());
        }else if(SelectedFreightOrder.getInstance().getUserRoleOfCurrentEditor() == EnumUserRole.PACKER){
            txtFeedback.setText(SelectedFreightOrder.getInstance().getSelectedFreightOrder().getFeedbackPacker());
        }

        btnSavefeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String feedback = txtFeedback.getText().toString();
                // add feedback to freightOrder depending on role
                if(SelectedFreightOrder.getInstance().getUserRoleOfCurrentEditor() == EnumUserRole.DRIVER){
                    SelectedFreightOrder.getInstance().getSelectedFreightOrder().setFeedbackDriver(feedback);
                }else if(SelectedFreightOrder.getInstance().getUserRoleOfCurrentEditor() == EnumUserRole.PACKER){
                    SelectedFreightOrder.getInstance().getSelectedFreightOrder().setFeedbackPacker(feedback);
                }
                Toast.makeText(FeedbackActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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

    @SuppressLint("ResourceType")//the Resources needed for the Language FeedbackActivity
    public void updateView(String lang) {
        Context context = LocaleHelper.setLocale(this,lang);
        Resources resources = context.getResources();
        btnSavefeedback.setText(resources.getString((R.string.btnSaveFeedback)));
    }
}



