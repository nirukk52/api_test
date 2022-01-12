package com.niranjan.android.laharisahitya.model

/**
 * Created by Depression on 19-11-2018.
 */
data class User(
    var name: String? = "",
    var id: String? = "",
    var userstatus: UserStatus = UserStatus.USER,
    var age: Int = 0,
    var language: String = "HINDI",
    var dp: String? = "",
    var phone_number: String? = "",
    var email: String? = "",

    @field:JvmField
    var isRegistered: Boolean = false,

    var first_name: String = "",
    var last_name: String = "",
    var gender: Gender = Gender.Unknown,
    var date_of_birth: String = "",
    var occupation: String? = "",
    var city: String? = "",
    var secondary_email: String? = "",
    var full_address: String? = "",
    var blood_group: String? = "",
    var education: String? = "",
    var comment: String? = ""

)

enum class Gender {
  M, F, Unknown
}

enum class UserStatus {
  DEVELOPER, USER, ADMIN
}