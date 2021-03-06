package com.example.redditclone.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.redditclone.R
import kotlinx.android.synthetic.main.activity_post.*

class PostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        var intentThatStartedThis = getIntent()
        var postTitle = intentThatStartedThis.getStringExtra("POST_TITLE")
        var postText = intentThatStartedThis.getStringExtra("POST_TEXT")
        var postUrl = intentThatStartedThis.getStringExtra("POST_URL")


        tvPostTitle.text = postTitle
        tvPostContents.text = postText
        Glide.with(this@PostActivity).load(postUrl).into(ivPostImage)
    }
}