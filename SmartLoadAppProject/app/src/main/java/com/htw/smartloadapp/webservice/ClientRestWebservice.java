package com.htw.smartloadapp.webservice;


import com.htw.smartloadapp.webservice.login.EnumUserRole;
import com.htw.smartloadapp.webservice.restservice.model.ArticleOverviewModel;
import com.htw.smartloadapp.webservice.restservice.model.FreightOrderModel;
import com.htw.smartloadapp.webservice.restservice.model.PhotoUploadModel;
import com.htw.smartloadapp.webservice.restservice.model.SmartLoadModel;
import com.htw.smartloadapp.webservice.task.bodies.ArticleOverviewTask;
import com.htw.smartloadapp.webservice.task.bodies.FreightOrderStatusTask;
import com.htw.smartloadapp.webservice.task.bodies.LoginTask;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ClientRestWebservice {

    //String BASE_URL = "https://rs.bss-bln.de";
    // Implement all features of REST Webservice here

    @Headers("Content-Type: application/json")
    @POST("/api/Authentication")
    Call<EnumUserRole>login(@Body LoginTask loginTask);

    @GET("/api/FreightOrders")
    Call<ArrayList<FreightOrderModel>> getFreightOrders();

    @GET("/api/Smartload")
    Call<List<SmartLoadModel>> getSmartLoads();

    @Headers("Content-Type: application/json")
    @POST("/api/Article_Overview")
    Call<ArrayList<ArticleOverviewModel>>getArticleOverview(@Body ArticleOverviewTask articleOverviewTask);

    @Multipart
    @POST("/api/Photoupload/upload")
    Call<PhotoUploadModel> uploadPhoto(
            @Part MultipartBody.Part filePart
    );

    @Headers("Content-Type: application/json")
    @POST("/api/FreightOrderStatus")
    Call<String>uploadFreightOrderStatus(@Body FreightOrderStatusTask freightOrderStatusTask);

    @Headers("Content-Type: application/json")
    @POST("/api/AllAppData")
    Call<String>uploadFreightOrder(@Body FreightOrderModel freightOrder);



}
