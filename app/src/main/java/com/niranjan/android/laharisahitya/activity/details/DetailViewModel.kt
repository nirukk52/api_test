package com.niranjan.android.laharisahitya.activity.details

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.niranjan.android.laharisahitya.Utils.SingleLiveEvent
import com.niranjan.android.laharisahitya.activity.home.Category
import com.niranjan.android.laharisahitya.model.Post

/**
 * Created by Depression on 22-11-2018.
 */
class DetailViewModel : ViewModel() {

    var postToAdd: SingleLiveEvent<Post> = SingleLiveEvent()

    fun getPostById(category: Category, postId: String) {
        if(category.isSubCategory){
            getSubPostById(category,postId)
        }else{

            val docRef = FirebaseFirestore.getInstance()
                    .collection(category.type.toString()).document(postId)
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document!!.exists()) {
                        val post = document.toObject(Post::class.java)
                        postToAdd.postValue(post)
                    } else {
                        // TODO Something went wrong
                    }
                } else {
                    // TODO Something went wrong
                }
            }

        }
    }

    private fun getSubPostById(category: Category, postId: String){
        val docRef = FirebaseFirestore.getInstance()
                .collection(category.parentType.toString()).document(category.post.id)
                .collection(category.title).document(postId)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    val post = document.toObject(Post::class.java)
                    postToAdd.postValue(post)
                } else {
                    // TODO Something went wrong
                }
            } else {
                // TODO Something went wrong
            }
        }
    }

}