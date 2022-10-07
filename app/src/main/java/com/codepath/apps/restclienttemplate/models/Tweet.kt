package com.codepath.apps.restclienttemplate.models

import android.os.Parcelable
import org.json.JSONArray
import org.json.JSONObject
import com.codepath.apps.restclienttemplate.TimeFormatter
import kotlinx.parcelize.Parcelize

@Parcelize
class Tweet(var id: Long = 0,
            var body: String = "",
            var createdAt: String = "",
            var user: User? = null) : Parcelable{

    companion object{
        fun fromJson(jsonObject: JSONObject):Tweet{
            val tweet = Tweet()
            tweet.id = jsonObject.getLong("id")
            tweet.body = jsonObject.getString("text")
            tweet.createdAt = TimeFormatter.getTimeDifference(jsonObject.getString("created_at"))
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"))
            return tweet
        }

        fun fromJsonArray(jsonArray: JSONArray):List<Tweet>{
            val tweets = ArrayList<Tweet>()
            for (i in 0 until jsonArray.length()){
                tweets.add(fromJson(jsonArray.getJSONObject(i)))
            }
            return tweets
        }
//        private fun getFormattedTimestamp(rawDate: String): String {
//            val timeFormatter = TimeFormatter
//
//            return timeFormatter.getTimeDifference(rawDate)
//        }
    }
}