package com.embed.pashudhan.Adapters

import android.net.Uri
import com.bumptech.glide.Glide
import com.embed.pashudhan.R
import jp.shts.android.storiesprogressview.StoriesProgressView

class StoryListener(
    changeStory: ((Int) -> Unit),
    storyPosition: Int,
    counter: Int,
    holder: StoryPagerAdapter.StoryPagerViewHolder,
    imageList: ArrayList<Uri>
) : StoriesProgressView.StoriesListener {

    private var mHolder = holder
    private var mContext = holder.itemView.context
    private var mCounter = counter
    private var mImageList = imageList
    private var mChangeStory = changeStory
    private var mStoryPosition = storyPosition

    override fun onNext() {
        Glide.with(mContext).load(mImageList[++mCounter]).placeholder(R.drawable.download)
            .fitCenter()
            .into(mHolder.singleStoryImageView)
    }

    override fun onPrev() {
        if (mCounter > 0) {
            Glide.with(mContext).load(mImageList[--mCounter]).placeholder(R.drawable.download)
                .fitCenter()
                .into(mHolder.singleStoryImageView)
        }
    }

    override fun onComplete() {
        mChangeStory(+1)
    }
}