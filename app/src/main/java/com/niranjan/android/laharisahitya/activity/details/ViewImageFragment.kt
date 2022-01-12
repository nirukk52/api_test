package com.niranjan.android.laharisahitya.activity.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.view.JcPlayerView
import com.google.firebase.firestore.FirebaseFirestore
import com.niranjan.android.laharisahitya.R
import com.niranjan.android.laharisahitya.Utils.FragmentArgumentDelegate
import com.niranjan.android.laharisahitya.activity.home.Category
import com.niranjan.android.laharisahitya.model.MediaType

/**
 * Created by Depression on 22-11-2018.
 */
class ViewImageFragment  : Fragment() {

    private var imageUrl by FragmentArgumentDelegate<String>()

    companion object {
        fun newInstance(imageUrl: String) = ViewImageFragment().apply {
            this.imageUrl = imageUrl
        }
    }


    lateinit var ivImage: ImageView
    lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_imageview, container, false)

        ivImage = view.findViewById(R.id.imageView) as ImageView
        progressBar = view.findViewById(R.id.progressBar) as ProgressBar



        Glide.with(ivImage.context).load(imageUrl).into(ivImage)

        return view
    }

}