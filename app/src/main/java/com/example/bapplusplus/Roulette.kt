package com.example.bapplusplus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_roulette.*
import java.util.*

class Roulette : AppCompatActivity() {

    val random = Random()

    fun rand(from: Int, to: Int) : Int {
        return random.nextInt(to - from) + from
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roulette)

//        roulette_view.layoutParams = LinearLayout.LayoutParams(500, 500)

        btn_rotate.setOnClickListener {
            roulette_view.rotate(roulette_view, 0f, rand(3600, 3960).toFloat())
        }

    }
}