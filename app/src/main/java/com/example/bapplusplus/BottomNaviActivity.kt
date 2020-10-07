package com.example.bapplusplus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.bapplusplus.fragment.BnFragment3
import com.example.bapplusplusTemp.fragment.BnFragment1
import com.example.bapplusplusTemp.fragment.BnFragment2
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_bottom_navi.*
import kotlinx.android.synthetic.main.activity_bottom_navi.view.*

class BottomNaviActivity : AppCompatActivity() {
    var frag1save: BnFragment1? = null
    var frag2save: BnFragment2? = null
    var frag3save: BnFragment3? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navi)

        //if (savedInstanceState == null) {
        frag1save = BnFragment1()
        supportFragmentManager.beginTransaction().replace(R.id.bn_framelayout, frag1save!!, frag1save!!.javaClass.simpleName).commit()

        //}

        val bn_toolbar = findViewById(R.id.bn_toolbar) as Toolbar
//        setSupportActionBar(bn_toolbar)
//
//        val ab = supportActionBar!!
//        ab.setDisplayShowTitleEnabled(false)
//        ab.setDisplayHomeAsUpEnabled(true)
        bn_toolbar.bn_toolbar_title.text = "Name BottomNaviActivity"
//        loadFragment(BnFragment1())
//
        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.navigation_menu1->{
                    if(frag1save == null){
                        frag1save = BnFragment1()
                        supportFragmentManager.beginTransaction().add(R.id.bn_framelayout, frag1save!!).commit()
                    }

                    if(frag1save != null) supportFragmentManager.beginTransaction().show(frag1save!!).commit()
                    if(frag2save != null) supportFragmentManager.beginTransaction().hide(frag2save!!).commit()
                    if(frag3save != null) supportFragmentManager.beginTransaction().hide(frag3save!!).commit()
                    //val frag = BnFragment1()
                    //supportFragmentManager.beginTransaction().replace(R.id.bn_framelayout, frag, frag.javaClass.simpleName).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_menu2->{
                    if(frag2save == null){
                        frag2save = BnFragment2()
                        supportFragmentManager.beginTransaction().add(R.id.bn_framelayout, frag2save!!).commit()
                    }

                    if(frag1save != null) supportFragmentManager.beginTransaction().hide(frag1save!!).commit()
                    if(frag2save != null) supportFragmentManager.beginTransaction().show(frag2save!!).commit()
                    if(frag3save != null) supportFragmentManager.beginTransaction().hide(frag3save!!).commit()
                    //val frag = frag2save

                    //supportFragmentManager.beginTransaction().replace(R.id.bn_framelayout, frag2save, frag2save.javaClass.simpleName).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_menu3->{
                    if(frag3save == null){
                        frag3save = BnFragment3()
                        supportFragmentManager.beginTransaction().add(R.id.bn_framelayout, frag3save!!).commit()
                    }

                    if(frag1save != null) supportFragmentManager.beginTransaction().hide(frag1save!!).commit()
                    if(frag2save != null) supportFragmentManager.beginTransaction().hide(frag2save!!).commit()
                    if(frag3save != null) supportFragmentManager.beginTransaction().show(frag3save!!).commit()

                    //val frag = BnFragment1()

                    //supportFragmentManager.beginTransaction().replace(R.id.bn_framelayout, frag, frag.javaClass.simpleName).commit()

                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

        bn_btmnavi.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

//        var transaction = FragmentManager.beginTransaction()
//        transaction.replace(R.id.bn_framelayout, fragment1).commitAllowingStateLoss()

    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }*/

}