package com.example.bapplusplus

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bapplusplus.adapter.RouletteAdapter
import kotlinx.android.synthetic.main.activity_roulette.*
import java.util.*
import kotlin.collections.ArrayList

class Roulette : AppCompatActivity() {

    val random = Random()

    fun rand(from: Int, to: Int) : Int {
        return random.nextInt(to - from) + from
    }

    var dataSetFlag: Int = 0

    lateinit var rouletteRestListSet: ArrayList<GetRouletteItemInfo>

//    var roulettetAdapter: RouletteAdapter ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roulette)


        btn_rotate.setOnClickListener {
            roulette_view.rotate(roulette_view, 0f, rand(3600, 3960).toFloat())
        }

        // data set flag -> 현재 선택된 데이터가 없으면 0 있으면 1
        // 데이터가 없을 때 선택된 가게가 없습니다 출력
        // 아래쪽에는 선택하기 버튼
        // 데이터가 있으면 데이터 띄우기
        // 이 부분을 onCreate가 아니라 다른 부분으로 넣어야할듯
        // FavListRouletteActivity 에서 다시 넘어올 때 onCreate로 들어올지 찾아보자

        rouletteRestListSet = intent.getParcelableArrayListExtra("listSet") ?: arrayListOf<GetRouletteItemInfo>()
//        rouletteRestListSet = intent.getParcelableArrayListExtra<GetRouletteItemInfo>("listSet") as ArrayList<GetRouletteItemInfo>


        if(rouletteRestListSet.isNullOrEmpty()) {
            roulette_view.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
            btnSelect.visibility = View.VISIBLE
//            rouletteRestList.visibility = View.GONE
            btnReSelect.visibility = View.GONE
        } else {
            roulette_view.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
            btnSelect.visibility = View.GONE
            rouletteRestList.visibility = View.VISIBLE
            btnReSelect.visibility = View.VISIBLE

            val roulettetAdapter = RouletteAdapter(this, rouletteRestListSet)
            rouletteRestList.adapter = roulettetAdapter

            val lm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rouletteRestList.layoutManager = lm

            roulette_view.setRoulette(rouletteRestListSet.size)
        }

        btnSelect.setOnClickListener {
            dataSetFlag = 1

            val intent = Intent(this, FavListRouletteActivity::class.java)
            startActivity(intent)
        }
        rouletteRestList.setOnClickListener {
            Toast.makeText(this, "pressed", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onResume() {
        super.onResume()

        btnReSelect.setOnClickListener {
            val intent = Intent(this, FavListRouletteActivity::class.java)
//            intent.putParcelableArrayListExtra("listSet", rouletteRestListSet)
            startActivity(intent)

        }
    }
}