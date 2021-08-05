package com.embed.pashudhan.DataModels

data class CommentsData(
    var profileImage: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var commentContent: String? = null,
    var timestamp: String? = null,
    var user_uuid: String? = null
)
