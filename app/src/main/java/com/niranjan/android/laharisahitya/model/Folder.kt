package com.niranjan.android.laharisahitya.model

import java.util.*

data class Folder(
        var title: String = "",
        var id: String = "",
        var description: String = "",
        var orderNumber: Int = 100,
        var createdBy: String = "UNKNOWN",
        var createdAt: Date? = Calendar.getInstance().time,
        var folderStatus: PostStatus = PostStatus.PENDING,
        var mediaType: MediaType = MediaType.TEXT,
        var folderType: PostType = PostType.BHAJANS,
        var postType: PostType = PostType.BHAJANS,
        var isSubFolder: Boolean = true
)