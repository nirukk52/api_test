package com.niranjan.android.laharisahitya.activity.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.niranjan.android.laharisahitya.R
import com.niranjan.android.laharisahitya.Utils.AppConstants
import com.niranjan.android.laharisahitya.Utils.isAdmin
import com.niranjan.android.laharisahitya.activity.home.Category

/**
 * Created by Depression on 22-11-2018.
 */
class DetailsActivity  : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context, category: Category, postId : String): Intent {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(AppConstants.Extras.category, category)
            intent.putExtra(AppConstants.Extras.postId, postId)
            return intent
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val category = intent.extras?.getParcelable<Category>(AppConstants.Extras.category)
        val postId = intent.extras?.getString(AppConstants.Extras.postId)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, TextDetailFragment.newInstance(category!!, postId!!))
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