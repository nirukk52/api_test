package com.niranjan.android.laharisahitya.activity.upload

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.niranjan.android.laharisahitya.R
import com.niranjan.android.laharisahitya.Utils.AppConstants
import com.niranjan.android.laharisahitya.Utils.FragmentArgumentDelegate
import com.niranjan.android.laharisahitya.Utils.SharedPrefsUtils
import com.niranjan.android.laharisahitya.Utils.showToast
import com.niranjan.android.laharisahitya.activity.home.Category
import com.niranjan.android.laharisahitya.activity.login.LoginActivity
import com.niranjan.android.laharisahitya.model.*
import kotlinx.android.synthetic.main.fragment_common_post.*
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*


/**
 * Created by Depression on 15-11-2018.
 */
class UploadPostFragment : Fragment(), ActivityCompat.OnRequestPermissionsResultCallback {


    private val auth = FirebaseAuth.getInstance()

    private var filePath: Uri? = null
    private var bitmap: Bitmap? = null
    private var imageSelected: Boolean = false
    private var inputStream: InputStream? = null
    private lateinit var postViewModel: UploadPostViewModel
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    private lateinit var btUpload: Button
    private lateinit var tvUploadAudio: TextView
    private lateinit var tvUploadImage: TextView
    private lateinit var ivImage: ImageView
    private lateinit var ivAudio: ImageView
    private lateinit var ivPdf: ImageView
    private lateinit var toolbar: Toolbar
    private lateinit var etTitle: TextInputEditText
    private lateinit var etBody: TextInputEditText
    private lateinit var spinner: Spinner
    private lateinit var determinateBar: ProgressBar

    private var category by FragmentArgumentDelegate<Category>()

    companion object {
        fun newInstance(category: Category) = UploadPostFragment().apply {
            this.category = category
        }

        const val TAG = "UploadPostFragment"

        const val PICK_IMAGE = 1
        const val PICK_AUDIO = 12
        const val PICK_PDF = 122
        const val MY_PERMISSIONS_REQUEST_STORAGE = 122
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_common_post, container, false)

        btUpload = view.findViewById(R.id.btUpload) as Button
        tvUploadAudio = view.findViewById(R.id.tvUploadAudio) as TextView
        tvUploadImage = view.findViewById(R.id.tvUploadImage) as TextView
        ivImage = view.findViewById(R.id.ivImage) as ImageView
        ivAudio = view.findViewById(R.id.ivAudio) as ImageView
        ivPdf = view.findViewById(R.id.ivPdf) as ImageView
        etTitle = view.findViewById(R.id.etTitle) as TextInputEditText
        etBody = view.findViewById(R.id.etBody) as TextInputEditText
        determinateBar = view.findViewById(R.id.determinateBar) as ProgressBar
        toolbar = view.findViewById(R.id.toolbarUp) as Toolbar

        toolbar.title = "Upload" + category.title


        hideViews()


//        postViewModel = ViewModelProviders.of(this).get(UploadPostViewModel::class.java)

        postViewModel = activity?.run {
            ViewModelProviders.of(this)[UploadPostViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

//        postViewModel.postToAdd.observe(this, androidx.lifecycle.Observer { Post ->
//            Log.d("Post", Post.toString())
//            showProgressCompleted()
//        })


        btUpload.setOnClickListener {
            if (etTitle.text != null && etTitle.text!!.equals("")) {
                showToast(requireContext(), "Enter Title")
            } else {
                if (FirebaseAuth.getInstance().currentUser?.uid == null
                        && SharedPrefsUtils.getStringPreference(requireContext(), AppConstants.ID) == null) {
                    startActivity(LoginActivity.newIntent(requireContext()))
                } else if (imageSelected) {
                    if (bitmap == null) {
                        bitmap = (ivImage.drawable as? BitmapDrawable)?.bitmap
                    }
                    uploadImage()
                } else if (filePath != null) {
                    uploadAudio()
                } else {
                    uploadPost("", MediaType.TEXT)
                }
            }
        }

        ivImage.setOnClickListener {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(activity as Activity,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity as Activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_STORAGE)
            } else {
                pickImage()
            }
        }

        ivPdf.setOnClickListener {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(activity as Activity,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity as Activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_STORAGE)
            } else {
                pickPdf()
            }
        }

        ivAudio.setOnClickListener {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(activity as Activity,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity as Activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_STORAGE)
            } else {
                pickAudio()
            }
        }

        return view

    }

    private fun hideViews() {
        when (category.type) {
            PostType.PRARTHANA -> {
            }
            PostType.CHALIISA -> {
            }
            PostType.AARTI -> {
                tvUploadAudio.visibility = View.GONE
                ivAudio.visibility = View.GONE
            }
            PostType.BHAJANS -> {
                tvUploadAudio.visibility = View.GONE
                ivAudio.visibility = View.GONE
            }
            PostType.SONGS -> {
                tvUploadImage.visibility = View.GONE
                ivImage.visibility = View.GONE
            }
            PostType.RECORDING -> {
                tvUploadImage.visibility = View.GONE
                ivImage.visibility = View.GONE
            }
            PostType.PHOTOS -> {
                tvUploadAudio.visibility = View.GONE
                ivAudio.visibility = View.GONE
            }
            PostType.POTHI -> {
            }
        }
    }

    private fun uploadPost(url: String, mediaType: MediaType) {
        val post = Post()

        post.title = etTitle.text.toString()
        post.body = etBody.text.toString()
        post.postType = category.parentType
        post.mediaType = mediaType
        post.createdBy = FirebaseAuth.getInstance().currentUser?.uid
        post.postUrl = url
        post.mediaUrl = url
        SharedPrefsUtils.getStringPreference(requireContext(), AppConstants.USER_STATUS)?.let { Log.d("UserStatus", it) }
        if (SharedPrefsUtils.getStringPreference(requireContext(), AppConstants.USER_STATUS)
                !! != UserStatus.USER.name) {
            post.postStatus = PostStatus.APPROVED
        }

        if (category.isSubCategory) {
            postViewModel.addSubCategoryPost(category, post)

        } else {
            postViewModel.addPost(post)
        }

    }

    private fun uploadImage() {
        val imagesRef = storageRef.child("images")
        val fileName = UUID.randomUUID().toString()
        val fileRef = imagesRef.child(fileName)
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val data = baos.toByteArray()

        fileRef.putBytes(data)
                .addOnFailureListener { e ->
                    showProgressFailed()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                            .totalByteCount
                    Log.d("commonpost", "progress " + progress)
                    showProgress(progress.toInt(), false)
                }.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downUri = task.result
                        Timber.d("onComplete: Url: " + downUri.toString())
                        uploadPost(task.result.toString(), MediaType.IMAGE)
                    }
                }


    }

    private fun uploadAudio() {

        val imagesRef = storageRef.child("songs")
        val fileName = UUID.randomUUID().toString()
        val fileRef = imagesRef.child(fileName)

        fileRef.putFile(filePath!!)
                .addOnFailureListener { e ->
                    showProgressFailed()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                            .totalByteCount
                    Timber.d("progress " + progress)
                    showProgress(progress.toInt(), false)
                }.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downUri = task.result
                        Log.d("c", "onComplete: Url: " + downUri.toString())
                        uploadPost(task.result.toString(), MediaType.AUDIO)
                    }
                }


    }

    private fun initSpinner() {

        val spinnerList: ArrayList<String> = arrayListOf<String>()
        spinnerList.add(PostType.BHAJANS.name)
        spinnerList.add(PostType.PRARTHANA.name)

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, spinnerList)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter
    }


    private fun showProgress(progress: Int, enable: Boolean) {
        btUpload.isEnabled = enable
        btUpload.isFocusable = enable
        btUpload.isClickable = enable
        determinateBar.visibility = View.VISIBLE
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            determinateBar.setProgress(progress, true)
        } else {
            determinateBar.progress = progress
        }
    }

    private fun showProgressFailed() {
        showToast(requireContext(), "Upload Failed")
        btUpload.isEnabled = true
        btUpload.isFocusable = true
        btUpload.isClickable = true
        determinateBar.visibility = View.GONE

    }

    private fun showProgressCompleted() {
        showToast(requireContext(), "Upload Successful")
        btUpload.isEnabled = true
        btUpload.isFocusable = true
        btUpload.isClickable = true
        determinateBar.visibility = View.GONE
        requireActivity().onBackPressed()
    }

    private fun pickImage() {
        val galleryIntent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE)
    }

    private fun pickAudio() {
        val galleryIntent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_AUDIO)
    }

    private fun pickPdf() {
        val galleryIntent = Intent(Intent.ACTION_VIEW)
        galleryIntent.setType("application/pdf");
        galleryIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(galleryIntent, PICK_PDF)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val filePath = data.data
            ivImage.setImageResource(R.drawable.ic_file_upload_black)
            Glide.with(ivImage.context).load(filePath).into(ivImage)
            bitmap = (ivImage.drawable as? BitmapDrawable)?.bitmap
            imageSelected = true
//            bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, filePath
//            ivImage.setImageBitmap(bitmap)
//            inputStream = context?.contentResolver?.openInputStream(data.data)

        } else if (requestCode == PICK_AUDIO && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            tvUploadAudio.text = "Audio selected"
            ivAudio.setImageResource(R.drawable.ic_music_note_black)
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_REQUEST_STORAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            } else {
                showToast(requireContext(), "Permission Required")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        if (auth.currentUser == null) {
            requireActivity().finish()
        }
    }

}