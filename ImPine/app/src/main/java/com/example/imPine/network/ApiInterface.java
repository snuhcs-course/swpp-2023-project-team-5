package com.example.imPine.network;
import com.example.imPine.model.FollowListResponse;
import com.example.imPine.model.Plant;
import com.example.imPine.model.PlantResponse;
import com.example.imPine.model.SignUpRequest;
import com.example.imPine.model.SignUpResponse;
import com.example.imPine.model.UserID;
import com.example.imPine.model.UserResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {
    @POST("/api/user/signin")
    Call<SignUpResponse> createUser(@Header("Authorization") String authToken,
                                    @Body SignUpRequest signUpRequest);

    @GET("/api/user")
    Call<UserResponse> getUser(@Header("Authorization") String authToken);

    @GET("/api/plant/user/{user_id}")
    Call<PlantResponse> getUserPlants(@Header("Authorization") String authToken, @Path("user_id") String userId);
    @Multipart
    @POST("/api/plant/")
    Call<ResponseBody> createPlant(
            @Header("Authorization") String authToken,
            @Part("name") RequestBody name,
            @Part("height") RequestBody height,
            @Part MultipartBody.Part image
    );
    @Multipart
    @PUT("/api/plant/")
    Call<ResponseBody> editPlant(
            @Header("Authorization") String authToken,
            @Part("name") RequestBody name,
            @Part("height") RequestBody height,
            @Part("plant_id") RequestBody plantId,
            @Part MultipartBody.Part image
    );

    @GET("/api/follow")
    Call<FollowListResponse> getFollowList(@Header("Authorization") String authToken);

    @POST("/api/follow/{user_id}")
    Call<ResponseBody> followUser(@Header("Authorization") String authToken, @Path("user_id") int userId);
}