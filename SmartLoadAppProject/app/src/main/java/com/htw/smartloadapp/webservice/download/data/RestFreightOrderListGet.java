package com.htw.smartloadapp.webservice.download.data;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.htw.smartloadapp.webservice.ClientRestWebservice;
import com.htw.smartloadapp.webservice.RestWebserviceSettings;
import com.htw.smartloadapp.webservice.restservice.model.FreightOrderModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestFreightOrderListGet {
    private static final String TAG = "RestFreightOrderListGet";
    private static final int DEFAULT_TIMEOUT = 15; //change this to control time till timeout

    private ProgressBar progressBar;
    private ArrayList<FreightOrderModel> downloadedFreightOrderList;
    private boolean isDownloaded = false;

    public RestFreightOrderListGet(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public ArrayList<FreightOrderModel> getDownloadedFreightOrderList() {
        return downloadedFreightOrderList;
    }

    public void downloadFreightOrderList(final DownloadProgressResponseBody.DownloadCallbacks downloadCallback){

        String baseUrl = RestWebserviceSettings.getBaseUrl();

        //Set up Progressbar
        try{
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
        }catch (Exception e){
            Log.e(TAG, "downloadFreightOrderList: " + e.getMessage() );
        }

        try{
            // creating retrofit object
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            // creating client
            ClientRestWebservice clientRestWebservice = retrofit.create(ClientRestWebservice.class);
            // creating a call and calling getFreightOrders()
            Call<ArrayList<FreightOrderModel>> call = clientRestWebservice.getFreightOrders();
            // finally performing the call
            call.enqueue(new Callback<ArrayList<FreightOrderModel>>() {
                @Override
                public void onResponse(Call<ArrayList<FreightOrderModel>> call, Response<ArrayList<FreightOrderModel>> response) {
                    if (response.isSuccessful()){
                        downloadedFreightOrderList = response.body();
                        Gson gson = new Gson();
                        String content = gson.toJson(downloadedFreightOrderList);
                        Log.d(TAG, "onResponse: " + content);
                        isDownloaded = true;
                        downloadCallback.onFinish();
                    }else{
                        Log.e(TAG, "onResponse: Code " + response.code() );
                        isDownloaded = false;
                        downloadCallback.onError();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<FreightOrderModel>> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage() );
                    isDownloaded = false;
                    downloadCallback.onError();
                }
            });
        }catch (Exception e){
            Log.e(TAG, "downloadFreightOrderList: Error on Download " + e.getMessage() );
            isDownloaded = false;
            downloadCallback.onError();
        }
    }
}
