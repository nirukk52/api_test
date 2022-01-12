package com.niranjan.android.laharisahitya.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.niranjan.android.laharisahitya.R
import com.niranjan.android.laharisahitya.Utils.AppConstants
import com.niranjan.android.laharisahitya.Utils.DateInputMask
import com.niranjan.android.laharisahitya.Utils.SharedPrefsUtils
import com.niranjan.android.laharisahitya.activity.home.HomeActivity
import com.niranjan.android.laharisahitya.model.Gender
import com.niranjan.android.laharisahitya.model.User
import kotlinx.android.synthetic.main.activity_registration.*


class RegistrationActivity : AppCompatActivity() {

  companion object {
    private const val TAG = "RegActivity"

    fun newIntent(context: Context): Intent {
      val intent = Intent(context, RegistrationActivity::class.java)
      return intent
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

  private val auth = FirebaseAuth.getInstance()

  private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
    val loggedIn = auth.currentUser != null
    if (loggedIn) {
      //  tvLogin.visibility = View.GONE
    } else {
      //  tvLogin.visibility = View.VISIBLE
    }

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_registration)


    topAppBar.setNavigationOnClickListener {
      showAlertDialog()
    }

    details_dropdown_layout.setOnClickListener {
      iv_payment_dropdown.scaleY = iv_payment_dropdown.scaleY * -1f
      if (ll_additional_details.visibility == VISIBLE) {
        ll_additional_details.visibility = GONE
        tv_additional_details.text = "Enter Additional Details"
      } else {
        ll_additional_details.visibility = VISIBLE
        tv_additional_details.text = "Hide Additional Details"
      }
    }

    DateInputMask(tvDob.editText!!);

    val items = listOf("Gender", "M", "F")
    val adapter = ArrayAdapter(this, R.layout.list_item, items)
    (tvGender.editText as? AutoCompleteTextView)?.setAdapter(adapter)

    btSubmit.setOnClickListener {
      if (validateForm()) {
        uploadForm()
      }
    }

    tvFirstName.editText?.onFocusChangeListener = focusChangeListener
    tvLastName.editText?.onFocusChangeListener = focusChangeListener
    tvGender.editText?.onFocusChangeListener = focusChangeListener
    tvDob.editText?.onFocusChangeListener = focusChangeListener
    tvPhone.editText?.onFocusChangeListener = focusChangeListener
    tvCity.editText?.onFocusChangeListener = focusChangeListener
    tvOccupation.editText?.onFocusChangeListener = focusChangeListener

  }

  private val focusChangeListener = OnFocusChangeListener { view, hasFocus ->
    if (!hasFocus) {
      if ((view as EditText).toString().isBlank()) {
        view.error = null
      } else {
        view.error = "This field is required"
      }
    }
  }


  private fun validateForm(): Boolean {

    if (tvFirstName.editText?.text.toString().isBlank()) {
      tvFirstName.error = "This field is required"
    } else {
      tvFirstName.error = null
    }
    if (tvLastName.editText?.text.toString().isBlank()) {
      tvLastName.error = "This field is required"
    } else {
      tvLastName.error = null
    }

    if (tvGender.editText?.text.toString().isBlank()) {
      tvGender.error = "This field is required"
    } else {
      tvGender.error = null
    }
    if (tvDob.editText?.text.toString().isBlank()) {
      tvDob.error = "This field is required"
    } else {
      tvDob.error = null
    }

    if (tvPhone.editText?.text.toString().isBlank()) {
      tvPhone.error = "Please enter 10 digit phone number"
    } else {
      tvPhone.error = null
    }
    if (tvCity.editText?.text.toString().isBlank()) {
      tvCity.error = "This field is required"
    } else {
      tvCity.error = null
    }
    if (tvOccupation.editText?.text.toString().isBlank()) {
      tvOccupation.error = "This field is required"
    } else {
      tvOccupation.error = null
    }

    return tvFirstName.editText?.text.toString().isNotBlank()
        && tvLastName.editText?.text.toString().isNotBlank()
        && tvGender.editText?.text.toString().isNotBlank()
        && tvDob.editText?.text.toString().isNotBlank()
        && tvPhone.editText?.text.toString().isNotBlank()
        && tvCity.editText?.text.toString().isNotBlank()
        && tvOccupation.editText?.text.toString().isNotBlank()
  }


  private fun uploadForm() {

    val retrievedUser = User()

    retrievedUser.first_name = tvFirstName.editText?.text.toString()
    retrievedUser.last_name = tvLastName.editText?.text.toString()

    when {
      tvGender.editText?.text.toString() == "M" -> {
        retrievedUser.gender = Gender.M
      }
      tvGender.editText?.text.toString() == "F" -> {
        retrievedUser.gender = Gender.F
      }
      else -> {
        retrievedUser.gender = Gender.Unknown
      }
    }
    retrievedUser.date_of_birth = tvDob.editText?.text.toString()
    retrievedUser.phone_number = tvPhone.editText?.text.toString()
    retrievedUser.city = tvCity.editText?.text.toString()
    retrievedUser.occupation = tvOccupation.editText?.text.toString()

    retrievedUser.full_address = tvFullAddress.editText?.text.toString()
    retrievedUser.education = tvEducation.editText?.text.toString()
    retrievedUser.blood_group = tvBloodGroup.editText?.text.toString()
    retrievedUser.secondary_email = tvSecEmail.editText?.text.toString()
    retrievedUser.comment = tvAddComments.editText?.text.toString()


    val userMap = hashMapOf(
        "first_name" to retrievedUser.first_name,
        "last_name" to retrievedUser.last_name,
        "gender" to retrievedUser.gender.name,
        "date_of_birth" to retrievedUser.date_of_birth,
        "phone_number" to retrievedUser.phone_number,
        "city" to retrievedUser.city,
        "occupation" to retrievedUser.occupation,
        "full_address" to retrievedUser.full_address,
        "education" to retrievedUser.education,
        "blood_group" to retrievedUser.blood_group,
        "secondary_email" to retrievedUser.secondary_email,
        "comment" to retrievedUser.comment,
        "isRegistered" to true
    )

    FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!)
        .update(userMap as Map<String, Any>)
        .addOnSuccessListener { documentReference ->
          SharedPrefsUtils.setBooleanPreference(this, AppConstants.REGISTERED, true)
          startActivity(HomeActivity.newIntent(this))
        }
        .addOnFailureListener { e ->
          showError()
        }
  }


  private fun showError() {
    Toast.makeText(this, "There was some error from our side.", Toast.LENGTH_SHORT).show()
  }


  private fun showAlertDialog() {
    MaterialAlertDialogBuilder(this)
        .setTitle(resources.getString(R.string.dialog_text))
        .setMessage(resources.getString(R.string.supporting_text))
        .setNegativeButton("Close App") { dialog, which ->
          finish()
        }
        .setPositiveButton("Continue Registration") { dialog, which ->
          dialog.dismiss()
          finish()
        }
        .show()
  }
}