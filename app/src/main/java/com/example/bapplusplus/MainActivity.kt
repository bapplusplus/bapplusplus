package com.example.bapplusplus

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val infoList: ArrayList<RestInfoTemp> = arrayListOf(
        RestInfoTemp("삼삼구 한양대점", 4.27, "육류, 고기요리", "매일 11:00 - 22:00", 37.5594683,127.0382745,
            "서울 성동구 마조로 17 1층 삼삼구","0507-1473-3394", null)
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testButton.setOnClickListener {
            Toast.makeText(this, "testing", Toast.LENGTH_SHORT).show()
        }

        btn_showmap.setOnClickListener {
            val intent = Intent(this, ShowMapActivity::class.java)
            intent.putExtra("infoArray", infoList)
            startActivity(intent)
        }

        btn_botnavi.setOnClickListener {
            val intent = Intent(this, BottomNaviActivity::class.java)
            intent.putExtra("infoList", infoList[0])
            intent.putExtra("posx", infoList[0].pos_x)
            intent.putExtra("posy", infoList[0].pos_y)
            startActivity(intent)
        }

//        btn_favlist.setOnClickListener {
//            val intent = Intent(this, FavoritesListActivity::class.java)
//            startActivity(intent)
//        }

        btn_list.setOnClickListener {
            val intent = Intent(this, RestaurantList::class.java)
            startActivity(intent)
        }
    }
}