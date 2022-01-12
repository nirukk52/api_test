package com.niranjan.android.laharisahitya.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by Depression on 15-11-2018.
 */
@Parcelize
data class Post(
        var title: String = "",
        var id: String = "",
        var body: String = "",
        var stars: Int = 0,
        var createdBy: String? = "",
        var createdAt: Date? = Calendar.getInstance().time,
        var postStatus: PostStatus = PostStatus.PENDING,
        var mediaType: MediaType = MediaType.TEXT,
        var mediaUrl: String = "",
        var postType: PostType = PostType.BHAJANS,
        var postUrl: String = "",
        var lyrics: String = "",
        var isFolder: Boolean = false


) : Parcelable {
    override fun toString(): String {
        return "Post(title='$title', body='$body', stars=$stars, createdBy='$createdBy', createdAt=$createdAt, postStatus=$postStatus, mediaType=$mediaType, mediaUrl='$mediaUrl', type=$postType, postUrl='$postUrl', lyrics='$lyrics')"
    }
}

enum class MediaType constructor(url: String) {
    TEXT("TEXT"), AUDIO("AUDIO"), VIDEO("VIDEO"), PDF("PDF"), IMAGE("IMAGE"), FOLDER("FOLDER")
}

enum class PostType constructor(url: String) {

    PRARTHANA("PRARTHANA"),
    CHALIISA("CHALIISA"),
    AARTI("AARTI"),
    BHAJANS("BHAJANS"),
    SONGS("SONGS"),
    RECORDING("RECORDING"),
    PHOTOS("PHOTOS"),
    LOGIN("LOGIN"),
    POTHI("POTHI"),
    POST("POST"),
    FOLDER("FOLDER"),
    HOME("HOME");

    override fun toString(): String {
        return this.name.toLowerCase()
    }


}

//sealed class ScreenState {
//    class TEXT : ScreenState()
//    class AUDIO : ScreenState()
//    class VIDEO : ScreenState()
//    class IMAGE : ScreenState()
//    class PDF : ScreenState()
//    data class Data(val : String) : ScreenState()
//}


public enum class PostStatus {
    APPROVED, PENDING, REJECTED
}