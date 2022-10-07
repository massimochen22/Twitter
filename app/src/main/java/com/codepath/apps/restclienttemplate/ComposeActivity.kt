package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {
    lateinit var etCompose:EditText
    lateinit var btnTweet:Button
    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        client = TwitterApplication.getRestClient(this)

        btnTweet.setOnClickListener {

//            Grab content od edit Text
            val tweetContent = etCompose.text.toString()
//            1. make sure he tweet isn't empty
            if (tweetContent.isEmpty()){
                Toast.makeText(this,"empty text is not allowed", Toast.LENGTH_SHORT).show()
//                look snackbar message
            }
//            2.Make sure tweet is under character count
            else{
                if (tweetContent.length>140){
                    Toast.makeText(this,"Text is too long", Toast.LENGTH_SHORT).show()
                }
                else{
                    client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {
                        override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {

                            Log.i(TAG, "Tweet successfully published!")
                            val tweet = Tweet.fromJson(json.jsonObject)

                            val intent = Intent()
                            intent.putExtra("tweet", tweet)
                            setResult(RESULT_OK, intent)
                            finish()
                        }

                        override fun onFailure(
                            statusCode: Int,
                            headers: Headers?,
                            response: String?,
                            throwable: Throwable?
                        ) {
                            Log.i(TAG, "Failure tweeting!", throwable)
                        }



                    })
                }
            }

        }
    }
    companion object {
        val TAG = "ComposeActivity"
    }
}