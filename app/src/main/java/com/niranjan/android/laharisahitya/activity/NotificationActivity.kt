package com.niranjan.android.laharisahitya.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.niranjan.android.laharisahitya.R
import com.niranjan.android.laharisahitya.Utils.AppConstants
import com.niranjan.android.laharisahitya.activity.home.HomeActivity
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {

  companion object {
    private const val TAG = "NotificationActivity"

    fun newIntent(context: Context, urlOne: String, @Nullable urlTwo: String, @Nullable urlThree: String): Intent {
      val intent = Intent(context, NotificationActivity::class.java)
      intent.putExtra(AppConstants.Extras.imageUrlOne, urlOne)
      intent.putExtra(AppConstants.Extras.imageUrlTwo, urlTwo)
      intent.putExtra(AppConstants.Extras.imageUrlThree, urlThree)
      return intent
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
    goToLogin = auth.currentUser == null
  }

  private var goToLogin = false
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_registration)

    val bundle = intent.extras

    if (bundle != null) {
      if (bundle.containsKey(AppConstants.Extras.imageUrlOne) && bundle.getString(AppConstants.Extras.imageUrlOne) != null) {
        Glide.with(ivNotificationImageOne.context).load(bundle.getString(AppConstants.Extras.imageUrlOne)).into(ivNotificationImageOne)
      }
      if (bundle.containsKey(AppConstants.Extras.imageUrlOne) && bundle.getString(AppConstants.Extras.imageUrlTwo) != null) {
        Glide.with(ivNotificationImageOne.context).load(bundle.getString(AppConstants.Extras.imageUrlTwo)).into(ivNotificationImageOne)
      }
      if (bundle.containsKey(AppConstants.Extras.imageUrlOne) && bundle.getString(AppConstants.Extras.imageUrlThree) != null) {
        Glide.with(ivNotificationImageOne.context).load(bundle.getString(AppConstants.Extras.imageUrlThree)).into(ivNotificationImageOne)
      }
    }

    topAppBar.setNavigationOnClickListener {
      if (goToLogin) {
        startActivity(SplashActivity.newIntent(this))
      } else {
        startActivity(HomeActivity.newIntent(this))
      }
    }

  }

}