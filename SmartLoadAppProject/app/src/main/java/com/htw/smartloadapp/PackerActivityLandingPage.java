package com.htw.smartloadapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.htw.smartloadapp.webservice.download.data.DownloadProgressResponseBody;
import com.htw.smartloadapp.webservice.download.data.RestFreightOrderListGet;
import com.htw.smartloadapp.webservice.management.order.FreightOrderList;
import com.htw.smartloadapp.webservice.management.order.SelectedFreightOrder;
import com.htw.smartloadapp.webservice.restservice.model.FreightOrderModel;

import java.util.ArrayList;

public class PackerActivityLandingPage extends MenuActivity {

    private static final String TAG = "PackerActivityLandingPa";

    // This class shows a list of orders received from the bss database
    // The user chooses an order and app will continue to DriverActivityOrderOverview

    ListView lvFreightOrders;
    ArrayList<FreightOrderModel> freightOrderListForPacker;
    ProgressBar progressBar;

    Button btnRefreshFreightOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packer_landing_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("PackerLandingPage");

        //Back button
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //  getSupportActionBar().setDisplayShowHomeEnabled(true);

        lvFreightOrders = (ListView)findViewById(R.id.lvFreightOrderListPac);
        progressBar = new ProgressBar(this);

        synchronizeListViewData();

        btnRefreshFreightOrders = (Button) findViewById(R.id.btnRefreshFreightOrdersPac);

        btnRefreshFreightOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronizeListViewData();
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
        btnRefreshFreightOrders.setText(resources.getString((R.string.btnRefreshFreightOrdersPac)));
        getSupportActionBar().setTitle(resources.getString(R.string.title_activity_packer_landing_page));
    }

    private static final int TIME_DELAY=2000;
    private static long back_pressed;
    @Override
    public void onBackPressed() {

        if(back_pressed+TIME_DELAY>System.currentTimeMillis()){
            Intent intent=new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(this,"Press once again to exit",Toast.LENGTH_SHORT).show();
        }
        back_pressed=System.currentTimeMillis();
    }

    private void synchronizeListViewData(){

        final RestFreightOrderListGet restFreightOrderListGet = new RestFreightOrderListGet(progressBar);
        restFreightOrderListGet.downloadFreightOrderList(new DownloadProgressResponseBody.DownloadCallbacks() {
            @Override
            public void onProgressUpdate(long bytesRead, long contentLength, boolean done) {
                Log.d(TAG, "onProgressUpdate: " + "BytesRead=" + bytesRead + " ContentLength=" + contentLength + " Done=" + done);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(PackerActivityLandingPage.this,"Error downloading freight orders from server" ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {

                FreightOrderList.getInstance().setFreightOrders(restFreightOrderListGet.getDownloadedFreightOrderList());
                freightOrderListForPacker = FreightOrderList.getInstance().getFreightOrdersPacker();

                // Arrayadapter to connect freightOrderListDummy to lv_freightOrders
                ArrayAdapter<FreightOrderModel> arrayAdapter = new ArrayAdapter<FreightOrderModel>(
                        PackerActivityLandingPage.this,
                        R.layout.list_item_freightorder,
                        freightOrderListForPacker
                );

                lvFreightOrders.setAdapter(arrayAdapter);

                // Make items of list clickable
                lvFreightOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                        Intent intent_lpPacker_orderOverview = new Intent(PackerActivityLandingPage.this, PackerActivityOrderOverview.class);
                        SelectedFreightOrder.getInstance().setSelectedFreightOrder(freightOrderListForPacker.get(index));
                        PackerActivityLandingPage.this.startActivity(intent_lpPacker_orderOverview);
                    }
                });
            }
        });
    }




}