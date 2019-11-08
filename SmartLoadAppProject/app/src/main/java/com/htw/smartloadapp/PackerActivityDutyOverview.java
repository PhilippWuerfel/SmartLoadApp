package com.htw.smartloadapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class PackerActivityDutyOverview extends MenuActivity {

    private static final String TAG = "PackerActivityDutyOverv";

    Button btnArticleOverview;
    Button btnSignature;
    Button btnFeedback;
    Button btnPhotoPack;
    Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packer_duty_overview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Duty Overview");


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Link buttons to matching Layout
        btnArticleOverview = (Button) findViewById(R.id.btnArtOverviewPac);
        btnSignature = (Button) findViewById(R.id.btnSignaturePack);
        btnFeedback = (Button) findViewById(R.id.btnfeedpackpack);
        btnFinish = (Button) findViewById(R.id.btnFinishdutyPac);
        btnPhotoPack = (Button) findViewById(R.id.btnPhotoPack);
        // Set OnClick Listeners

        btnArticleOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_item_overview = new Intent(PackerActivityDutyOverview.this, PackerActivityArtOverview.class);
                startActivity(intent_item_overview);
            }
        });
        btnSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_signat = new Intent(PackerActivityDutyOverview.this, SignatureActivity.class);
                startActivity(intent_signat);
            }
        });
        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_feed = new Intent(PackerActivityDutyOverview.this, FeedbackActivity.class);
                startActivity(intent_feed);
            }

        });
        btnPhotoPack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_PhotoPack = new Intent(PackerActivityDutyOverview.this, PhotomakerActivity.class);
                intent_PhotoPack.putExtra("keyword", "PACKING");
                startActivity(intent_PhotoPack);
            }
        });
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_finish = new Intent(PackerActivityDutyOverview.this, PackerActivityFinish.class);
                startActivity(intent_finish);
            }
        });


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }

    @SuppressLint("ResourceType")//the Resources needed for the Language PackerActivityDutyOverview
    public void updateView(String lang) {
        Context context = LocaleHelper.setLocale(this, lang);
        Resources resources = context.getResources();
        btnArticleOverview.setText(resources.getString(R.string.btnArtOverviewPac));
        btnPhotoPack.setText(resources.getString(R.string.btnPhotoPack));
        btnFeedback.setText(resources.getString(R.string.btnFeedbackDriv));
        btnSignature.setText(resources.getString(R.string.btnSignaturePac));
        btnFinish.setText(resources.getString(R.string.btnFinishdutyPac));
        getSupportActionBar().setTitle(resources.getString(R.string.title_activity_packer_duty_overview));
    }
}
