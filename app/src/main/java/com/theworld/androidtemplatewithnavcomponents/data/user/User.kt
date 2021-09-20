package com.theworld.androidtemplatewithnavcomponents.data.user


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("deleted_at")
    val deletedAt: String? = null,

    @SerializedName("email")
    val email: String,

    @SerializedName("id")
    val id: Int,

    @SerializedName("mobile_no")
    val mobileNo: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("updated_at")
    val updatedAt: String
)