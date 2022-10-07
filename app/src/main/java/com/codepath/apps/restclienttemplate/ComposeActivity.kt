package com.codepath.apps.restclienttemplate

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

const val CHARACTER_LIMIT = 280
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
                if (tweetContent.length> CHARACTER_LIMIT){
                    Toast.makeText(this,"Text is too long. Limit is ${CHARACTER_LIMIT.toString()} characters", Toast.LENGTH_SHORT).show()
                }
                else{
                    client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {
                        override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
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
            val tvCount = findViewById<TextView>(R.id.tvCount)
            etCompose.addTextChangedListener(object : TextWatcher {
                val tweetContent = this.toString()
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    //reset tvCount text color in any case beforehand to prevent problems
                    tvCount.setTextColor(Color.GRAY)
                }
                @SuppressLint("SetTextI18n")
                override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    if (s != null) {
                        if(s.length > CHARACTER_LIMIT) {
                            tvCount.setTextColor(Color.RED)
                        }
                        tvCount.text = "Characters left: ${CHARACTER_LIMIT - s.length}"
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                    TODO("Not yet implemented")
                }
            })
    }

    companion object {
        val TAG = "ComposeActivity"
    }
}
