package com.htw.smartloadapp.webservice.download.data;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.htw.smartloadapp.webservice.ClientRestWebservice;
import com.htw.smartloadapp.webservice.RestWebserviceSettings;
import com.htw.smartloadapp.webservice.restservice.model.ArticleOverviewModel;
import com.htw.smartloadapp.webservice.restservice.model.FreightOrderModel;
import com.htw.smartloadapp.webservice.task.bodies.ArticleOverviewTask;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestArticleOverviewGet {
    private static final String TAG = "RestFreightOrderListGet";
    private static final int DEFAULT_TIMEOUT = 15; //change this to control time till timeout

    private ProgressBar progressBar;
    private ArrayList<ArticleOverviewModel> downloadedArticleOverview;
    private boolean isDownloaded = false;

    Retrofit retrofit;
    ClientRestWebservice clientRestWebservice;

    public RestArticleOverviewGet(ProgressBar progressBar) {
        this.progressBar = progressBar;

        // gson necessary because LoginTask is header with Content-Type: json/application
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        // creating retrofit object
        retrofit = new Retrofit.Builder()
                .baseUrl(RestWebserviceSettings.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        // creating client
        clientRestWebservice = retrofit.create(ClientRestWebservice.class);
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public ArrayList<ArticleOverviewModel> getDownloadedArticleOverview() {
        return downloadedArticleOverview;
    }

    public void downloadArticleOverview(ArticleOverviewTask articleOverviewTask, final DownloadProgressResponseBody.DownloadCallbacks downloadCallback){

        String baseUrl = RestWebserviceSettings.getBaseUrl();

        //Set up Progressbar
        try{
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
        }catch (Exception e){
            Log.e(TAG, "downloadArticleOverview: " + e.getMessage() );
        }

        try{
            // creating a call and calling getFreightOrders()
            Call<ArrayList<ArticleOverviewModel>> call = clientRestWebservice.getArticleOverview(articleOverviewTask);
            // finally performing the call
            call.enqueue(new Callback<ArrayList<ArticleOverviewModel>>() {
                @Override
                public void onResponse(Call<ArrayList<ArticleOverviewModel>> call, Response<ArrayList<ArticleOverviewModel>> response) {
                    if (response.isSuccessful()){
                        downloadedArticleOverview = response.body();
                        Gson gson = new Gson();
                        String content = gson.toJson(downloadedArticleOverview);
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
                public void onFailure(Call<ArrayList<ArticleOverviewModel>> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage() );
                    isDownloaded = false;
                    downloadCallback.onError();
                }
            });
        }catch (Exception e){
            Log.e(TAG, "downloadArticleOverview: Error on Download " + e.getMessage() );
            isDownloaded = false;
            downloadCallback.onError();
        }
    }
}
