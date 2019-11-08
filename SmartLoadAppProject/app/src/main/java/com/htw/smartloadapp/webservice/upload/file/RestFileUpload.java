package com.htw.smartloadapp.webservice.upload.file;

import android.app.ProgressDialog;
import android.util.Log;


import com.google.gson.Gson;
import com.htw.smartloadapp.webservice.ClientRestWebservice;
import com.htw.smartloadapp.webservice.RestWebserviceSettings;
import com.htw.smartloadapp.webservice.restservice.model.PhotoUploadModel;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestFileUpload {
    private static final String TAG = "RestFileUpload";

    ProgressDialog progressDialog;

    public RestFileUpload(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public void uploadFiles(String filekey, ArrayList<File> files, final UploadProgressRequestBody.UploadCallbacks uploadCallback) {
        Log.d(TAG, "uploadFile: Uploading files");

        String baseUrl = RestWebserviceSettings.getBaseUrl();

        //Set up ProgressDialog
        try{
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Uploading Files");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100); // Progress Dialog Max Value
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            progressDialog.show();
        }catch (Exception e){
            Log.e(TAG, "uploadFiles: " + e.getMessage() );
        }

        try {
            File file;
            // necessary to call OnFinish() when onResponse of last File is called
            final String lastFileName = files.get(files.size()-1).getName();
            for(int i = 0; i < files.size(); i++){
                file = files.get(i);
                UploadProgressRequestBody fileBody = new UploadProgressRequestBody(file, "image",uploadCallback);
                final MultipartBody.Part filePart = MultipartBody.Part.createFormData(filekey, file.getName(), fileBody);

                //creating retrofit object
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                //creating client
                ClientRestWebservice clientRestWebservice = retrofit.create(ClientRestWebservice.class);

                //creating a call and calling uploadPhoto()
                Call<PhotoUploadModel> call = clientRestWebservice.uploadPhoto(filePart);

                //finally performing the call
                call.enqueue(new Callback<PhotoUploadModel>() {
                    @Override
                    public void onResponse(Call<PhotoUploadModel> call, Response<PhotoUploadModel> response) {
                        if (response.isSuccessful()) {
                            PhotoUploadModel uploadedPhoto = response.body();
                            Gson gson = new Gson();
                            String content = gson.toJson(uploadedPhoto);
                            Log.d(TAG, "onResponse: Successfully uploaded file " + content);
                            Log.d(TAG, "onResponse: Filename- Webserver: " + uploadedPhoto.getName() + "Filelist: " + lastFileName);
                            // Call onFinish() in Progress-Requestbody of CallerActivity when last was successfully uploaded
                            if(lastFileName.equals(uploadedPhoto.getName())){
                                uploadCallback.onFinish();
                            }

                        } else {
                            Log.e(TAG, "onResponse: " + "Error on Upload: " + response.code() );
                            uploadCallback.onError();
                        }
                    }
                    @Override
                    public void onFailure(Call<PhotoUploadModel> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + "Error on Upload: " + t.getMessage() );
                        uploadCallback.onError();
                    }
                });
            }

        }catch (Exception e){
            Log.e(TAG, "uploadFiles: Error on Upload" + e.getMessage() );
            uploadCallback.onError();
        }
    }
}
