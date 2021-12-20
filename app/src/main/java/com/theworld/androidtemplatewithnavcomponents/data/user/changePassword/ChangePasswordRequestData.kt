package com.theworld.androidtemplatewithnavcomponents.data.user.changePassword


data class ChangePasswordRequestData(
    val oldPassword: String,
    val newPassword: String,
    val role: String,
)
