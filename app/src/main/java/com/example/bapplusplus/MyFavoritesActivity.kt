package com.example.bapplusplus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.bapplusplus.fragment.MiFirstFragment
import com.example.bapplusplus.fragment.MyFavFirstFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_my_favorites.*
import kotlinx.android.synthetic.main.activity_my_info.*

class MyFavoritesActivity : AppCompatActivity() {

    companion object{

    }

    var favDrawerLayout: DrawerLayout? = null
    var favNavi: NavigationView? = null
    lateinit var mBundle: Bundle

    lateinit var firstFrag: MyFavFirstFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_favorites)

        val mf_toolbar = myfav_toolbar as Toolbar
        setSupportActionBar(mf_toolbar)

        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        //ab.setDisplayHomeAsUpEnabled(true)

        favDrawerLayout = findViewById(R.id.mafav_drawer)
        favNavi = findViewById(R.id.mff_navi)

        val bundle = Bundle()
        firstFrag = MyFavFirstFragment()
        firstFrag!!.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.myfav_frame, firstFrag!!, firstFrag!!.javaClass.simpleName).commit()
    }

    override fun onBackPressed() {
        if(mafav_drawer.isDrawerOpen(GravityCompat.START)){
            mafav_drawer.closeDrawers()
        }else{
            super.onBackPressed()
        }
    }
}