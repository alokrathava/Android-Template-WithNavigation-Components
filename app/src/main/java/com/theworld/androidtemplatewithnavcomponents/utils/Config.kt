package com.theworld.androidtemplatewithnavcomponents.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.theworld.androidtemplatewithnavcomponents.R
import com.theworld.androidtemplatewithnavcomponents.ui.auth.LoginFragment
import com.theworld.androidtemplatewithnavcomponents.utils.Resource.Failure
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

const val lastSegment = "api/"
//const val baseUrl = "http://192.168.0.163:8000/"

const val baseUrl = "http://192.168.0.140:8000/"
const val imageUrl = baseUrl + "storage/"


/*------------------------------------- Display Snackbar ------------------------------*/

fun View.snackbar(message: String?) {
    val snackbar = Snackbar.make(this, message ?: "", Snackbar.LENGTH_LONG)
    snackbar.show()
}

/*------------------------------------- Display Toast ------------------------------*/

fun Context.toast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun Context.clearAuthData() {
    val sharedPrefManager = SharedPrefManager(this)
    sharedPrefManager.clear()
}


/*------------------------------------- Handle API Errors ------------------------------*/


fun <T> Fragment.handleApiError(resource: Resource<T>) {

    if (resource is Failure) {

        val isNetworkError = resource.isNetworkError

        if (isNetworkError == true) {
            requireView().snackbar(resource.errorBody.toString())

            Log.d("Handle API ERROR", "handleApiError: ${resource.errorBody}")
        } else {
            val translation = translateCode(resource.errorCode, this)
            Log.d("Handle API ERROR", "handleApiError: $translation")
            translation?.let {
                requireView().snackbar(it)
            }
        }

    }
}


/*------------------------------------- Translate Error Code ------------------------------*/



fun translateCode(code: Int?, fragment: Fragment): String? {

    return when (code) {
        400 -> {

            when (fragment) {

                /*is SellerDashboardFragment,
                is DeliveryBoyDashboardFragment,
                is CartFragment -> null


                is SellerProductFragment -> "Please checkout the cart first!!"
                is ChangePasswordFragment -> "Old Password Does not Match"*/


                else -> {
                    "Invalid Parameters"
                }
            }
        }

        404 -> "Not Found"
        409 -> "Account is already Registered"
        401 -> {

            when (fragment) {


                is LoginFragment -> "Invalid Email or Password"

//                is ChangePasswordFragment -> "Invalid Password"

                else -> {
                    fragment.clearAuthData()
                    when (fragment.getRole()) {
                        /*RoleEnum.BUYER.name -> fragment.findNavController().redirectToDestination(
                            popUpTo = R.id.buyer_graph_xml,
                            destination = R.id.buyerProfile
                        )
                        RoleEnum.SELLER.name -> fragment.findNavController().redirectToDestination(
                            popUpTo = R.id.seller_graph_xml,
                            destination = R.id.sellerProfile
                        )*/
                    }
                    null
                }
            }
        }

        403 -> {

            when (fragment) {


//                is BuyerSendRequestDialogFragment -> "Already have pending request"
//                is BuyerRequestCategoryDialogFragment -> "Already have pending request"
//                is ChangePasswordFragment -> "Old Password Does not Match"


                else -> {
                    "Account Approval Pending"
                }
            }
        }
        422 -> {

            when (fragment) {

//                is SellerRegisterFragment -> "Email or Contact no. already taken"
//                is BuyerRegisterFragment -> "Email or Contact no. already taken"

                else -> "Invalid Parameters"

            }
        }

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

fun Fragment.clearAuthData() {
    val sharedPrefManager = SharedPrefManager(requireContext())
    sharedPrefManager.clear()
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

fun Context.getRole(): String {
    val sharedPrefManager = SharedPrefManager(this)
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
    this.editText?.text.toString().uppercase(Locale.getDefault()).trim()


fun EditText.normalText() = this.text.toString().trim()
fun EditText.upperCaseText() =
    this.text.toString().uppercase(Locale.getDefault()).trim()


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


inline fun Fragment.confirmDialog(
    title: String = "Confirmation",
    message: String = "Do you really want to delete?",
    crossinline invoke: () -> Unit
) {
    val dialog = AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton("No", null)
        .setPositiveButton("Yes") { dialog, _ ->

            invoke.invoke()

            dialog.dismiss()
        }
        .create()

    dialog.show()
}

