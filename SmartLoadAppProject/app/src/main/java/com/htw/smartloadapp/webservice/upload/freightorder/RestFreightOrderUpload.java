package com.htw.smartloadapp.webservice.upload.freightorder;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.htw.smartloadapp.webservice.ClientRestWebservice;
import com.htw.smartloadapp.webservice.RestWebserviceSettings;
import com.htw.smartloadapp.webservice.download.data.DownloadProgressResponseBody;
import com.htw.smartloadapp.webservice.restservice.model.FreightOrderModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestFreightOrderUpload {
    private static final String TAG = "RestFreightOrderUpload";

    Retrofit retrofit;
    ClientRestWebservice clientRestWebservice;
    FreightOrderModel freightOrder;

    public RestFreightOrderUpload(FreightOrderModel freightOrder) {
        this.freightOrder = freightOrder;

        // gson necessary because FreightOrderUpload is header with Content-Type: json/application
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
    public void uploadFreightOrder(final DownloadProgressResponseBody.DownloadCallbacks downloadCallback){
        try{
            // creating a call and calling  uploadFreightOrder()
            Call<String> call = clientRestWebservice.uploadFreightOrder(freightOrder);
            // finally performing the call
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if(response.isSuccessful()){
                        Log.d(TAG, "onResponse: code" + response.body());
                        downloadCallback.onFinish();
                    }else{
                        Log.e(TAG, "onResponse: uploadFreightOrder " + response.code() );
                        downloadCallback.onError();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, "onFailure: uploadFreightOrder " + t.getMessage() + " " + t.getStackTrace() );
                    downloadCallback.onError();
                }
            });



        }catch(Exception e){
            Log.e(TAG, "Error uploadFreightOrder: " + e.getMessage() );
        }
    }



}
