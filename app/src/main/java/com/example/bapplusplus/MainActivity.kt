package com.example.bapplusplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.bapplusplus.adapter.MainHotAdapter
import com.example.bapplusplus.data.App
import com.example.bapplusplus.data.FBUserInfo
import com.example.bapplusplus.deprecated.TestLoginActivity
import com.example.bapplusplus.fragment.BottomSheetMainQuit
import com.example.bapplusplus.fragment.MyFavMapFragment
import com.example.bapplusplus.fragment.MyFav_Data
import com.example.bapplusplusTemp.fragment.Bn1Info_Data
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.fragment_my_fav_map.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

data class MainHot_Data(
    var restNo: Int,
    var restTitle: String? = "",
    var restRatingAvg: Double,
    var restPosx: Double,
    var restPosy: Double
)

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    var UserName: String = ""
    val infoList: ArrayList<RestInfoTemp> = arrayListOf(
        RestInfoTemp("삼삼구 한양대점", 4.27, "육류, 고기요리", "매일 11:00 - 22:00", 37.5594683,127.0382745,
            "서울 성동구 마조로 17 1층 삼삼구","0507-1473-3394", null)
    )

    //Data Array
    var ratingArray: ArrayList<GetNumsInfo> = arrayListOf()
    var hotArray: ArrayList<MainHot_Data> = arrayListOf()
    lateinit var hotAdapter: MainHotAdapter
    //Map
    private lateinit var locationSource: FusedLocationSource
    private lateinit var mapN: NaverMap
    var fbdb = FirebaseFirestore.getInstance()

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        lateinit var loginbtn: Button
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //FBUserInfo()
        main_const_bot.visibility = View.INVISIBLE
        loginbtn = findViewById(R.id.btn_login)

        var islogin = intent.getIntExtra("login", 0)

        if(FBUserInfo.fbauth.currentUser != null){
            println("main loggedin")
            btn_login.visibility = View.GONE
        }else{
            println("main loggedout")
            btn_login.visibility = View.VISIBLE
        }

        /*if(islogin == 10){
            println("main islogin"+islogin.toString())
            btn_login.visibility = View.GONE
        }else if(islogin == 5){
            println("main islogin"+islogin.toString())
            btn_login.visibility = View.VISIBLE
        }*/

        iv_roulette.setOnClickListener {
            val intent = Intent(this, RouletteActivity::class.java)
            startActivity(intent)
        }

        iv_search.setOnClickListener {
            val intent = Intent(this, FavoritesListActivity::class.java)
            startActivity(intent)
        }

        iv_heart.setOnClickListener {
            if(FBUserInfo.fbauth.currentUser == null){
                Toast.makeText(this, "로그인 후 이용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(this, MyFavoritesActivity::class.java)
                startActivity(intent)
            }
        }

        iv_setting.setOnClickListener {
            val intent = Intent(this, MyInfoActivity::class.java)
            startActivity(intent)
        }

        btn_login.setOnClickListener {
            if(FBUserInfo.fbauth.currentUser != null){

            }else{
                val intent = Intent(this, NewLoginActivity::class.java)
                startActivityForResult(intent, 19)
            }
        }

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
//            }
//        }
        var mapFragment : MapFragment?
        hotAdapter = MainHotAdapter(this, hotArray)
        main_hot_recycler.adapter = hotAdapter
        //set hot info
        CoroutineScope(IO).launch {
            getRatingArray()
            getHotArray()

            withContext(Main){
                hotAdapter!!.notifyDataSetChanged()
                main_hot_progress.visibility = View.GONE
                main_const_bot.visibility = View.VISIBLE
                mapFragment = supportFragmentManager.findFragmentById(R.id.main_hot_frame) as MapFragment?
                    ?:run {
                        //println("mapfr")
                        val options = NaverMapOptions()
                            .camera(CameraPosition(LatLng(37.56019, 127.03955), 13.8, 0.0, 0.0))
                            .locationButtonEnabled(true)
                        MapFragment.newInstance(options).also {
                            supportFragmentManager.beginTransaction().add(R.id.main_hot_frame, it).commit()
                        }
                    }
                mapFragment?.getMapAsync(this@MainActivity)
                locationSource = FusedLocationSource(this@MainActivity, LOCATION_PERMISSION_REQUEST_CODE)
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(resultCode){
            19->btn_login.visibility = View.GONE
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed() {
        //dialog
        val bsmq = BottomSheetMainQuit()
        bsmq.show(supportFragmentManager, bsmq.tag)

        //super.onBackPressed()
    }

    suspend fun getRatingArray(){
        ratingArray.clear()

        var docRefNew = fbdb.collection("tmp7vList").document("bf6c7ba0-537c-11eb-85d6-f96783d0ff1e").get().await()

        var list2 = docRefNew.data!!.get("tmp7vArray") as List<*>

        for (kk in 0..list2.count() - 1) {
            var map1 = list2.get(kk) as HashMap<*, *>
            var gni = GetNumsInfo(
                map1.get("RestNo").toString().toInt(),
                map1.get("RestTitle").toString(),
                map1.get("RestRatingAvg").toString().toDouble(),
                map1.get("RestReviewNum").toString().toInt(),
                map1.get("RestCategory").toString()
            )
            ratingArray.add(gni)
        }
        //listtry.sortedBy { it.RestNo } //as ArrayList<GetNumsInfo>
        ratingArray.sortByDescending { it.RestRatingAvg }
    }

    suspend fun getHotArray(){
        hotArray.clear()
        for(cc in 0..2){
            val doc = fbdb!!.collection("tmp7vBasic").document("RestBasic" + ratingArray.get(cc).RestNo.toString()).get().await()
            var basicInfo = MainHot_Data(
                (doc.get("RestNo") as Long).toInt(),
                doc.get("RestTitle").toString(),
                ratingArray.get(cc).RestRatingAvg,
                doc.get("RestPosx") as Double,
                doc.get("RestPosy") as Double
            )
            hotArray.add(basicInfo)
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        mapN = naverMap
        mapN.locationSource = locationSource
        mapN.uiSettings.isLocationButtonEnabled = false
        //mapN.cameraPosition

        mapN.addOnOptionChangeListener {
            val mode = naverMap.locationTrackingMode

            locationSource.isCompassEnabled = mode == LocationTrackingMode.Follow || mode == LocationTrackingMode.Face

        }
        mapN.locationTrackingMode = LocationTrackingMode.None


        var markerArray = ArrayList<Marker>()
        for(cc in 0..2){
            markerArray.add(Marker().apply {
                position = LatLng(hotArray.get(cc).restPosy, hotArray.get(cc).restPosx)
                tag = hotArray.get(cc).restTitle
                map = mapN
                setOnClickListener {
                    Toast.makeText(this@MainActivity, hotArray.get(cc).restTitle, Toast.LENGTH_SHORT).show()
                    false
                }
            })
        }
    }
}