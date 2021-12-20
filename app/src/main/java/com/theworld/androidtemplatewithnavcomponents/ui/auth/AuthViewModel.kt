package com.theworld.androidtemplatewithnavcomponents.ui.auth

import androidx.lifecycle.*
import com.theworld.androidtemplatewithnavcomponents.data.user.UserLoginRequestData
import com.theworld.androidtemplatewithnavcomponents.data.user.UserRegisterRequestData
import com.theworld.androidtemplatewithnavcomponents.data.user.changePassword.ChangePasswordRequestData
import com.theworld.androidtemplatewithnavcomponents.extras.RoleEnum
import com.theworld.androidtemplatewithnavcomponents.network.Api
import com.theworld.androidtemplatewithnavcomponents.utils.SafeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: Api,
    private val state: SavedStateHandle,
) : ViewModel(), SafeApiCall {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _snackbar = MutableSharedFlow<String>()
    val snackbar = _snackbar.asSharedFlow()


    fun displaySnackBar(msg: String) {
        viewModelScope.launch {
            _snackbar.emit(msg)
        }
    }


    /*------------------------------------ Login -----------------------------------------*/

    fun login(loginRequestData: UserLoginRequestData) = flow {
        /* val list = if (loginRequestData.type == RoleEnum.SELLER.name) {
             safeApiCall { api.sellerLogin(loginRequestData) }
         } else {
             safeApiCall { api.buyerLogin(loginRequestData) }
         }*/

        val list = api.login(loginRequestData)
        emit(list)
    }.flowOn(Dispatchers.IO)
        .conflate()


/*------------------------------------ Change Password -----------------------------------------*/

    fun changePassword(data: ChangePasswordRequestData) = flow {
        val list = safeApiCall {
            api.changePassword(data)
        }
        emit(list)

    }.flowOn(Dispatchers.IO)
        .conflate()


    /*----------------------------------- Register -----------------------------------------*/

    fun register(data: UserRegisterRequestData) = flow {
        val list = safeApiCall { api.register(data) }
        emit(list)
    }.flowOn(Dispatchers.IO)
        .conflate()

    /*


     fun sellerRegister(data: SellerRegisterRequestData) = flow {
         val list = safeApiCall { api.sellerRegister(data) }
         emit(list)
     }.flowOn(Dispatchers.IO)
         .conflate()





     *//*------------------------------------ logout -----------------------------------------*//*

    fun logout(role: RoleEnum) = flow {
        val list = if (role == RoleEnum.SELLER) {
            safeApiCall { api.sellerLogout() }
        } else {
            safeApiCall { api.buyerLogout() }
        }
        emit(list)

    }.flowOn(Dispatchers.IO)
        .conflate()
*/

}
