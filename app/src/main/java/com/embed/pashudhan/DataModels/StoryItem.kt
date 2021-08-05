package com.embed.pashudhan.DataModels

data class StoryItem(
    var imageUri: String? = null,
    var likes: ArrayList<String>? = null,
    var comments: ArrayList<CommentsData>? = null,
    var timestamp: String? = null,
    var name: String? = null,
    var location: ArrayList<String>? = null,
)