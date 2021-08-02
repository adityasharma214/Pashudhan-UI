package com.embed.pashudhan.DataModels

data class StoryItem(
    var imageUri: String? = null,
    var likes: ArrayList<String>? = null,
    var comments: ArrayList<String>? = null,
    var timestamp: String? = null,
)
