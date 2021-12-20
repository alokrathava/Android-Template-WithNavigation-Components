package com.theworld.androidtemplatewithnavcomponents.data.multipartForm

import java.io.File

data class MultipartBodyFormRequest(
        val key: String,
        val value: String? = null,
        val isFile: Boolean = false,
        val mime: String = "image/jpeg",
        val file: File? = null,
)