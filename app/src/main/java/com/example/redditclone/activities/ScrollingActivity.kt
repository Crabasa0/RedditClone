package com.example.redditclone.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.redditclone.R
import com.example.redditclone.adaptor.PostAdaptor
import com.example.redditclone.data.AppDatabase
import com.example.redditclone.data.Post
import kotlinx.android.synthetic.main.activity_scrolling.*
import com.example.redditclone.network.Util
import kotlinx.coroutines.*

class ScrollingActivity : AppCompatActivity() {

    private lateinit var postAdaptor: PostAdaptor
    lateinit var afterSlug: String

    companion object {
        const val KEY_AFTER_SLUG = "KEY_AFTER_SLUG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)

        if (intent.hasExtra(KEY_AFTER_SLUG)){
            afterSlug = intent.getStringExtra(KEY_AFTER_SLUG)
        }

        initRecyclerViewFromDB()

        resetButton.setOnClickListener{
            GlobalScope.launch {
                AppDatabase.getInstance(this@ScrollingActivity).postDao().deleteAll()

                // BLOCKING FUNCTION
                afterSlug = Util().addPostsToDB(afterSlug, this@ScrollingActivity)
                runOnUiThread {
                    postAdaptor.removeAll()
                    initRecyclerViewFromDB()
                }
            }
        }
    }

    private fun initRecyclerViewFromDB() {
        Thread {
            val listPosts = AppDatabase.getInstance(this@ScrollingActivity).postDao().getAllPosts()

            runOnUiThread {
                postAdaptor = PostAdaptor(this, listPosts, { post : Post -> postItemClicked(post)})
                recyclerPosts.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
                recyclerPosts.adapter = postAdaptor
            }

            recyclerPosts.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (!recyclerView.canScrollVertically(1)) {

                        GlobalScope.launch {

                            // BLOCKING FUNCTION
                            afterSlug = Util().addPostsToDB(afterSlug, this@ScrollingActivity)
                            initRecyclerViewFromDB()
                        }
                    }
                }
            })

        }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun postItemClicked(post: Post) {
        val postIntent = Intent(this, PostActivity::class.java)
        postIntent.putExtra("POST_TITLE", post.postTitle)
        postIntent.putExtra("POST_TEXT", post.postText)
        postIntent.putExtra("POST_URL", post.postImageUrl)
        startActivity(postIntent)
    }

}
