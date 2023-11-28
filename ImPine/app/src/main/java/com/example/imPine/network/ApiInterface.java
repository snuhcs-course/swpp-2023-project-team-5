package com.example.imPine.network;
import com.example.imPine.model.Diary;
import com.example.imPine.model.DiaryAdapter;
import com.example.imPine.model.DiaryGetResponse;
import com.example.imPine.model.DiaryResponse;
import com.example.imPine.model.FollowListResponse;
import com.example.imPine.model.Plant;
import com.example.imPine.model.PlantResponse;
import com.example.imPine.model.SignUpRequest;
import com.example.imPine.model.SignUpResponse;
import com.example.imPine.model.UserID;
import com.example.imPine.model.UserListResponse;
import com.example.imPine.model.UserResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
            @Part("avatar") RequestBody avatar,
            @Part MultipartBody.Part image
    );

    @Multipart
    @PUT("/api/plant/")
    Call<ResponseBody> editPlant(
            @Header("Authorization") String authToken,
            @Part("name") RequestBody name,
            @Part("height") RequestBody height,
            @Part("status") RequestBody status,
            @Part("last_watered") RequestBody lastWatered,
            @Part("avatar") RequestBody avatar,
            @Part("user_id") RequestBody userId,
            @Part("plant_id") RequestBody plantId,
            @Part MultipartBody.Part image
    );

    @GET("/api/follow")
    Call<FollowListResponse> getFollowList(@Header("Authorization") String authToken);

    @POST("/api/follow/{user_id}")
    Call<ResponseBody> followUser(@Header("Authorization") String authToken, @Path("user_id") int userId);

    @GET("/api/diary")
    Call<DiaryResponse> getDiaries(@Header("Authorization") String authToken);
    @GET("/api/diary/user/{user_id}")
    Call<DiaryResponse> getDiariesByUserId(@Header("Authorization") String authToken, @Path("user_id") String userId);


    @Multipart
    @POST("/api/diary/")
    Call<ResponseBody> createDiary(
            @Header("Authorization") String authToken,
            @Part("title") RequestBody title,
            @Part("content") RequestBody content,
            @Part("is_private") RequestBody isPrivate,
            @Part("category") RequestBody category,
            @Part MultipartBody.Part image
    );

    @POST("/api/diary/")
    Call<ResponseBody> createDiaryWithoutImage(
            @Header("Authorization") String authToken,
            @Body Diary diary);



//    @Multipart
//    @POST("/api/diary/")
//    Call<ResponseBody> createDiary(
//            @Header("Authorization") String authToken,
//            @Part("diary") RequestBody diary,  // Serialized Diary object
//            @Part MultipartBody.Part image     // Image file
//    );

    @POST("/predict_fcr/")
    Call<ResponseBody> predictFcrStatus(@Body RequestBody params);

    @GET("/api/user/search")
    Call<UserListResponse> searchUsers(@Header("Authorization") String authToken, @Query("username") String username);

    // Fetch a diary entry
    @GET("/api/diary/{diary_id}")
    Call<DiaryGetResponse> getDiary(
            @Header("Authorization") String authToken,
            @Path("diary_id") String diaryId);

    // Update a diary entry without image
    @FormUrlEncoded
    @PUT("/api/diary/")
    Call<ResponseBody> updateDiaryWithoutImage(
            @Header("Authorization") String authToken,
            @Field("title") String title,
            @Field("content") String content,
            @Field("is_private") boolean isPrivate,
            @Field("category") String category,
            @Field("diary_id") int diaryId
    );

    // Update a diary entry with image
    @Multipart
    @PUT("/api/diary/")
    Call<ResponseBody> updateDiaryWithImage(
            @Header("Authorization") String authToken,
            @Part("diary_id") RequestBody diaryId,
            @Part("title") RequestBody title,
            @Part("content") RequestBody content,
            @Part("is_private") RequestBody isPrivate,
            @Part("category") RequestBody category,
            @Part MultipartBody.Part image);

    // Delete a diary entry
    @HTTP(method = "DELETE", path = "/api/diary/", hasBody = true)
    Call<ResponseBody> deleteDiaryWithBody(
            @Header("Authorization") String authToken,
            @Body RequestBody diaryIdBody);

    @DELETE("/api/follow/{user_id}")
    Call<ResponseBody> unfollowUser(
            @Header("Authorization") String authToken,
            @Path("user_id") int userId);
}