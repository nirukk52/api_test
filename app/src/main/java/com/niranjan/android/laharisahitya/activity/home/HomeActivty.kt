package com.niranjan.android.laharisahitya.activity.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.niranjan.android.laharisahitya.R
import com.niranjan.android.laharisahitya.Utils.*
import com.niranjan.android.laharisahitya.activity.login.LoginActivity
import com.niranjan.android.laharisahitya.model.*
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*


/**
 * Created by Depression on 15-11-2018.
 */
class HomeActivity : AppCompatActivity() {


    val TAG = "MainActivity"

    companion object {

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            return intent
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        StatusBarUtil.setTranslucent(this@SplashActivity, 0)
//        StatusBarUtil.setLightMode(this@SplashActivity)

        if (FirebaseAuth.getInstance().currentUser?.uid == null
                && SharedPrefsUtils.getStringPreference(this, AppConstants.ID) == null) {
            tvLogin.visibility = View.VISIBLE
        }

        val mainList: ArrayList<Category> = arrayListOf()
        mainList.add(Category(getString(R.string.prayer), "", PostType.PRARTHANA, PostType.PRARTHANA, false))
        mainList.add(Category(getString(R.string.pothi), "", PostType.POTHI, PostType.POTHI, false))
        mainList.add(Category(getString(R.string.chaliisa), "", PostType.CHALIISA, PostType.CHALIISA, false))
        mainList.add(Category(getString(R.string.aarti), "", PostType.AARTI, PostType.AARTI, false))
        mainList.add(Category(getString(R.string.songs), "", PostType.SONGS, PostType.SONGS, false))
        mainList.add(Category(getString(R.string.bhajan), "", PostType.BHAJANS, PostType.BHAJANS, false))
        mainList.add(Category(getString(R.string.photos), "", PostType.PHOTOS, PostType.PHOTOS, false))
        mainList.add(Category(getString(R.string.recording), "", PostType.RECORDING, PostType.RECORDING, false))

        initAdapter(mainList)
        updateCache()
        initListeners()
    }


    private fun initListeners() {
        tvLogin.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser?.uid == null
                    && SharedPrefsUtils.getStringPreference(this, AppConstants.ID) == null) {
                startActivity(LoginActivity.newIntent(this))
            }
        }
//        tvAddFolder.setOnClickListener {
//            if (isAdmin(this)) {
//                UploadFolderFragment.newInstance(Category("", "", )).show(ft, category.title)
//            } else {
//                startActivity(LoginActivity.newIntent(context!!))
//            }
//        }

        cvWebsite.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://santlaharinath.org/"))
            startActivity(browserIntent)
        }
    }

    private fun initAdapter(mainList: ArrayList<Category>) {
        val layoutManager = GridLayoutManager(applicationContext, 2)
        rvMainList.layoutManager = layoutManager

        rvMainList.addItemDecoration(GridSpacingItemDecoration(2, convertDpToPixel(24.0f, this@HomeActivity), true))
        val adapter = CategoryAdapter(applicationContext, mainList)
        rvMainList.adapter = adapter

    }


    private fun updateCache() {

        if (FirebaseAuth.getInstance().currentUser?.uid != null) {
            val docRef = FirebaseFirestore.getInstance()
                    .collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document!!.exists()) {
                        val user = document.toObject(User::class.java)
                        saveUser(user!!, this)
                    }
                } else {

                }
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
            tvLogin.visibility = View.GONE
        } else {
            tvLogin.visibility = View.VISIBLE
        }

    }
}

@Parcelize
data class Category(
        var title: String = "",
        var image: String = "",
        var type: PostType = PostType.BHAJANS,
        var parentType: PostType = PostType.BHAJANS,
        var isSubCategory: Boolean = false,
        var post: Post = Post()

) : Parcelable