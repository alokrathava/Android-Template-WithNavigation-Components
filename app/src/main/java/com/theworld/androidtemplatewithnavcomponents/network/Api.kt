package com.theworld.androidtemplatewithnavcomponents.network

import com.theworld.androidtemplatewithnavcomponents.data.user.User
import com.theworld.androidtemplatewithnavcomponents.data.user.UserLoginRequestData
import com.theworld.androidtemplatewithnavcomponents.data.user.UserRegisterRequestData
import okhttp3.ResponseBody
import retrofit2.http.*

interface Api {

    /*------------------------------------ Login ------------------------------------*/

    @POST("user/login")
    suspend fun login(@Body loginRequestData: UserLoginRequestData): User


    /*--------------------------------- Social Login ------------------------------------*/

    @FormUrlEncoded
    @POST("user/social_login")
    suspend fun socialLogin(
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("person_id") personId: String,
        @Field("profile_image") profileImage: String,
    ): ResponseBody


    /*------------------------------------ Register ------------------------------------*/

    @POST("user/register")
    suspend fun register(@Body registerRequestData: UserRegisterRequestData): User


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


    /*---------------------------------- Get Profile ------------------------------------*/

    @GET("user/profile")
    suspend fun getProfile(): User

}
