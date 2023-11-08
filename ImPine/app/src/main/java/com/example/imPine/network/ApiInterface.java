package com.example.imPine.network;
import com.example.imPine.model.Plant;
import com.example.imPine.model.PlantResponse;
import com.example.imPine.model.SignUpRequest;
import com.example.imPine.model.SignUpResponse;
import com.example.imPine.model.UserID;
import com.example.imPine.model.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    @POST("/api/user/signin")
    Call<SignUpResponse> createUser(@Header("Authorization") String authToken,
                                    @Body SignUpRequest signUpRequest);

    @GET("/api/user")
    Call<UserResponse> getUser(@Header("Authorization") String authToken);

    @GET("/api/plant/user/{user_id}")
    Call<PlantResponse> getUserPlants(@Header("Authorization") String authToken, @Path("user_id") String userId);
    @POST("/api/plant/")
    Call<Plant> createPlant(@Header("Authorization") String authToken, @Body Plant plant);

}