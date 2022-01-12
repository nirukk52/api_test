package com.niranjan.android.laharisahitya.activity.list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.niranjan.android.laharisahitya.R
import com.niranjan.android.laharisahitya.model.MediaType
import com.niranjan.android.laharisahitya.model.Post
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


/**
 * Created by Depression on 21-11-2018.
 */
class PostListAdapter(options: FirestorePagingOptions<Post>)
    : FirestorePagingAdapter<Post, PostListAdapter.ViewHolder>(options) {

    val TAG = "PostListAdpater"

    private val loading = PublishSubject.create<Boolean>()
    private val postClick = PublishSubject.create<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int, post: Post) {
        viewHolder.tvTitle.text = post.title
        viewHolder.tvBody.text = post.body

        Log.d(TAG, post.mediaType.name)

        if (post.mediaType.equals(MediaType.FOLDER)) {
            viewHolder.ivPostIcon.setImageResource(R.drawable.folder)
        } else if (post.mediaType.equals(MediaType.AUDIO)) {
            viewHolder.ivPostIcon.setImageResource(R.drawable.musical_note)
        } else {
            viewHolder.ivPostIcon.setImageResource(R.drawable.text)
        }

        viewHolder.itemListPost.setOnClickListener {
            postClick.onNext(post)
        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_title) as TextView
        val tvBody: TextView = view.findViewById(R.id.tv_body) as TextView
        val ivPostIcon: ImageView = view.findViewById(R.id.ivPostIcon) as ImageView
        val itemListPost: ConstraintLayout = view.findViewById(R.id.item_list_post) as ConstraintLayout
    }

    override fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.LOADING_INITIAL -> {
                loading.onNext(true)
            }
            LoadingState.LOADING_MORE -> {
                loading.onNext(false)
            }
            LoadingState.LOADED -> {
                loading.onNext(false)
            }
            LoadingState.FINISHED -> {
                loading.onNext(false)

            }
            LoadingState.ERROR -> {
                loading.onNext(false)

            }
        }
    }

    fun getPositionClicks(): Observable<Boolean> {
        return loading
    }

    fun getPostClick(): Observable<Post> {
        return postClick
    }
}
