package com.example.docvision;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Requests {

    @Multipart
    @POST("doOP/")
    Call<_Response> doOP(@Part MultipartBody.Part image_file,
                         @Query("operation") String op);


    @GET("getOP/")
    Call<String> down(@Query("filename") String filename);

}
