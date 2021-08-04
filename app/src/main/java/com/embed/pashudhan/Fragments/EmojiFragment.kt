package com.embed.pashudhan.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.embed.pashudhan.Adapters.EmojiAdapter
import com.embed.pashudhan.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EmojiFragment : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "CustomBottomSheetDialogFragment"
    }

    private lateinit var emojiRV: RecyclerView
    private lateinit var emojiList: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.emoji_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        emojiRV = view.findViewById(R.id.emoji_rv)
        emojiRV.layoutManager = GridLayoutManager(activity?.applicationContext, 4)

        emojiRV.setHasFixedSize(true)

        emojiList = resources.getStringArray(R.array.emojis)

        emojiRV.adapter = EmojiAdapter(requireContext(), emojiList)

    }
}