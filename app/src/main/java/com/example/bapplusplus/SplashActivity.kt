package com.example.bapplusplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        CoroutineScope(Main).launch {
            delay(2200)
            var itt = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(itt)
            this@SplashActivity.finish()
        }
    }

    override fun onBackPressed() {

    }
}