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
    companion object {
        val TAG = "TimelineActivity"
    }
}