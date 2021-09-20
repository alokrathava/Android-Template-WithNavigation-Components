package com.theworld.androidtemplatewithnavcomponents.ui.auth

import androidx.lifecycle.liveData
import com.theworld.androidtemplatewithnavcomponents.data.user.UserLoginRequestData
import com.theworld.androidtemplatewithnavcomponents.data.user.UserRegisterRequestData
import com.theworld.androidtemplatewithnavcomponents.utils.SafeApiCall
import com.theworld.androidtemplatewithnavcomponents.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class AuthRepo @Inject constructor(private val api: Api) : SafeApiCall {


    /*-------------------- Login ---------------------*/

    suspend fun login(loginRequestData: UserLoginRequestData) = safeApiCall {
        api.login(loginRequestData)
    }


    /*-------------------- Social Login ---------------------*/

    suspend fun socialLogin(
        email: String,
        name: String,
        personId: String,
        profileImage: String
    ) = api.socialLogin(email, name, personId, profileImage)


    /*-------------------- Register ---------------------*/

    suspend fun register(registerRequestData: UserRegisterRequestData) = safeApiCall {
        api.register(registerRequestData)
    }


    /*------------------------------------ Reset Password ------------------------------------*/

    suspend fun resetPassword(
        mobile_no: String,
        password: String,
    ) = api.resetPassword(mobile_no, password)


    /*------------------------------------ Change Password ------------------------------------*/


    suspend fun changePassword(
        userId: Int,
        currentPassword: String,
        password: String,
    ) = api.changePassword(userId, currentPassword, password)


    /*-------------------- Profile ---------------------*/

    suspend fun getProfile() = api.getProfile()

}