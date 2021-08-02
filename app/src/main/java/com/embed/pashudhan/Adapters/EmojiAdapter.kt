package com.embed.pashudhan.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.embed.pashudhan.R

class EmojiAdapter(ctx: Context, emojiList: Array<String>) :
    RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder>() {

    private var mContext = ctx
    private var mEmojiList = emojiList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val itemview =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.emoji_text_view, parent, false)
        return EmojiViewHolder(itemview)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return mEmojiList.size
    }

    class EmojiViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)

}