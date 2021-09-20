package com.theworld.androidtemplatewithnavcomponents.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.theworld.androidtemplatewithnavcomponents.data.user.UserLoginRequestData
import com.theworld.androidtemplatewithnavcomponents.data.user.UserRegisterRequestData
import com.theworld.androidtemplatewithnavcomponents.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repo: AuthRepo) : ViewModel() {


    /*------------------------------------ Login -----------------------------------------*/

    fun login(loginRequestData: UserLoginRequestData) = liveData {
        emit(repo.login(loginRequestData))
    }


    /*-------------------- Social Login ---------------------*/

    fun socialLogin(
        email: String,
        name: String,
        personId: String,
        profileImage: String
    ) = liveData(Dispatchers.IO) {
        try {
            emit(Resource.Success(repo.socialLogin(email, name, personId, profileImage)))
        } catch (exception: Exception) {

            when (exception) {
                is HttpException -> {
                    emit(Resource.Failure(exception.code(), false, exception.message()))
                }
                is IOException -> {
                    emit(Resource.Failure(null, true, exception.message))
                }
                else -> emit(Resource.Failure(null, true, exception.message))
            }
        }
    }


    /*------------------------------------ Register -----------------------------------------*/

    fun register(registerRequestData: UserRegisterRequestData) = liveData {
            emit(repo.register(registerRequestData))
        }


    /*----------------------------------------- Update Password -------------------------------*/

    fun resetPassword(
        mobile_no: String,
        password: String,
    ) = liveData(Dispatchers.IO) {

        try {
            emit(Resource.Success(repo.resetPassword(mobile_no, password)))
        } catch (exception: Exception) {

            when (exception) {
                is HttpException -> {
                    emit(Resource.Failure(exception.code(), false, exception.message()))
                }
                is IOException -> {
                    emit(Resource.Failure(null, true, exception.message))
                }
                else -> emit(Resource.Failure(null, true, exception.message))
            }
        }
    }


    /*----------------------------------------- Change Password -------------------------------*/

    fun changePassword(
        userId: Int,
        currentPassword: String,
        password: String,
    ) = liveData(Dispatchers.IO) {

        try {
            emit(Resource.Success(repo.changePassword(userId, currentPassword, password)))
        } catch (exception: Exception) {

            when (exception) {
                is HttpException -> {
                    emit(Resource.Failure(exception.code(), false, exception.message()))
                }
                is IOException -> {
                    emit(Resource.Failure(null, true, exception.message))
                }
                else -> emit(Resource.Failure(null, true, exception.message))
            }
        }
    }

}