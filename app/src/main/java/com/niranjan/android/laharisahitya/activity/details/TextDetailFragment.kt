package com.niranjan.android.laharisahitya.activity.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.view.JcPlayerView
import com.google.firebase.firestore.FirebaseFirestore
import com.niranjan.android.laharisahitya.R
import com.niranjan.android.laharisahitya.Utils.FragmentArgumentDelegate
import com.niranjan.android.laharisahitya.Utils.showToast
import com.niranjan.android.laharisahitya.activity.home.Category
import com.niranjan.android.laharisahitya.model.MediaType
import com.niranjan.android.laharisahitya.model.Post
import android.util.TypedValue
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.niranjan.android.laharisahitya.Utils.isAdmin


/**
 * Created by Depression on 22-11-2018.
 */
class TextDetailFragment : Fragment() {

    private var category by FragmentArgumentDelegate<Category>()
    private var postId by FragmentArgumentDelegate<String>()
    private lateinit var detailViewModel: DetailViewModel

    private val TAG = "TextDetailFragment"

    companion object {
        fun newInstance(category: Category, postId: String) = TextDetailFragment().apply {
            this.category = category
            this.postId = postId
        }
    }

    val firestore = FirebaseFirestore.getInstance()

    lateinit var rvPost: RecyclerView
    lateinit var tvTitle: TextView
    lateinit var tvBody: TextView
    lateinit var tvDate: TextView
    lateinit var tvDelete: TextView
    lateinit var ivImage: ImageView
    lateinit var btSmall: Button
    lateinit var btMed: Button
    lateinit var btLarge: Button
    lateinit var llTextSize: LinearLayout
    lateinit var progressBar: ProgressBar
    lateinit var jcPlayerView: JcPlayerView
    lateinit var mPost: Post

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_detail, container, false)

        tvTitle = view.findViewById(R.id.tvTitle) as TextView
        tvBody = view.findViewById(R.id.tvBody) as TextView
        tvDate = view.findViewById(R.id.tvDate) as TextView
        tvDelete = view.findViewById(R.id.tvDelete) as TextView
        ivImage = view.findViewById(R.id.ivImage) as ImageView
        btSmall = view.findViewById(R.id.btSmall) as Button
        btMed = view.findViewById(R.id.btMed) as Button
        btLarge = view.findViewById(R.id.btLarge) as Button
        llTextSize = view.findViewById(R.id.llTextSize) as LinearLayout
        progressBar = view.findViewById(R.id.progressBar) as ProgressBar
        jcPlayerView = view.findViewById(R.id.jcplayer) as JcPlayerView

        detailViewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)

//        detailViewModel.getPostById(category, postId)
        progressBar.visibility = View.GONE

        displayPost(category.post)

//        detailViewModel.postToAdd.observe(this, androidx.lifecycle.Observer { post ->
//            Log.d("DetailPost", post.toString())
//            displayPost(post)
//        })

        tvDelete.setOnClickListener {

            AlertDialog.Builder(requireContext())
                    .setTitle("  Delete  " + mPost.title + " ? ")
                    .setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton(android.R.string.yes) { dialog, which ->
                        FirebaseFirestore.getInstance().collection(mPost.postType.name.toLowerCase()).document(postId)
                                .delete()
                                .addOnSuccessListener {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                                    showToast(requireContext(), "Successfully Deleted!")
                                    activity?.finish()

                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error deleting document", e)
                                    showToast(requireContext(), "Something went wrong!")
                                }
                    }
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()

        }

        btSmall.setOnClickListener { tvBody.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f) }
        btMed.setOnClickListener { tvBody.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f) }
        btLarge.setOnClickListener { tvBody.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f) }


        return view
    }

    private fun displayPost(post: Post) {
        progressBar.visibility = View.GONE
        if (post != null) {
            this.mPost = post;
            tvTitle.text = post.title
            tvBody.text = post.body
            tvDate.text = post.createdAt.toString()

            if (isAdmin(requireContext())) {
                tvDelete.visibility = View.VISIBLE
            }
            if (post.mediaType.equals(MediaType.IMAGE)) {
                displayImage(post)
            }
            if (post.mediaType.equals(MediaType.AUDIO)) {
                displayAudio(post)
            }

            if (!tvBody.text.isNullOrEmpty()) {
                llTextSize.visibility = View.VISIBLE
            }
        }
    }

    private fun displayImage(post: Post) {
        Glide.with(ivImage.context).load(post.mediaUrl).dontTransform().placeholder(R.drawable.ic_loading).into(ivImage)

        ivImage.setOnClickListener {
            fragmentManager?.beginTransaction()
                    ?.add(R.id.detailRoot, ViewImageFragment.newInstance(post.mediaUrl))
                    ?.addToBackStack(null)
                    ?.commit()
        }
    }

    private fun displayAudio(post: Post) {
        jcPlayerView.visibility = View.VISIBLE
        val jcAudios: java.util.ArrayList<JcAudio> = arrayListOf()
        jcAudios.add(JcAudio.createFromURL(post.title, post.postUrl))

        jcPlayerView.initAnonPlaylist(jcAudios)
        jcPlayerView.createNotification()
    }
}