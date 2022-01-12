package com.niranjan.android.laharisahitya.activity.upload

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.niranjan.android.laharisahitya.R
import com.niranjan.android.laharisahitya.Utils.*
import com.niranjan.android.laharisahitya.activity.home.Category
import com.niranjan.android.laharisahitya.activity.login.LoginActivity
import com.niranjan.android.laharisahitya.model.MediaType
import com.niranjan.android.laharisahitya.model.Post
import com.niranjan.android.laharisahitya.model.PostStatus
import com.niranjan.android.laharisahitya.model.UserStatus
import kotlinx.android.synthetic.main.fragment_common_post.*


class UploadFolderFragment : DialogFragment() {


    private var category by FragmentArgumentDelegate<Category>()

    private lateinit var btUpload: Button
    private lateinit var toolbar: Toolbar
    private lateinit var etTitle: TextInputEditText
    private lateinit var etBody: TextInputEditText


    private lateinit var postViewModel: UploadPostViewModel

    companion object {
        fun newInstance(category: Category) = UploadFolderFragment().apply {
            this.category = category
        }

        const val TAG = "UploadFolderFragment"

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_folder_post, container, false)

        btUpload = view.findViewById(R.id.btUpload) as Button
        etTitle = view.findViewById(R.id.etTitle) as TextInputEditText
        etBody = view.findViewById(R.id.etBody) as TextInputEditText

        postViewModel = activity?.run {
            ViewModelProviders.of(this)[UploadPostViewModel::class.java]
        } ?: throw Exception("Invalid Activity")


        btUpload.setOnClickListener {
            if (etTitle.text != null && etTitle.text!!.equals("")) {
                showToast(context!!, "Enter Title")
            } else {
                if (FirebaseAuth.getInstance().currentUser?.uid == null
                        && SharedPrefsUtils.getStringPreference(context!!, AppConstants.ID) == null) {
                    startActivity(LoginActivity.newIntent(context!!))
                } else {
                    uploadFolder()
                }
            }
        }

        return view

    }


    private fun uploadFolder() {
        val post = Post()

        post.title = etTitle.text.toString()
        post.body = etBody.text.toString()
        post.postType = category.type
        post.mediaType = MediaType.FOLDER
        post.isFolder = true
        post.createdBy = FirebaseAuth.getInstance().currentUser?.uid
        if (isAdmin(context!!)) {
            post.postStatus = PostStatus.APPROVED
        }

        postViewModel.addFolder(post)
    }

    private fun showProgress(progress: Int, enable: Boolean) {
        btUpload.isEnabled = enable
        btUpload.isFocusable = enable
        btUpload.isClickable = enable

    }

    private fun showProgressFailed() {
        showToast(context!!, "Upload Failed")
        btUpload.isEnabled = true
        btUpload.isFocusable = true
        btUpload.isClickable = true

    }

    private fun showProgressCompleted() {
        showToast(context!!, "Upload Successful")
        dismiss()
    }
}