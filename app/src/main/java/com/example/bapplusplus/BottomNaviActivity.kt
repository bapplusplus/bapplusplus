package com.example.bapplusplus

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.bapplusplus.fragment.BnFragment2
import com.example.bapplusplus.fragment.BnFragment4
import com.example.bapplusplusTemp.fragment.BnFragment1
import com.example.bapplusplusTemp.fragment.BnFragment3
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_bottom_navi.*
import kotlinx.android.synthetic.main.activity_bottom_navi.view.*


class BottomNaviActivity : AppCompatActivity() {
    var frag1save: BnFragment1? = null
    var frag2save: BnFragment2? = null
    //var frag3save: BnFragment3? = null
    var frag3save: BnFragment4? = null

    var posx = 0.0
    var posy = 0.0
    lateinit var getinfo: RestInfoTemp

    var RestNo = 0
    var get_posx = 0.0
    var get_posy = 0.0
    var RestTitle = ""
    var RestRoadAddress = ""
    var RestCallNum = ""
    var RestCategory = ""
    var gni_my_like = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navi)
        val bundle = Bundle()
        val itt = intent
        val fbdb = FirebaseFirestore.getInstance()

        //val progressDialog = ProgressDialog(this)
        //progressDialog.setMessage("ProgressDialog running...")
        //progressDialog.setCancelable(true)
        //progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        //progressDialog.show()
        //posx = intent.getDoubleExtra("posx", 30.5613217)
        //posy = intent.getDoubleExtra("posy", 127.0384896)
        //getinfo = intent.getParcelableExtra<RestInfoTemp>("infoList")!!

        RestNo = intent.getIntExtra("gni_num", 0)

        RestTitle = intent.getStringExtra("gni_title").toString()
        RestCategory = intent.getStringExtra("gni_category").toString()
        gni_my_like = intent.getBooleanExtra("gni_my_like", false)
        println("btn restno" + RestNo + " btn_resttitle" + RestTitle)
        get_posx = itt.getDoubleExtra("pppx", 1.0)
        get_posy = itt.getDoubleExtra("pppy", 1.0)
        println(
            "btn pppx" + itt.getDoubleExtra("pppx", 1.0) + " btn_pppy" + itt.getDoubleExtra(
                "pppy",
                1.0
            )
        )
        bundle.putInt("RestNo", RestNo)
        bundle.putString("RestTitle", RestTitle)
        bundle.putString("RestCategory", RestCategory)
        bundle.putBoolean("isMyLike", gni_my_like)
        bundle.putInt("RestReviewNum", intent.getIntExtra("gni_review_num", -1))
        bundle.putDouble("RestRatingAvg", intent.getDoubleExtra("gni_rating_avg", 0.4))
        bundle.putDouble("pppx", itt.getDoubleExtra("pppx", 1.0))
        bundle.putDouble("pppy", itt.getDoubleExtra("pppy", 1.0))




        fbdb.collection("tmp3v")
            .whereEqualTo("RestNo", RestNo)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents)
                    if (document != null) {
                        Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                        bundle.putDouble("RestPosx", document.getDouble("RestPosx") ?: 0.0)
                        bundle.putDouble("RestPosy", document.getDouble("RestPosy") ?: 0.0)
//                        bundle.putString("RestTitle", document.getString("RestTitle").toString())
//                        bundle.putString("RestRoadAddress", document.getString("RestRoadAddress").toString())
//                        bundle.putString("RestCallNum", document.getString("RestCallNum").toString())
//                        bundle.putString("RestCategory", document.getString("RestCategory").toString())
                        //progressDialog.dismiss()
                    } else {
                        Log.d("TAG", "No such document")
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }

        //val bundle = Bundle()
        bundle.putDouble("posx", posx)
        bundle.putDouble("posy", posy)
        //bundle.putParcelable("infotemp", getinfo)


        //if (savedInstanceState == null) {
        frag1save = BnFragment1()
//        frag2save = BnFragment2()
        frag1save!!.arguments = bundle
        supportFragmentManager.beginTransaction().replace(
            R.id.bn_framelayout,
            frag1save!!,
            frag1save!!.javaClass.simpleName
        ).commit()
//        supportFragmentManager.beginTransaction().add(R.id.bn_framelayout, frag2save!!).commit()
//        supportFragmentManager.beginTransaction().hide(frag2save!!).commit()

        //}

        val bn_toolbar = findViewById(R.id.bn_toolbar) as Toolbar
        setSupportActionBar(bn_toolbar)

        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)
        bn_toolbar.bn_toolbar_title.text = RestTitle +" 정보"
//        loadFragment(BnFragment1())

        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.navigation_menu1 -> {
                    if (frag1save == null) {
                        frag1save = BnFragment1()
                        supportFragmentManager.beginTransaction().add(
                            R.id.bn_framelayout,
                            frag1save!!
                        ).commit()
                        frag1save!!.arguments = bundle
                    }

                    if (frag1save != null) supportFragmentManager.beginTransaction()
                        .show(frag1save!!).commit()
                    if (frag2save != null) supportFragmentManager.beginTransaction()
                        .hide(frag2save!!).commit()
                    if (frag3save != null) supportFragmentManager.beginTransaction()
                        .hide(frag3save!!).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_menu2 -> {
                    if (frag2save == null) {
                        frag2save = BnFragment2()
                        supportFragmentManager.beginTransaction().add(
                            R.id.bn_framelayout,
                            frag2save!!
                        ).commit()
                        frag2save!!.arguments = bundle
                    }

                    if (frag1save != null) supportFragmentManager.beginTransaction()
                        .hide(frag1save!!).commit()
                    if (frag2save != null) supportFragmentManager.beginTransaction()
                        .show(frag2save!!).commit()
                    if (frag3save != null) supportFragmentManager.beginTransaction()
                        .hide(frag3save!!).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_menu3 -> {
                    if (frag3save == null) {
                        frag3save = BnFragment4()
                        supportFragmentManager.beginTransaction().add(
                            R.id.bn_framelayout,
                            frag3save!!
                        ).commit()
                        frag3save!!.arguments = bundle
                    }

                    if (frag1save != null) supportFragmentManager.beginTransaction()
                        .hide(frag1save!!).commit()
                    if (frag2save != null) supportFragmentManager.beginTransaction()
                        .hide(frag2save!!).commit()
                    if (frag3save != null) supportFragmentManager.beginTransaction()
                        .show(frag3save!!).commit()

                    return@OnNavigationItemSelectedListener true
                }
            }


            false
        }


        bn_btmnavi.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

//        var transaction = FragmentManager.beginTransaction()
//        transaction.replace(R.id.bn_framelayout, fragment1).commitAllowingStateLoss()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}