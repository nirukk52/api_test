package com.niranjan.android.laharisahitya.activity.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.niranjan.android.laharisahitya.R
import com.niranjan.android.laharisahitya.activity.details.DetailsActivity
import com.niranjan.android.laharisahitya.activity.home.Category
import com.niranjan.android.laharisahitya.activity.login.LoginActivity
import com.niranjan.android.laharisahitya.activity.upload.UploadActivity
import com.niranjan.android.laharisahitya.activity.upload.UploadPostViewModel
import com.niranjan.android.laharisahitya.model.Post
import com.niranjan.android.laharisahitya.model.UserStatus
import io.reactivex.disposables.Disposable
import com.google.common.eventbus.EventBus
import com.niranjan.android.laharisahitya.Utils.*
import com.niranjan.android.laharisahitya.activity.upload.UploadFolderFragment
import com.niranjan.android.laharisahitya.activity.upload.UploadPostFragment
import com.niranjan.android.laharisahitya.model.Folder
import com.niranjan.android.laharisahitya.model.PostType
import kotlinx.android.synthetic.main.activity_post.*
import org.koin.android.ext.android.get


/**
 * Created by Depression on 20-11-2018.
 */
class ListFragment : Fragment() {

    private var category by FragmentArgumentDelegate<Category>()

    companion object {
        fun newInstance(category: Category) = ListFragment().apply {
            this.category = category
        }
    }

    lateinit var adapter: PostListAdapter
    val firestore = FirebaseFirestore.getInstance()
    private lateinit var postViewModel: UploadPostViewModel

    lateinit var rvPostLists: RecyclerView
    lateinit var btUploadFile: Button
    lateinit var btUploadFolder: Button
    lateinit var listProgressBar: ProgressBar
    lateinit var containerFragList: FrameLayout

    private var subscribe: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_list, container, false)


        rvPostLists = view.findViewById(R.id.rvPostsList) as RecyclerView
        btUploadFile = view.findViewById(R.id.btUploadFile) as Button
        btUploadFolder = view.findViewById(R.id.btUploadFolder) as Button
        listProgressBar = view.findViewById(R.id.list_progressBar) as ProgressBar
        containerFragList = view.findViewById(R.id.container_frag_list) as FrameLayout

        val toolbarUp = view.findViewById<Toolbar>(R.id.toolbarUp)
        toolbarUp.title = category.title


        initAdapter()

        postViewModel = activity?.run {
            ViewModelProviders.of(this)[UploadPostViewModel::class.java]
        } ?: throw Exception("Invalid Activity")


        if (FirebaseAuth.getInstance().currentUser?.uid != null
                && SharedPrefsUtils.getStringPreference(context!!, AppConstants.ID) != null) {
            if (SharedPrefsUtils.getStringPreference(context!!, AppConstants.USER_STATUS)!!.equals(UserStatus.ADMIN.name)) {
                btUploadFile.visibility = View.VISIBLE
                if (!category.type.equals(PostType.FOLDER)) {
                    //btUploadFolder.visibility = View.VISIBLE
                }
            }
        }

        btUploadFile.setOnClickListener {
            if (isAdmin(context!!)) {
                val ft = activity?.supportFragmentManager!!.beginTransaction()
                val uploadPostFragment = UploadPostFragment.newInstance(category)
                if (fragmentManager?.findFragmentByTag(UploadPostFragment.TAG) != null) {
                    ft.remove(fragmentManager?.findFragmentByTag(UploadPostFragment.TAG)!!)
                }
                ft.addToBackStack(null)
                ft.replace(containerFragList.id, uploadPostFragment, UploadPostFragment.TAG)
                ft.commit()
            } else {
                startActivity(LoginActivity.newIntent(context!!))
            }
        }


        lateinit var uploadFolderFragment: UploadFolderFragment
        btUploadFolder.setOnClickListener {
            if (isAdmin(context!!)) {
                uploadFolderFragment = UploadFolderFragment.newInstance(category)
                val ft = activity?.supportFragmentManager!!.beginTransaction()
                if (fragmentManager?.findFragmentByTag(UploadFolderFragment.TAG) != null) {
                    ft.remove(fragmentManager?.findFragmentByTag(UploadFolderFragment.TAG)!!)
                }
                ft.addToBackStack(null)
                uploadFolderFragment.show(ft, category.title)

            } else {
                startActivity(LoginActivity.newIntent(context!!))
            }
        }

        postViewModel.folderToAdd.observe(this, androidx.lifecycle.Observer { Post ->
            initAdapter()
            uploadFolderFragment.dismiss()
        })

        postViewModel.postToAdd.observe(this, androidx.lifecycle.Observer { Post ->
            adapter.retry()
            showToast(context!!, "Upload is successful!")
            if (fragmentManager?.findFragmentByTag(UploadPostFragment.TAG) != null) {
                activity?.supportFragmentManager?.beginTransaction()
                        ?.remove(fragmentManager?.findFragmentByTag(UploadPostFragment.TAG)!!)?.commit()
            }
        })

        postViewModel.subPostToAdd.observe(this, androidx.lifecycle.Observer { Post ->
            adapter.retry()
            showToast(context!!, "Upload is successful!")
            if (fragmentManager?.findFragmentByTag(UploadPostFragment.TAG) != null) {
                activity?.supportFragmentManager?.beginTransaction()
                        ?.remove(fragmentManager?.findFragmentByTag(UploadPostFragment.TAG)!!)?.commit()
            }
        })

        return view
    }


    private fun initAdapter() {
        val firstQuery =
                if (category.isSubCategory) {
                    if (isAdmin(context!!)) {
                        firestore.collection(category.parentType.toString())
                                .document(category.post.id).collection(category.title)
                                .whereEqualTo("postType", category.parentType.name)
                    } else {
                        firestore.collection(category.parentType.toString())
                                .document(category.post.id).collection(category.title)
                                .whereEqualTo("postType", category.parentType.name)
                    }
                } else {
                    if (isAdmin(context!!)) {
                        firestore.collection(category.type.toString())
                                .orderBy("folder", Query.Direction.DESCENDING)
                                .orderBy("title", Query.Direction.ASCENDING)
                    } else {
                        firestore.collection(category.type.toString())
                                .orderBy("title", Query.Direction.ASCENDING)
                                .orderBy("folder", Query.Direction.DESCENDING)
                                .whereEqualTo("postStatus", UserStatus.ADMIN.name)
                    }
                }

        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build()

        val options: FirestorePagingOptions<Post> = FirestorePagingOptions.Builder<Post>()
                .setLifecycleOwner(this as LifecycleOwner)
                .setQuery(firstQuery, config, Post::class.java)
                .build()

        adapter = PostListAdapter(options)


        rvPostLists.layoutManager = LinearLayoutManager(context)
        rvPostLists.adapter = adapter

        subscribe = adapter.getPositionClicks()
                .subscribe {
                    if (it) {
                        listProgressBar.visibility = View.VISIBLE
                    } else {
                        listProgressBar.visibility = View.GONE
                    }
                }

        subscribe = adapter.getPostClick()
                .subscribe {

                    if (it.isFolder) {
                        val category = Category(it.title, "", PostType.FOLDER, this.category.type, true, it)

                        val ft = activity?.supportFragmentManager!!.beginTransaction()
                        val uploadPostFragment = newInstance(category)
                        if (fragmentManager?.findFragmentByTag(UploadPostFragment.TAG) != null) {
                            ft.remove(fragmentManager?.findFragmentByTag(UploadPostFragment.TAG)!!)
                        }
                        ft.addToBackStack(null)
                        ft.replace(containerFragList.id, uploadPostFragment, category.title)
                        ft.commit()
                    } else {
//                        val category = if(it.postType.equals(PostType.FOLDER)){
//                            Category(it.title, "", this.category.parentType, this.category.parentType, true, it)
//                        }else{
//                            Category(it.title, "", this.category.type, this.category.type, false, it)
//                        }
                        val category = Category(it.title, ""
                                , this.category.parentType, this.category.parentType, false, it)

                        startActivity(DetailsActivity.newIntent(context!!, category, it.id))
                    }
                }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
        auth.addAuthStateListener(authStateListener)

    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
        auth.removeAuthStateListener(authStateListener)

    }

    override fun onDestroy() {
        super.onDestroy()
        subscribe?.dispose()
    }

    private val auth = FirebaseAuth.getInstance()
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val loggedIn = auth.currentUser != null
        if (loggedIn) {
            if (isAdmin(context!!)) {

            }
        } else {

        }

    }

}