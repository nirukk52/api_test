/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.niranjan.android.laharisahitya.activity.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.niranjan.android.laharisahitya.R
import com.niranjan.android.laharisahitya.Utils.AppConstants
import com.niranjan.android.laharisahitya.Utils.SharedPrefsUtils
import com.niranjan.android.laharisahitya.Utils.showToast
import com.niranjan.android.laharisahitya.activity.RegistrationActivity
import com.niranjan.android.laharisahitya.activity.home.HomeActivity
import com.niranjan.android.laharisahitya.model.User
import com.niranjan.android.laharisahitya.model.UserStatus


class LoginActivity : AppCompatActivity() {

  companion object {
    private const val TAG = "MainActivity"
    private const val RC_SIGN_IN = Activity.RESULT_FIRST_USER

    private enum class Database { Firestore, RealtimeDatabase }

    private var database = Database.Firestore

    fun newIntent(context: Context): Intent {
      val intent = Intent(context, LoginActivity::class.java)
      return intent
    }

  }

  private val auth = FirebaseAuth.getInstance()

  private lateinit var vRoot: View


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_main)

    vRoot = findViewById(R.id.root)
//        initViews()
    val window = this.window
    window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    snack("Login with google.")
    startActivityForResult(
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
            .build(),
        RC_SIGN_IN)
  }

  override fun onStart() {
    super.onStart()
    auth.addAuthStateListener(authStateListener)
  }

  override fun onStop() {
    super.onStop()
    auth.removeAuthStateListener(authStateListener)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == RC_SIGN_IN) {
      // Successfully signed in
      if (resultCode == Activity.RESULT_OK) {
        snack("Signed in")
      } else {
        // Sign in failed
        val response = IdpResponse.fromResultIntent(data) ?: return

        if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
          snack("No network")
          showError()
          return
        }

        snack("Unknown error with sign in")
        showError()
        Log.e(TAG, "Sign-in error: ", response.error)
      }
    }
  }


  private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
    val loggedIn = auth.currentUser != null
    if (loggedIn) {
      val user = com.niranjan.android.laharisahitya.model.User()
      user.name = auth.currentUser?.displayName
      user.id = auth.currentUser?.uid
      user.dp = auth.currentUser?.photoUrl.toString()
      user.phone_number = auth.currentUser?.phoneNumber.toString()
      user.userstatus = UserStatus.USER
      user.email = auth.currentUser?.email

      val docRef = FirebaseFirestore.getInstance()
          .collection("users").document(user.id!!)
      docRef.get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
          val document = task.result
          if (document!!.exists()) {
            val retrievedUser = document.toObject(User::class.java)
            saveUser(retrievedUser!!)
          } else {
            FirebaseFirestore.getInstance().collection("users").document(user.id!!)
                .set(user)
                .addOnSuccessListener { documentReference ->
                  saveUser(user)
                }
                .addOnFailureListener { e ->
                  showError()
                }
          }
        } else {
          showError()
        }
      }
    } else {
      //showError()
    }

  }

  private fun snack(message: String) {
    Snackbar.make(vRoot, message, Snackbar.LENGTH_SHORT).show()
  }

  private fun saveUser(user: User) {

    SharedPrefsUtils.setStringPreference(this@LoginActivity, AppConstants.NAME, user.name!!)
    SharedPrefsUtils.setStringPreference(this@LoginActivity, AppConstants.DP, user.dp!!)
    SharedPrefsUtils.setStringPreference(this@LoginActivity, AppConstants.PHONE_NUMBER, user.phone_number!!)
    SharedPrefsUtils.setStringPreference(this@LoginActivity, AppConstants.ID, user.id!!)
    SharedPrefsUtils.setStringPreference(this@LoginActivity, AppConstants.USER_STATUS, user.userstatus.name)
    SharedPrefsUtils.setStringPreference(this@LoginActivity, AppConstants.EMAIL, user.email.toString())
    SharedPrefsUtils.setBooleanPreference(this@LoginActivity, AppConstants.REGISTERED, user.isRegistered)

    showToast(applicationContext, "Login Successful")
    if (user.isRegistered) {
      startActivity(HomeActivity.newIntent(this))
    } else {
      startActivity(RegistrationActivity.newIntent(this))
    }

  }

  private fun showError() {
    showToast(applicationContext, "Login Unsuccessful")
    finish()

  }

}
