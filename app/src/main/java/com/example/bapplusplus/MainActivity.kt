package com.example.bapplusplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import com.example.bapplusplus.data.App
import com.example.bapplusplus.data.FBUserInfo
import com.example.bapplusplus.deprecated.TestLoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    var UserName: String = ""
    val infoList: ArrayList<RestInfoTemp> = arrayListOf(
        RestInfoTemp("삼삼구 한양대점", 4.27, "육류, 고기요리", "매일 11:00 - 22:00", 37.5594683,127.0382745,
            "서울 성동구 마조로 17 1층 삼삼구","0507-1473-3394", null)
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //FBUserInfo()



        iv_roulette.setOnClickListener {
            val intent = Intent(this, RouletteActivity::class.java)
            startActivity(intent)
        }

        iv_search.setOnClickListener {

        }

        iv_heart.setOnClickListener {

        }

        iv_setting.setOnClickListener {

        }

        btn_login

//        btn_newlogin.setOnClickListener {
//            val fbuser = FirebaseAuth.getInstance().currentUser?.uid
//            val fbdb = FirebaseFirestore.getInstance()
//            if(fbuser == null){
//                val intent = Intent(this, NewLoginActivity::class.java)
//                startActivity(intent)
//            }else{
//                Log.d("Main User Info:", FBUserInfo.userName)
//                val intent = Intent(this, MyInfoActivity::class.java)
//                startActivity(intent)
//
//            }
//        }

//        btn_reviewupload.setOnClickListener {
//            if(FBUserInfo.loginState == false){
//                Toast.makeText(this, "Not Logged In", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, ReviewUploadActivity::class.java)
//                startActivity(intent)
//            }else{
//                Toast.makeText(this, FBUserInfo.userName, Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, ReviewUploadActivity::class.java)
//                startActivity(intent)
//            }
//
//            val intent = Intent(this, FavListRouletteActivity::class.java)
//            startActivity(intent)
//        }
//
//        btn_testingact.setOnClickListener {
//            val intent = Intent(this, TestingActivity::class.java)
//            startActivity(intent)
//        }

    }


    override fun onBackPressed() {
        if(!App.prefs.isAutoLogin && !App.prefs.passwordValue.isNullOrEmpty()){
            FBUserInfo.setSignOut()
            Toast.makeText(this, "Logout, Finish", Toast.LENGTH_SHORT).show()
        }
        super.onBackPressed()
    }
}