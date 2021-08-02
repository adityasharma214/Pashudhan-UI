package com.embed.pashudhan.Adapters

class StoryCounter {
    private var counter: Int = 0
    private lateinit var listener: ChangeListener


    interface ChangeListener {
        fun onChange()
    }
}