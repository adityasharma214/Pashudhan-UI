package com.embed.pashudhan.DataModels

import java.sql.Timestamp
import java.util.ArrayList

data class StoryDataModel(var comments: String? = null, var img: ArrayList<String>? = null, var likes: String? = null
                          , var user_id: String? = null)
