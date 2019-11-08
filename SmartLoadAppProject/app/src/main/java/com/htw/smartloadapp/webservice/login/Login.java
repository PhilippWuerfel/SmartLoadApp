package com.htw.smartloadapp.webservice.login;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.htw.smartloadapp.webservice.ClientRestWebservice;
import com.htw.smartloadapp.webservice.RestWebserviceSettings;
import com.htw.smartloadapp.webservice.download.data.DownloadProgressResponseBody;
import com.htw.smartloadapp.webservice.task.bodies.LoginTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Login {
    private static final String TAG = "Login";

    String userID;
    String userPassword;
    boolean isLoggedIn;
    private EnumUserRole userRole; // = EnumUserRole.NONE;

    Retrofit retrofit;
    ClientRestWebservice clientRestWebservice;
    LoginTask loginTask;


    public Login(String userID, String userPassword){
        this.userID = userID;
        this.userPassword = userPassword;
        this.isLoggedIn = false;
        this.userRole = EnumUserRole.NONE;
        // gson necessary because LoginTask is header with Content-Type: json/application
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        try{
            // creating retrofit object
            retrofit = new Retrofit.Builder()
                    .baseUrl(RestWebserviceSettings.getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            // creating client
            clientRestWebservice = retrofit.create(ClientRestWebservice.class);
            // creating task
            loginTask = new LoginTask(userID, userPassword);
        }catch(IllegalArgumentException e){
            Log.e(TAG, "Login: No URL" + e.getMessage() );
        }


    }

    public void validateCredentials(final DownloadProgressResponseBody.DownloadCallbacks downloadCallback){
        try {
            Log.d(TAG, "Validating credentials");

            try{
                // creating a call and calling  getMeasurementPoints()
                Call<EnumUserRole> call = clientRestWebservice.login(loginTask);
                // finally performing the call
                call.enqueue(new Callback<EnumUserRole>() {
                    @Override
                    public void onResponse(Call<EnumUserRole> call, Response<EnumUserRole> response) {
                        if(response.isSuccessful()){
                            //String testString = response.body();
                            userRole = response.body();
                            /*
                            Gson gson = new Gson();
                            String content = gson.toJson(userRole);*/
                            Log.d(TAG, "onResponse: login " + userRole);
                            downloadCallback.onFinish();
                        }else {
                            Log.e(TAG, "onResponse: login Code" + response.code() );
                            downloadCallback.onError();
                        }
                    }

                    @Override
                    public void onFailure(Call<EnumUserRole> call, Throwable t) {
                        Log.e(TAG, "onFailure: login " + t.getMessage() + " " + t.getStackTrace() );
                        downloadCallback.onError();
                    }
                });

            }catch(Exception e){
                Log.e(TAG, "validateCredentials: Error " + e.getMessage() );
            }
        }
        catch (Exception e){
            Log.e(TAG, "Error whilst validating: " + e.getMessage() );
            userRole = EnumUserRole.NONE;
        }
    }
    public EnumUserRole getUserRole(){
        return this.userRole;
    }
}
