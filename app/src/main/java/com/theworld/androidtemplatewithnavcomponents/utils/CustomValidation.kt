package com.theworld.androidtemplatewithnavcomponents.utils

data class CustomValidation(
    val isEmail: Boolean = false,
    val isLengthRequired: Boolean = false,
    val length: Int = 0,
)
