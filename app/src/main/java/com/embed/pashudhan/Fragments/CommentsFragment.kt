package com.embed.pashudhan.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.embed.pashudhan.DataModels.StoryUserDataModel
import com.embed.pashudhan.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.Serializable

class CommentsFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "CommentsFragment==>"
        fun newInstance(storiesList: ArrayList<StoryUserDataModel>, position: Int, counter: Int) =
            CommentsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("storiesList", storiesList as Serializable)
                    putInt("position", position)
                    putInt("counter", counter)
                }
            }


    }

    private lateinit var sendCommentButton: Button
    private lateinit var commentEditText: EditText
    private lateinit var mStoriesList: Serializable
    private var mCounter: Int = 0
    private var mPosition: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mStoriesList = arguments?.getSerializable("storiesList")!!
        mPosition = arguments?.getInt("position")!!
        mCounter = arguments?.getInt("counter")!!
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "$mStoriesList")
        Log.d(TAG, "$mPosition")
        Log.d(TAG, "$mCounter")
        sendCommentButton = view.findViewById(R.id.submitStoryCommentButton)
        commentEditText = view.findViewById(R.id.commentsEditText)

    }
}