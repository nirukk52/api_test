package com.niranjan.android.laharisahitya.activity

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import timber.log.Timber


/**
 * Created by Depression on 19-11-2018.
 */
class MyApplication : Application() {

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

//        val settings = FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(true)
//                .build()
//        FirebaseFirestore.getInstance().firestoreSettings = settings

        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .build()
        firestore.firestoreSettings = settings
    }

}