package com.niranjan.android.laharisahitya.activity.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.niranjan.android.laharisahitya.R
import com.niranjan.android.laharisahitya.Utils.AppConstants
import com.niranjan.android.laharisahitya.Utils.isAdmin
import com.niranjan.android.laharisahitya.activity.home.Category


class ListActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context, category: Category): Intent {
            val intent = Intent(context, ListActivity::class.java)
            intent.putExtra(AppConstants.Extras.category, category)
            return intent
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
//        val decorView = this.getWindow().getDecorView()
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE)
        val category = intent.extras!!.getParcelable<Category>(AppConstants.Extras.category)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, ListFragment.newInstance(category!!))
                    .commitNow()
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
            if(isAdmin(this)){

            }
        } else {

        }

    }
}
