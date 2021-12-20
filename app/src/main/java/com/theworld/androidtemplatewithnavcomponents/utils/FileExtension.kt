package com.theworld.androidtemplatewithnavcomponents.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.theworld.androidtemplatewithnavcomponents.data.multipartForm.MultipartBodyFormRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.util.*


const val TAG = "FILE EXTENSION"


/*----------------------- File to Request Body + Multipart ------------------------------*/

fun File.fileToRequestBody() = this.asRequestBody("image/*".toMediaTypeOrNull())
//fun File.fileToRequestBody() = this.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//fun File.fileToRequestBody() = this.asRequestBody("application/octet".toMediaTypeOrNull())

fun InputStream.fileToRequestBody() =
        this.readBytes()
                .toRequestBody("image/*".toMediaTypeOrNull(), 0, this.readBytes().size)


fun File.fileToMultiPart(key: String) = MultipartBody.Part.createFormData(
        key,
        this.name,
        this.fileToRequestBody()
)

fun InputStream.fileToMultiPart(key: String) = MultipartBody.Part.createFormData(
        key,
        "${UUID.randomUUID()}.jpg",
        this.fileToRequestBody()
)

fun File.convertPartToRequestBody(key: String): String {
    return "$key\"; filename=\"${this.name}\""
}


/*----------------------- Create Multipart Builder (@Body purpose) ------------------------------*/

fun createMultiPartRequestBundle(data: List<MultipartBodyFormRequest>): MultipartBody {
    val builder = MultipartBody.Builder()

    try {
        builder.setType(MultipartBody.FORM)
        data.forEach {

            if (it.isFile) {
                builder.addFormDataPart(
                        it.key,
                        it.file!!.name,
//                    it.file.asRequestBody("image/png".toMediaTypeOrNull())
                        it.file.asRequestBody(it.mime.toMediaTypeOrNull())
                )
            } else {
                builder.addFormDataPart(it.key, it.value!!)
            }

        }

    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("EXTENSIONS", "createMultiPartRequestBundle: ${e.message}")
    }

    return builder.build()
}


fun download(link: String, path: String) {
    URL(link).openStream().use { input ->
        FileOutputStream(File(path)).use { output ->
            input.copyTo(output)
        }
    }
}

fun download(link: String, path: String, progress: ((Long, Long) -> Unit)? = null): Long {
    val url = URL(link)
    val connection = url.openConnection()
    connection.connect()
    val length =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) connection.contentLengthLong else
                connection.contentLength.toLong()
    url.openStream().use { input ->
        FileOutputStream(File(path)).use { output ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytesRead = input.read(buffer)
            var bytesCopied = 0L
            while (bytesRead >= 0) {
                output.write(buffer, 0, bytesRead)
                bytesCopied += bytesRead
                progress?.invoke(bytesCopied, length)
                bytesRead = input.read(buffer)
            }
            return bytesCopied
        }
    }
}

fun downloadWithoutProgress(link: String, path: String, isCompleted: () -> Unit) {
    val url = URL(link)
    val connection = url.openConnection()
    connection.connect()
    val length =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) connection.contentLengthLong else
                connection.contentLength.toLong()
    url.openStream().use { input ->
        FileOutputStream(File(path)).use { output ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytesRead = input.read(buffer)
            var bytesCopied = 0L
            while (bytesRead >= 0) {
                output.write(buffer, 0, bytesRead)
                bytesCopied += bytesRead
//                progress?.invoke(bytesCopied, length)

                Log.e(TAG, "download: $bytesCopied --- $length")
                bytesRead = input.read(buffer)
            }

            isCompleted.invoke()

//            return bytesCopied
        }
    }
}


fun Activity.getFile(documentUri: Uri, mimeType: String = ".jpeg"): File {
    val inputStream = contentResolver?.openInputStream(documentUri)
    var file: File

    inputStream.use { input ->

        file = File(cacheDir, System.currentTimeMillis().toString() + mimeType)

        inputStream?.copyTo(file.outputStream())

//        FileOutputStream(file).use { output ->
//            val buffer = ByteArray(4 * 1024) // or other buffer size
//            var read: Int = -1
//
//            while (input?.read(buffer).also {
//                    if (it != null) {
//                        read = it
//                    }
//                } != -1) {
//                output.write(buffer, 0, read)
//            }
//
//            output.flush()
//
//        }
    }
    return file
}


/*------------------------------------- File to Base 64 String ------------------------------*/

fun File.getStringPDF(): String {
    return ByteArrayOutputStream().use { outputStream ->
        Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
            this.inputStream().use { inputStream ->
                inputStream.copyTo(base64FilterStream)
            }
        }
        return@use outputStream.toString()
    }
}

fun File.getImageBitmap(): Bitmap {
    val bmOptions = BitmapFactory.Options()
    return BitmapFactory.decodeFile(this.absolutePath, bmOptions)
}

fun File.isFileLessThan2MB(): Boolean {
    val maxFileSize = 2 * 1024 * 1024
    val finalFileSize = this.length().toString().toInt()
    return finalFileSize <= maxFileSize
}

fun File.getFileSizeInKB(): Int {
    return this.length().toString().toInt() / (1024)
}

fun Uri.getMimeType(context: Context, requiredExtension: Boolean = true): String? {
    return when (scheme) {
        ContentResolver.SCHEME_CONTENT -> if (requiredExtension) {
            MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(context.contentResolver.getType(this))
        } else {
            context.contentResolver.getType(this)
        }

        ContentResolver.SCHEME_FILE -> MimeTypeMap.getSingleton().getExtensionFromMimeType(
                MimeTypeMap.getFileExtensionFromUrl(toString()).lowercase(Locale.US)
        )
        else -> null
    }
}


fun File.getMimeFromFile(): String {

//    val extension = this.extension
//    MimeTypeMap.getFileExtensionFromUrl(this.extension)

    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(this.extension)!!

//    return ""

}


/*------------------------------------ Get extension from URL -------------------------------*/

fun String.getExtensionFromUrl(): String {

    return if (this.isValidUrl()) {
        this.substring(this.lastIndexOf("."))
    } else {
        ""
    }
//        (extension == ".mp4" || extension == ".mkv" || extension == ".avi") {
//
//        } else

}

fun Fragment.getIntentForFile(filePath: String, mimeType: String = "application/pdf"): Intent {
//    https://stackoverflow.com/a/62045721
    val intent = Intent()

    val uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName + ".provider",
            File(filePath)
    )
    intent.action = Intent.ACTION_VIEW
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.setDataAndType(uri, mimeType)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    return intent
}