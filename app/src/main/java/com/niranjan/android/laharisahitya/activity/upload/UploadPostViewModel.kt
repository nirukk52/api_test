package com.niranjan.android.laharisahitya.activity.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.niranjan.android.laharisahitya.Utils.SingleLiveEvent
import com.niranjan.android.laharisahitya.activity.home.Category
import com.niranjan.android.laharisahitya.model.Post


/**
 * Created by Depression on 16-11-2018.
 */
class UploadPostViewModel : ViewModel() {

    var postToAdd: SingleLiveEvent<Post> = SingleLiveEvent()
    var subPostToAdd: SingleLiveEvent<Post> = SingleLiveEvent()
    var folderToAdd: SingleLiveEvent<Post> = SingleLiveEvent()

    fun addPost(post: Post?): LiveData<Post> {
        if (post != null) {
            val docRef = FirebaseFirestore.getInstance().collection(post.postType.toString())
            val newPostKey: String = docRef.document().id
            post.id = newPostKey
            FirebaseFirestore.getInstance().collection(post.postType.toString()).document(newPostKey)
                    .set(post)
                    .addOnSuccessListener { documentReference ->
                        docRef.document(newPostKey).get().addOnSuccessListener { documentSnapshot ->
                            val postAdded = documentSnapshot.toObject<Post>(Post::class.java)
                            postToAdd.postValue(postAdded)
                        }
                    }
                    .addOnFailureListener { e ->

                    }
        }
        return postToAdd
    }

    fun addSubCategoryPost(category: Category, post: Post?): LiveData<Post> {
        if (post != null) {
            val docRef = FirebaseFirestore.getInstance().collection(category.parentType.toString())
                    .document(category.post.id).collection(category.post.title)
            val newPostKey: String = docRef.document().id
            post.id = newPostKey
            docRef.document(newPostKey)
                    .set(post)
                    .addOnSuccessListener { documentReference ->
                        docRef.document(newPostKey).get().addOnSuccessListener { documentSnapshot ->
                            val postAdded = documentSnapshot.toObject<Post>(Post::class.java)
                            subPostToAdd.postValue(postAdded)
                        }
                    }
                    .addOnFailureListener { e ->

                    }
        }
        return subPostToAdd
    }

    fun addFolder(post: Post?): LiveData<Post> {
        if (post != null) {
            val docRef = FirebaseFirestore.getInstance().collection(post.postType.toString())
            val newPostKey: String = docRef.document().id
            post.id = newPostKey
            docRef.document(newPostKey).set(post)
                    .addOnSuccessListener { documentReference ->
                        docRef.document(newPostKey).get().addOnSuccessListener { documentSnapshot ->
                            val docRefFolder = FirebaseFirestore.getInstance().collection(post.postType.toString())
                                    .document(newPostKey).collection(post.title)
                            val newPostKeyFolder: String = docRefFolder.document().id
                            post.id = newPostKeyFolder
                            docRefFolder.document(newPostKeyFolder)
                                    .set(post)
                                    .addOnSuccessListener { documentReference ->
                                        docRef.document(newPostKey).get().addOnSuccessListener { documentSnapshot ->
                                            val postAdded = documentSnapshot.toObject<Post>(Post::class.java)
                                            folderToAdd.postValue(postAdded)
                                        }
                                    }
                                    .addOnFailureListener { e ->

                                    }
                        }
                    }
                    .addOnFailureListener { e ->

                    }
        }
        return folderToAdd
    }

}