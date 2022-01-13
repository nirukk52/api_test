package com.niranjan.android.laharisahitya.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
//import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.niranjan.android.laharisahitya.R
import com.niranjan.android.laharisahitya.Utils.AppConstants
import com.niranjan.android.laharisahitya.Utils.SharedPrefsUtils
import com.niranjan.android.laharisahitya.Utils.showToast
import com.niranjan.android.laharisahitya.activity.home.HomeActivity
import com.niranjan.android.laharisahitya.activity.login.LoginActivity
import com.niranjan.android.laharisahitya.model.User
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {

  companion object {
    private const val TAG = "SplashActivity"

    fun newIntent(context: Context): Intent {
      val intent = Intent(context, SplashActivity::class.java)
      return intent
    }

  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
//    installSplashScreen()
    setContentView(R.layout.activity_splash)

//    tv_splash_text.alpha = 0f
//    tv_splash_text.animate().apply {
//      interpolator = LinearInterpolator()
//      duration = 2000
//      startDelay = 0
//      alpha(1f)
//      start()
//    }

    tv_notice.alpha = 0f
    tv_notice.animate().apply {
      interpolator = LinearInterpolator()
      duration = 1000
      startDelay = 500
      alpha(1f)
      start()
    }

    btLogin.alpha = 0f

    btLogin.setOnClickListener {
      if (FirebaseAuth.getInstance().currentUser?.uid == null
          && SharedPrefsUtils.getStringPreference(this, AppConstants.ID) == null) {
        startActivity(LoginActivity.newIntent(this))
      }
    }


  }

  override fun onStart() {
    super.onStart()
    auth.addAuthStateListener(authStateListener)
  }

  override fun onStop() {
    super.onStop()
    auth.removeAuthStateListener(authStateListener)
  }

  private val auth = FirebaseAuth.getInstance()
  private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
    val loggedIn = auth.currentUser != null
    if (loggedIn) {
      if (SharedPrefsUtils.getBooleanPreference(this, AppConstants.REGISTERED, false)) {
        startActivity(HomeActivity.newIntent(this))
      } else {
        val docRef = FirebaseFirestore.getInstance()
            .collection("users").document(auth.currentUser?.uid!!)
        docRef.get().addOnCompleteListener { task ->
          if (task.isSuccessful) {
            val document = task.result
            if (document!!.exists()) {
              val retrievedUser = document.toObject(User::class.java)
              if (retrievedUser?.isRegistered!!) {
                startActivity(HomeActivity.newIntent(this))
              } else {
                startActivity(RegistrationActivity.newIntent(this))
              }
            } else {
              animateLoginButton()
            }
          } else {
            showError()
          }
        }
      }
    } else {
      animateLoginButton()
    }

  }

  private fun animateLoginButton() {
    btLogin.animate().apply {
      interpolator = LinearInterpolator()
      duration = 0
      alpha(1f)
      startDelay = 1000
      start()
    }
  }

  private fun showError() {
    showToast(applicationContext, "Something went wrong")
  }
}