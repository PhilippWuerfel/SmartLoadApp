package com.htw.smartloadapp.webservice.upload.freightorder;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.htw.smartloadapp.webservice.ClientRestWebservice;
import com.htw.smartloadapp.webservice.RestWebserviceSettings;
import com.htw.smartloadapp.webservice.download.data.DownloadProgressResponseBody;
import com.htw.smartloadapp.webservice.task.bodies.FreightOrderStatusTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestFreightOrderStatusUpload {
    private static final String TAG = "RestFreightOrderStatusU";

    Retrofit retrofit;
    ClientRestWebservice clientRestWebservice;
    FreightOrderStatusTask freightOrderStatusTask;

    public RestFreightOrderStatusUpload(FreightOrderStatusTask freightOrderStatusTask) {
        this.freightOrderStatusTask = freightOrderStatusTask;

        // gson necessary because FreightOrderStatusTask is header with Content-Type: json/application
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        // creating retrofit object
        retrofit = new Retrofit.Builder()
                .baseUrl(RestWebserviceSettings.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        // creating client
        clientRestWebservice = retrofit.create(ClientRestWebservice .class);
    }
    public void uploadFreightOrderStatus(final DownloadProgressResponseBody.DownloadCallbacks downloadCallback){
        try{
            // creating a call and calling  uploadFreightOrderStatus()
            Call<String> call = clientRestWebservice.uploadFreightOrderStatus(freightOrderStatusTask);
            // finally performing the call
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if(response.isSuccessful()){
                        Log.d(TAG, "onResponse: code" + response.body());
                        downloadCallback.onFinish();
                    }else{
                        Log.e(TAG, "onResponse: uploadFreightOrderStatus " + response.code() );
                        downloadCallback.onError();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, "onFailure: uploadFreightOrderStatus " + t.getMessage() + " " + t.getStackTrace() );
                    downloadCallback.onError();
                }
            });

        }catch(Exception e){
            Log.e(TAG, "Error uploadFreightOrderStatus: " + e.getMessage() );
        }
    }



}
