package com.htw.smartloadapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.htw.smartloadapp.webservice.download.data.DownloadProgressResponseBody;
import com.htw.smartloadapp.webservice.download.data.RestArticleOverviewGet;
import com.htw.smartloadapp.webservice.management.order.SelectedFreightOrder;
import com.htw.smartloadapp.webservice.management.order.ArticleOverviewList;
import com.htw.smartloadapp.layout.adapter.ArticleOverviewAdapter;
import com.htw.smartloadapp.webservice.task.bodies.ArticleOverviewTask;

public class PackerActivityArtOverview extends MenuActivity {
    private static final String TAG = "PackerActivityArtDetail";
    ListView lvArtOverview;
    ArticleOverviewList articleOverviewList;
    Button btnRefreshArticleOverview;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packer_art_overview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Article Overview");
        lvArtOverview = (ListView)findViewById(R.id.lvArtOverviewPac);
        btnRefreshArticleOverview = (Button) findViewById(R.id.btnRefreshArticleOverviewPac);

        progressBar = new ProgressBar(this);

        synchronizeListViewData();

        btnRefreshArticleOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronizeListViewData();
            }
        });

        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Article Overview");


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

    @SuppressLint("ResourceType")//the Resources needed for the Language DriverActivityLandingPage
    public void updateView(String lang) {
        Context context = LocaleHelper.setLocale(this,lang);
        Resources resources = context.getResources();
        btnRefreshArticleOverview.setText(resources.getString((R.string.btnRefreshArticleOverviewPac)));
        getSupportActionBar().setTitle(resources.getString(R.string.title_activity_packer_art_details));
    }

    private void synchronizeListViewData(){

        final RestArticleOverviewGet restArticleOverviewGet = new RestArticleOverviewGet(progressBar);
        ArticleOverviewTask articleOverviewTask = new ArticleOverviewTask(SelectedFreightOrder.getInstance().getSelectedFreightOrder().getFreightOrderId());
        restArticleOverviewGet.downloadArticleOverview(articleOverviewTask, new DownloadProgressResponseBody.DownloadCallbacks() {
            @Override
            public void onProgressUpdate(long bytesRead, long contentLength, boolean done) {
                Log.d(TAG, "onProgressUpdate: " + "BytesRead=" + bytesRead + " ContentLength=" + contentLength + " Done=" + done);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(PackerActivityArtOverview.this,"Error downloading article overview from server" ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {

                ArticleOverviewList.getInstance().setArticleOverview(restArticleOverviewGet.getDownloadedArticleOverview());

                // Arrayadapter to connect freightOrderListDummy to lvArtOverview
                articleOverviewList = ArticleOverviewList.getInstance();

                ArticleOverviewAdapter articleOverviewAdapter = new ArticleOverviewAdapter(
                        PackerActivityArtOverview.this,
                        R.layout.list_item_articleoverview,
                        articleOverviewList.getArticleOverview());

                lvArtOverview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int test = lvArtOverview.getPositionForView(view);
                        Log.d(TAG, "onItemClick: " + test);
                    }
                });
                lvArtOverview.setAdapter(articleOverviewAdapter);
            }
        });


    }
}
