package com.niranjan.android.laharisahitya.Utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.niranjan.android.laharisahitya.model.User
import com.niranjan.android.laharisahitya.model.UserStatus


/**
 * Created by Depression on 19-11-2018.
 */

fun showToast(context: Context, string: String) {
    Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
}

fun convertDpToPixel(dp: Float, context: Context): Int {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return (dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

fun saveUser(user: User, context: Context) {

    SharedPrefsUtils.setStringPreference(context, AppConstants.NAME, user.name!!)
    SharedPrefsUtils.setStringPreference(context, AppConstants.DP, user.dp!!)
    SharedPrefsUtils.setStringPreference(context, AppConstants.PHONE_NUMBER, user.phone_number!!)
    SharedPrefsUtils.setStringPreference(context, AppConstants.ID, user.id!!)
    SharedPrefsUtils.setStringPreference(context, AppConstants.USER_STATUS, user.userstatus.name)
    SharedPrefsUtils.setStringPreference(context, AppConstants.EMAIL, user.email.toString())

}


fun isAdmin(context: Context): Boolean {
    Log.d("UserStatus", "Current User is : " + FirebaseAuth.getInstance().currentUser.toString())
    if (FirebaseAuth.getInstance().currentUser?.uid != null
            && SharedPrefsUtils.getStringPreference(context, AppConstants.ID) != null) {
        if (SharedPrefsUtils.getStringPreference(context, AppConstants.USER_STATUS)!!.equals(UserStatus.ADMIN.name)) {
            Log.d("UserStatus", "Current User is : Admin")
            return true
        }
    }
    return false
}

