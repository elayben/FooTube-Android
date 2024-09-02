package com.example.myyoutube.api;

import com.example.myyoutube.entities.Video;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VideoApiService {
    @GET("api/videos")
    Call<List<Video>> getVideos();

    @GET("api/suggestedVideos/{email}/{videoId}")
    Call<List<Video>> getSuggestedVideos(@Path("email") String email, @Path("videoId") String videoID);

    @GET("api/users/{email}/videos")
    Call<List<Video>> getUserVideos(@Path("email") String email);

    @POST("api/users/{email}/videos")
    Call<JsonObject> createVideo(@Path("email") String email, @Body Object jsonVideo, @Header("authorization") String token);

    @PUT("api/users/{email}/videos/{vid}")
    Call<JsonObject> updateVideo(@Path("email") String email, @Path("vid") String videoId, @Body Object jsonVideo, @Header("authorization") String token);

    @DELETE("api/users/{email}/videos/{vid}")
    Call<JsonObject> deleteVideo(@Path("email") String email, @Path("vid") String videoId, @Header("authorization") String token);

    @DELETE("api/videos/{email}")
    Call<JsonObject> deleteVideosByEmail(@Path("email") String email, @Header("authorization") String token);

    @PATCH("api/users/{email}/videos/{pid}/likes")
    Call<JsonObject> likeVideo(@Path("email") String email, @Path("pid") String videoId, @Header("Authorization") String token);

    @PATCH("api/users/{email}/videos/{pid}/dislikes")
    Call<JsonObject> dislikeVideo(@Path("email") String email, @Path("pid") String videoId, @Header("Authorization") String token);

    @PATCH("api/videos/{pid}/views")
    Call<JsonObject> updateVideoViews(@Path("pid") String videoId, @Body JsonObject emailBody);
}