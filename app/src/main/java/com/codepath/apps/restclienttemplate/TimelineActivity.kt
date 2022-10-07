package com.codepath.apps.restclienttemplate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException
import EndlessRecyclerViewScrollListener
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class TimelineActivity : AppCompatActivity() {

    lateinit var client: TwitterClient
    lateinit var rvTweets: RecyclerView
    lateinit var adapter: TweetsAdapter
    lateinit var swipeContainer: SwipeRefreshLayout

    val tweets = ArrayList<Tweet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client = TwitterApplication.getRestClient(this)
        swipeContainer = findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            populateHomeTimeline(false)
        }
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)

        rvTweets = findViewById(R.id.rvTweets)
        adapter = TweetsAdapter(tweets)

        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.adapter = adapter

        val linearLayoutManager = LinearLayoutManager(this)
        rvTweets.layoutManager = linearLayoutManager
        rvTweets.adapter = adapter

        var scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.i(TAG, "Populating rv with more items")
                populateHomeTimeline(true)
            }
        }
        rvTweets.addOnScrollListener(scrollListener)

        populateHomeTimeline(false)

    }

    //Method called back when the user comes from ComposeActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE){
            //get tweet object back from ComposeActivity
            val tweet = data?.getParcelableExtra("tweet") as Tweet

            //update timeline
            //modify data source of tweets
            tweets.add(0, tweet)
            //update adapter
            adapter.notifyItemInserted(0)
            //scroll adapter back to the first position so we can see the tweet the user composed
            rvTweets.smoothScrollToPosition(0)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun populateHomeTimeline(isLoadMore: Boolean) {

        if(isLoadMore) {
            client.getNextPageOfTweets(object: JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                    Log.i(TAG, "onSuccess $statusCode")
                    val jsonArray = json.jsonArray
                    try {
                        val listOfNewTweetsRetrieved = Tweet.fromJsonArray(jsonArray)
                        tweets.addAll(listOfNewTweetsRetrieved)
                        adapter.notifyDataSetChanged()
                    } catch (e: JSONException) {
                        Log.e(TAG, "JSON Exception $e")
                    }
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    response: String?,
                    throwable: Throwable?
                ) {
                    Log.i(TAG, "onFailure $statusCode")
                }
            }, adapter.getLastTweetId())
        }

        else {
            client.getHomeTimeline(object : JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                    Log.i(TAG, "onSuccess $json")
                    val jsonArray = json.jsonArray
                    try {
                        adapter.clear()
                        val listOfNewTweetsRetrieved = Tweet.fromJsonArray(jsonArray)
                        tweets.addAll(listOfNewTweetsRetrieved)
                        adapter.notifyDataSetChanged()
                        swipeContainer.setRefreshing(false);
                    } catch (e: JSONException) {
                        Log.e(TAG, "Json Exception $e")
                    }
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    response: String?,
                    throwable: Throwable?
                ) {
                    Log.i(TAG, "onFailure $statusCode")
                }

            })

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }
//Handles click on menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.compose){
//            Navigate compose screen
            val intent = Intent(this,ComposeActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val TAG = "TimelineActivity"
        val REQUEST_CODE = 10
    }
}