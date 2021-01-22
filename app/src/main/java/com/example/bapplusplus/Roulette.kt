package com.example.bapplusplus

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bapplusplus.adapter.RouletteAdapter
import com.example.bapplusplus.data.FBUserInfo
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_favorites_list.*
import kotlinx.android.synthetic.main.activity_my_favorites.*
import kotlinx.android.synthetic.main.activity_roulette.*
import java.util.*
import kotlin.collections.ArrayList

class Roulette : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val random = Random()

    fun rand(from: Int, to: Int) : Int {
        return random.nextInt(to - from) + from
    }

    var dataSetFlag: Int = 0

    lateinit var rouletteRestListSet: ArrayList<GetRouletteItemInfo>
    var mDrawerLayout: DrawerLayout? = null

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


        //set Toolbar
        val rou_toolbar = roulette_toolbar as Toolbar
        setSupportActionBar(rou_toolbar)

        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.ic_dehaze_white_24dp)

        //set Drawer
        mDrawerLayout = findViewById(R.id.roulette_drawer)
        roulette_navi.setNavigationItemSelectedListener(this)

        var roulette__navi_view = roulette_navi.getHeaderView(0)
        roulette__navi_view.findViewById<TextView>(R.id.navihead_title).text = FBUserInfo.fbauth.currentUser?.displayName ?: "로그인되지 않음"
        roulette__navi_view.findViewById<TextView>(R.id.navihead_subtitle).text = FBUserInfo.fbauth.currentUser?.email ?: "Guest"
        roulette__navi_view.findViewById<ImageButton>(R.id.navihead_btn_settings).setOnClickListener {

            val itts = Intent(this, MyInfoActivity::class.java)
            startActivity(itts)
            Handler(Looper.getMainLooper()).postDelayed({
                mDrawerLayout!!.closeDrawers()
            }, 400)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                //finish()
                mDrawerLayout!!.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navimenu_one->{
                //Main
                //this.finish()
                val itt = Intent(this, MainActivity::class.java)
                startActivity(itt)
            }
            R.id.navimenu_two->{
                //Roulette
                //Do Nothing
                mDrawerLayout!!.closeDrawers()
            }
            R.id.navimenu_three->{
                //MyLike
                //Toast.makeText(this, "Menu3", Toast.LENGTH_SHORT).show()
                if(FBUserInfo.fbauth.currentUser == null){
                    Toast.makeText(this, "로그인 후 이용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    this.finish()
                    val itt = Intent(this, MyFavoritesActivity::class.java)
                    startActivity(itt)
                }

            }
            R.id.navimenu_four->{
                //Toast.makeText(this, "Menu4", Toast.LENGTH_SHORT).show()
                //search list == this
                //this.finish()
                val itt = Intent(this, FavoritesListActivity::class.java)
                itt.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(itt)
            }
        }
        return false
    }

    override fun onBackPressed() {
        if(mDrawerLayout!!.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout!!.closeDrawers()
        }else{
            super.onBackPressed()
        }
    }
}