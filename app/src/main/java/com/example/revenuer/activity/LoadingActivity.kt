package com.example.revenuer.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.revenuer.R

class LoadingActivity : AppCompatActivity() {
//    private val actionBar: ActionBar? = supportActionBar
    override fun onCreate(savedInstanceState: Bundle?) {
//        actionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val handler = Handler(Looper.myLooper()!!)

        handler.postDelayed({
            val it = Intent(LoadingActivity@this, LoginActivity::class.java);
            startActivity(it);
            finish();
        }, 3000L)
    }
}