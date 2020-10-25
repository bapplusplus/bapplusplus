package com.example.bapplusplus

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_favorites_list.*
import kotlinx.android.synthetic.main.activity_restaurant_list.*

class RestaurantList : AppCompatActivity() {

    val restList: ArrayList<RestInfoTemp> = arrayListOf(
        RestInfoTemp("삼삼구 한양대점", 4.27, "육류, 고기요리", "매일 11:00 - 22:00", 37.559248, 127.040840,
            "서울 성동구 마조로 17 1층 삼삼구","0507-1473-3394", null),
        RestInfoTemp("알촌 한양대점", 4.49, "퓨전음식", "매일 09:00 - 21:00", 37.558542, 127.040173,
            "서울 성동구 마조로3길 6","02-2292-8885", null),
        RestInfoTemp("다시올치킨", 4.1, "치킨, 닭강정", "매일 07:00 - 22:00\n일요일 휴무", 37.5604271,127.0445195,
            "서울 성동구 사근동8길 8","02-2293-8979", null),
        RestInfoTemp("Dummy", 4.20, "육류, 고기요리", "매일 11:00 - 22:00", 37.5594700,127.0382752,
            "서울 성동구 마조로 17 1층 삼삼구", "0507-1473-3394", null),
        RestInfoTemp("Dummy2", 4.20, "육류, 고기요리", "매일 11:00 - 22:00", 37.5594700,127.0382752,
            "서울 성동구 마조로 17 1층 삼삼구", "0507-1473-3394", null),
        RestInfoTemp("Dummy3", 4.20, "육류, 고기요리", "매일 11:00 - 22:00", 37.5594700,127.0382752,
            "서울 성동구 마조로 17 1층 삼삼구", "0507-1473-3394", null),
        RestInfoTemp("Dummy4", 4.20, "육류, 고기요리", "매일 11:00 - 22:00", 37.5594700,127.0382752,
            "서울 성동구 마조로 17 1층 삼삼구", "0507-1473-3394", null),
        RestInfoTemp("Dummy5", 4.20, "육류, 고기요리", "매일 11:00 - 22:00", 37.5594700,127.0382752,
            "서울 성동구 마조로 17 1층 삼삼구", "0507-1473-3394", null),
        RestInfoTemp("Dummy6", 4.20, "육류, 고기요리", "매일 11:00 - 22:00", 37.5594700,127.0382752,
            "서울 성동구 마조로 17 1층 삼삼구", "0507-1473-3394", null),
        RestInfoTemp("Dummy7", 4.20, "육류, 고기요리", "매일 11:00 - 22:00", 37.5594700,127.0382752,
            "서울 성동구 마조로 17 1층 삼삼구", "0507-1473-3394", null)

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_list)

        val adapter = RestAdapter(this, restList)
        rst_recyclerView.adapter = adapter

        val rst_toolbar = findViewById(R.id.rst_toolbar) as Toolbar
        setSupportActionBar(rst_toolbar)

        val ab = supportActionBar!!
        //ab.setDisplayShowTitleEnabled(false)
        ab.title = "Restaurant List"
        ab.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.ftb_maps ->{
                Toast.makeText(this, "OnMap Button Clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ShowMapActivity::class.java)
                intent.putExtra("infoArray", restList)
                startActivity(intent)
//                finish()
                return true
            }
            R.id.ftb_search->{
                Toast.makeText(this, "Search Button Clicked", Toast.LENGTH_SHORT).show()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.fav_toolbar_menu_file, menu)
        return true
    }
}