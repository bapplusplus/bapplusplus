package com.example.bapplusplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testButton.setOnClickListener {
            Toast.makeText(this, "testing", Toast.LENGTH_SHORT).show()
        }

        btn_showmap.setOnClickListener {
            val intent = Intent(this, ShowMapActivity::class.java)
            startActivity(intent)
        }

        btn_botnavi.setOnClickListener {
            val intent = Intent(this, BottomNaviActivity::class.java)
            startActivity(intent)
        }
    }
}