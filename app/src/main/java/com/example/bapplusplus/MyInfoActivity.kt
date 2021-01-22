package com.example.bapplusplus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.bapplusplus.data.FBUserInfo
import com.example.bapplusplus.fragment.MiFavFragment
import com.example.bapplusplus.fragment.MiFirstFragment
import kotlinx.android.synthetic.main.activity_my_info.*
import kotlinx.android.synthetic.main.activity_my_info.view.*

class MyInfoActivity : AppCompatActivity() {

    var firstFrag: MiFirstFragment? = null
    lateinit var customModeFrag: MiFavFragment
    var fm = supportFragmentManager
    var mode = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)

        /*myinfo_tv_user_name.text = FBUserInfo.fbuser!!.displayName.toString()
        myinfo_tv_user_email.text = FBUserInfo.fbuser!!.email.toString()

        myinfo_const_user.setOnClickListener {
            Toast.makeText(this, "User Clicked", Toast.LENGTH_SHORT).show()
        }

        myinfo_const_favs.setOnClickListener {
            Toast.makeText(this, "Favs Clicked", Toast.LENGTH_SHORT).show()
        }

        myinfo_const_review.setOnClickListener {
            Toast.makeText(this, "Review Clicked", Toast.LENGTH_SHORT).show()
        }*/

        mode = intent.getIntExtra("Mode", -2)

        if(mode > 10){
            val bundle = Bundle()
            customModeFrag = MiFavFragment()
            customModeFrag!!.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.myinfo_frame, customModeFrag!!, customModeFrag!!.javaClass.simpleName).commit()
        }else{
            val bundle = Bundle()
            firstFrag = MiFirstFragment()
            firstFrag!!.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.myinfo_frame, firstFrag!!, firstFrag!!.javaClass.simpleName).commit()
        }



        val mi_toolbar = myinfo_toolbar as Toolbar
        setSupportActionBar(mi_toolbar)

        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)


    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        if(supportFragmentManager.backStackEntryCount > 0 ) {
//            supportFragmentManager.popBackStack();//Pops one of the added fragments
//        }
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                onBackPressed()
                //finish()
                //return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}