package com.theworld.androidtemplatewithnavcomponents.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.theworld.androidtemplatewithnavcomponents.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repo: AuthRepo) : ViewModel() {


    /*------------------------------------ Login -----------------------------------------*/

    fun login(
        email: String,
        password: String
    ) = liveData(Dispatchers.IO) {

        try {
            emit(Resource.Loading)
            emit(Resource.Success(repo.login(email, password)))
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

    fun register(
        name: String,
        email: String,
        mobile_no: String,
        password: String
    ) = liveData(Dispatchers.IO) {

        try {
            emit(Resource.Loading)
            emit(Resource.Success(repo.register(name, email, mobile_no, password)))
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