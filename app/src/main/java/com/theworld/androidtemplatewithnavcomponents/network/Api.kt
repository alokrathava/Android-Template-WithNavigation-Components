package com.theworld.androidtemplatewithnavcomponents.network

import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Api {

    /*------------------------------------ Login ------------------------------------*/

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): ResponseBody


    /*------------------------------------ Login ------------------------------------*/

    @FormUrlEncoded
    @POST("social_login")
    suspend fun socialLogin(
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("person_id") personId: String,
        @Field("profile_image") profileImage: String,
    ): ResponseBody


    /*------------------------------------ Register ------------------------------------*/

    @FormUrlEncoded
    @POST("registration")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("mobile_no") mobile_no: String,
        @Field("password") password: String
    ): ResponseBody


    /*------------------------------------ Reset Password ------------------------------------*/

    @FormUrlEncoded
    @POST("user/reset/password")
    suspend fun resetPassword(
        @Field("mobile_no") mobile_no: String,
        @Field("password") password: String,
    ): ResponseBody


    /*------------------------------------ Change Password ------------------------------------*/

    @FormUrlEncoded
    @POST("changepwd")
    suspend fun changePassword(
        @Field("user_id") userId: Int,
        @Field("old_pwd") currentPassword: String,
        @Field("new_pwd") password: String,
    ): ResponseBody


}
