package com.hrsports.cricketstreaming.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.theworld.androidtemplatewithnavcomponents.R
import com.theworld.androidtemplatewithnavcomponents.utils.Resource.Failure
import com.theworld.androidtemplatewithnavcomponents.utils.CustomValidation
import com.theworld.androidtemplatewithnavcomponents.utils.Resource
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}


/*------------------------------------- Base + Image Url ------------------------------*/

//const val baseUrl = "http://192.168.0.133/"
//const val imageUrl = baseUrl + "matchapi/"

const val baseUrl = "http://appbiz.club/"
const val imageUrl = baseUrl + "matchapi/"
//const val imageUrl = baseUrl + "HRS/"


/*------------------------------------- Display Snackbar ------------------------------*/

fun View.snackbar(message: String?) {
    val snackbar = Snackbar.make(this, message ?: "", Snackbar.LENGTH_LONG)
    snackbar.show()
}

/*------------------------------------- Display Toast ------------------------------*/

fun Context.toast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/*------------------------------------- Handle API Errors ------------------------------*/


fun <T> Fragment.handleApiError(resource: Resource<T>) {

    if (resource is Failure) {

        val isNetworkError = resource.isNetworkError

        if (isNetworkError == true) {
            requireView().snackbar(
                resource.errorBody.toString()
            )

            Log.d("Handle API ERROR", "handleApiError: ${resource.errorBody}")
        } else {

            if (resource.errorCode != 455) {

                requireView().snackbar(
                    translateCode(resource.errorCode)
                )

                return
            }

            requireView().snackbar(
                "Hello"
            )

        }

    }

}


/*------------------------------------- Translate Error Code ------------------------------*/



fun translateCode(code: Int?): String {

    return when (code) {

        400 -> "Invalid Parameters"
        404 -> "Not Found"
        409 -> "Account is already Registered"
        401 -> "Invalid Mobile No. or Password"
        403 -> "Account Approval Pending"
        422 -> "Invalid Parameters"
        423 -> "Current Password is Invalid"
        429 -> "Too many requests"
        500 -> "Server Error"
        503 -> "Service Unavailable"
        else -> "Something went wrong"

    }


}


/*------------------------------------- Get User Id ------------------------------*/


fun Fragment.getUserId(): Int {
    val sharedPrefManager = SharedPrefManager(requireContext())
    return sharedPrefManager.getInt("user_id")
}


fun Context.getUserId(): Int {
    val sharedPrefManager = SharedPrefManager(this)
    return sharedPrefManager.getInt("user_id")
}


/*------------------------------------- Get Auth Token ------------------------------*/


fun Fragment.getToken(): String {
    val sharedPrefManager = SharedPrefManager(requireContext())
    return sharedPrefManager.getString("token").toString()
}


/*------------------------------------- Get User Role ------------------------------*/


fun Fragment.getRole(): String {
    val sharedPrefManager = SharedPrefManager(requireContext())
    return sharedPrefManager.getString("role").toString()
}


/*------------------------------------- Is User Login ------------------------------*/


fun Context.isLogin(): Boolean {
    val sharedPrefManager = SharedPrefManager(this)
    return sharedPrefManager.getBoolean("is_login")
}


/*------------------------------------- IS Valid URL ------------------------------*/

fun String?.isValidUrl(): Boolean {
    val regex = ("((http|https)://)(www.)?"
            + "[a-zA-Z0-9@:%._\\+~#?&//=]"
            + "{2,256}\\.[a-z]"
            + "{2,6}\\b([-a-zA-Z0-9@:%"
            + "._\\+~#?&//=]*)")

    if (this == null) {
        return false
    }

    val p = Pattern.compile(regex)

    val m: Matcher = p.matcher(this)

    return m.matches()
}

/*------------------------------------- Listen Text Change ------------------------------*/


fun EditText.afterTextChange(afterTextChanged: (String) -> Unit) {

    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(editable: CharSequence?, p1: Int, p2: Int, p3: Int) {


        }

        override fun onTextChanged(editable: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}


/*------------------------------------- Enable TextView ------------------------------*/

fun TextView.enableTextView(enabled: Boolean) {
    this.isEnabled = enabled

    if (!enabled) {
        this.alpha = 0.5f
    } else {
        this.alpha = 1f
    }

}
/*------------------------------------- Bitmap to String ------------------------------*/

fun Bitmap.getStringImage(): String {
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 50, baos)
    val imageBytes = baos.toByteArray()
    return Base64.encodeToString(imageBytes, Base64.DEFAULT)
}

/*------------------------------------- String to Bitmap ------------------------------*/

fun String.decodeStringToImage(): Bitmap {
    val decodedString: ByteArray = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}


/*------------------------------------- Is Email Valid ------------------------------*/

fun String.isEmailValid(): Boolean {
    val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,8}$"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(this)
    return matcher.matches()
}
/*------------------------------------- Edit Text to String ------------------------------*/

fun TextInputLayout.normalText() = this.editText?.text.toString().trim()
fun TextInputLayout.upperCaseText() =
    this.editText?.text.toString().toUpperCase(Locale.getDefault()).trim()


/*------------------------------------- Edit Text Validation ------------------------------*/

fun TextInputLayout.customValidation(validation: CustomValidation): Boolean {
    val text = this.normalText()

    if (text.isEmpty()) {
        this.error = "Field can't be empty"
        return false
    }

    if (validation.isEmail) {
        this.error = if (text.isEmailValid()) null else "Invalid Email"
        return text.isEmailValid()
    }

    if (validation.isLengthRequired) {
        val length = validation.length
        this.error =
            if (text.length == length) null else "Field should have $length digits/characters"
        return text.length == length
    }


    this.error = null
    return true
}


/*---------------------------------------- Hide System UI ------------------------------*/

fun Activity.hideSystemUI() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.let {
            // Default behavior is that if navigation bar is hidden, the system will "steal" touches
            // and show it again upon user's touch. We just want the user to be able to show the
            // navigation bar by swipe, touches are handled by custom code -> change system bar behavior.
            // Alternative to deprecated SYSTEM_UI_FLAG_IMMERSIVE.
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            // make navigation bar translucent (alternative to deprecated
            // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            // - do this already in hideSystemUI() so that the bar
            // is translucent if user swipes it up
             // Finally, hide the system bars, alternative to View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            // and SYSTEM_UI_FLAG_FULLSCREEN.
            it.hide(WindowInsets.Type.systemBars())
        }
    } else {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                // Do not let system steal touches for showing the navigation bar
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Hide the nav bar and status bar
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        // Keep the app content behind the bars even if user swipes them up
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        // make navbar translucent - do this already in hideSystemUI() so that the bar
        // is translucent if user swipes it up
        @Suppress("DEPRECATION")
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    }
}


fun Activity.showSystemUI() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // show app content in fullscreen, i. e. behind the bars when they are shown (alternative to
        // deprecated View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.setDecorFitsSystemWindows(false)
        // finally, show the system bars
        window.insetsController?.show(WindowInsets.Type.systemBars())
    } else {
        // Shows the system bars by removing all the flags
        // except for the ones that make the content appear under the system bars.
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}