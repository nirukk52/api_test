package com.niranjan.android.laharisahitya.activity.upload

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.niranjan.android.laharisahitya.R
import com.niranjan.android.laharisahitya.Utils.AppConstants
import com.niranjan.android.laharisahitya.activity.home.Category

class UploadActivity : AppCompatActivity() {

    val sss= "ddd"

    companion object {
        fun newIntent(context: Context, category: Category): Intent {
            val intent = Intent(context, UploadActivity::class.java)
            intent.putExtra(AppConstants.Extras.category, category)
            return intent
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val category = intent.extras?.getParcelable<Category>(AppConstants.Extras.category)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, UploadPostFragment.newInstance(category!!))
                    .commitNow()
        }

    }
}
