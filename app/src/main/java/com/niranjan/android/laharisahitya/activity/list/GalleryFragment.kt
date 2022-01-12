package com.niranjan.android.laharisahitya.activity.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.niranjan.android.laharisahitya.R

class GalleryFragment : Fragment() {


    companion object {
        fun newInstance() = GalleryFragment().apply {
        }
    }


    lateinit var ivImage: ImageView
    lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_imageview, container, false)



        return view
    }

}