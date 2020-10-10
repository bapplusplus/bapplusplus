package com.example.bapplusplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_favorites_list.*

class FavoritesListActivity : AppCompatActivity() {
    val infoList: ArrayList<InfoTemp> = arrayListOf(
        InfoTemp("삼삼구 한양대점", 4.27, "육류, 고기요리", "매일 11:00 - 22:00", 37.5594683,127.0382745,
            "0507-1473-3394","서울 성동구 마조로 17 1층 삼삼구", true),
        InfoTemp("Beta", 4.20, "육류, 고기요리", "매일 11:00 - 22:00", 37.5594700,127.0382752,
            "0507-1473-3394","서울 성동구 마조로 17 1층 삼삼구", false),
        InfoTemp("다시올치킨", 4.1, "치킨, 닭강정", "매일 07:00 - 22:00\n일요일 휴무", 37.5604271,127.0445195,
            "02-2293-8979","서울 성동구 사근동8길 8", false)

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites_list)

        val adapter = FavListAdapter(this, infoList)
        fav_recycler.adapter = adapter


        val fav_toolbar = findViewById(R.id.fav_toolbar) as Toolbar
        setSupportActionBar(fav_toolbar)

        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
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
                Toast.makeText(this, "Menu Test", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ShowMapActivity::class.java)
                startActivity(intent)
//                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.fav_toolbar_menu_file, menu)
        return true
    }
}