package com.example.bapplusplus.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.bapplusplus.BottomNaviActivity
import com.example.bapplusplus.MyFavoritesActivity
import com.example.bapplusplus.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.activity_my_favorites.*
import kotlinx.android.synthetic.main.fragment_my_fav_map.*
import kotlinx.android.synthetic.main.fragment_my_fav_map.view.*
import kotlin.math.roundToInt


class MyFavMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var mapN: NaverMap
    private val REQUEST_CODE_LOCATION: Int = 2
    //private lateinit var firstMapGet:
    var sendtry = ""
    lateinit var myFavNewArray: ArrayList<MyFav_Data>
    private lateinit var lm: LocationManager
    lateinit var RestLoc: Location
    private var permissionCheck: Int = -100
    private val REQUEST_ACCESS_FIND_LOCATION = 1000


    private class InfoWindowAdapter(context: Context) : InfoWindow.DefaultTextAdapter(context) {

        override fun getText(infoWindow: InfoWindow): String {
            val marker = infoWindow.marker
            return if (marker != null) {
                marker.tag.toString()
            } else {
                ""
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var rootview =  inflater.inflate(R.layout.fragment_my_fav_map, container, false)

        permissionCheck = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)

        val getmbb = arguments
        println(getmbb?.getString("sendtry") ?: "map nun")
        myFavNewArray = getmbb?.getParcelableArrayList<MyFav_Data>("get_array") as ArrayList<MyFav_Data>

        lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        RestLoc = Location("pov")

        rootview.mfm_frame_downside.visibility = View.GONE

        rootview.mfm_frame_downside.setOnTouchListener { view, motionEvent ->
            true
        }

        val ab = (requireActivity() as MyFavoritesActivity).supportActionBar
        //ab!!.setHomeAsUpIndicator(R.drawable.ic_dehaze_white_24dp)
        ab!!.setDisplayHomeAsUpEnabled(false)

        requireActivity().myfav_toolbar_title.text = "지도에서 보기"


        var mapFragment : MapFragment?
        mapFragment = childFragmentManager.findFragmentById(R.id.mfm_frame) as MapFragment?
            ?:run {
                //println("mapfr")
                val options = NaverMapOptions()
                    .camera(CameraPosition(LatLng(37.56019, 127.03955), 14.5, 0.0, 0.0))
                    .locationButtonEnabled(true)
                MapFragment.newInstance(options).also {
                    childFragmentManager.beginTransaction().add(R.id.mfm_frame, it).commit()
                }
            }
        mapFragment?.getMapAsync(this)
        locationSource = FusedLocationSource(
            this,
            MyFavMapFragment.LOCATION_PERMISSION_REQUEST_CODE
        )

        return rootview
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        //menu.clear()
        inflater.inflate(R.menu.myfav_menu_file_empty, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            android.R.id.home -> {
                requireParentFragment().requireActivity().mafav_drawer.openDrawer(GravityCompat.START)
                //requireActivity().onBackPressed()
                Toast.makeText(requireContext(), "나옴?", Toast.LENGTH_SHORT).show()
                //requireActivity().mafav_drawer.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                mapN.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(naverMap: NaverMap) {
        mapN = naverMap
        //mapN.locationSource = locationSource
        //mapN.cameraPosition

        mapN.addOnOptionChangeListener {
            val mode = naverMap.locationTrackingMode
            mapN.locationSource = locationSource
            locationSource.isCompassEnabled = mode == LocationTrackingMode.Follow || mode == LocationTrackingMode.Face

        }
        //mapN.locationTrackingMode = LocationTrackingMode.None

        val infoWindow = InfoWindow().apply {
            position = LatLng(37.5666102, 126.9783881)
            adapter = InfoWindowAdapter(requireContext())
            setOnClickListener {
                close()
                mfm_frame_downside.visibility = View.GONE
                true
            }
            open(naverMap)
        }

        var markerArray = ArrayList<Marker>()
        for(cc in 0..myFavNewArray.count()-1){
            markerArray.add(Marker().apply {
                position = LatLng(myFavNewArray.get(cc).restPosy, myFavNewArray.get(cc).restPosx)
                tag = myFavNewArray.get(cc).restTitle
                map = mapN

                setOnClickListener {
                    var distanceEstimate = 0.0

                    RestLoc.latitude = myFavNewArray.get(cc).restPosy
                    RestLoc.longitude = myFavNewArray.get(cc).restPosx

                    mfmf_tv_title.text = myFavNewArray.get(cc).restTitle
                    mfmf_tv_address.text = myFavNewArray.get(cc).restCategory

                    if(permissionCheck == PackageManager.PERMISSION_DENIED){
                        mfmf_tv_distance.text = ""
                    }else{
                        distanceEstimate =
                            getCurrentLocationNu()?.distanceTo(RestLoc)?.toDouble() ?: 111.0
                        if (distanceEstimate > 1000) {
                            mfmf_tv_distance.text =
                                (((distanceEstimate / 1000.0) * 10).roundToInt() / 10f).toString() + "km"
                        } else {
                            mfmf_tv_distance.text = distanceEstimate.roundToInt().toString() + "m"
                        }
                    }

                    mfmf_btn_details.setOnClickListener {
                        var ittbn = Intent(requireContext(), BottomNaviActivity::class.java)
                        ittbn.putExtra("gni_num", myFavNewArray.get(cc).restNo)
                        ittbn.putExtra("gni_title", myFavNewArray.get(cc).restTitle)
                        startActivity(ittbn)
                    }



                    //println(myFavNewArray.get(cc).restPosy.toString()+", "+ myFavNewArray.get(cc).restPosx.toString())
                    //Toast.makeText(requireContext(), myFavNewArray.get(cc).restPosy.toString()+", "+ myFavNewArray.get(cc).restPosx.toString(), Toast.LENGTH_SHORT).show()
                    val animation = CameraAnimation.Easing
                    var campo = CameraPosition(
                        LatLng(
                            myFavNewArray.get(cc).restPosy, myFavNewArray.get(
                                cc
                            ).restPosx
                        ), 14.5
                    )
                    naverMap.moveCamera(
                        CameraUpdate.toCameraPosition(campo).animate(animation, 400)
                    )

                    mfm_frame_downside.visibility = View.VISIBLE
                    infoWindow.open(this)
                    true
                }
            })
        }

        /*val marker = Marker().apply {
            position = LatLng(myFavNewArray.get(0).restPosy, myFavNewArray.get(0).restPosx)

            tag = 10
            map = mapN
        }

        val marker2 = Marker().apply {
            position = LatLng(myFavNewArray.get(1).restPosy, myFavNewArray.get(1).restPosx)

            tag = 11
            map = mapN
        }*/

        naverMap.setOnMapClickListener { _, coord ->
            if(infoWindow.marker != null && !infoWindow.isVisible){
                mfm_frame_downside.visibility = View.GONE
                infoWindow.position = coord
                infoWindow.open(naverMap)
            }else if(infoWindow.isVisible){
                mfm_frame_downside.visibility = View.GONE
                infoWindow.close()
            }
        }
    }

    fun getCurrentLocationNu(): Location? {
        val isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (Build.VERSION.SDK_INT >= 23 && requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        } else {
            when { //프로바이더 제공자 활성화 여부 체크
                isNetworkEnabled ->{
                    val location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) //인터넷기반으로 위치를 찾음
                    //getLongitude = location?.longitude!!
                    //getLatitude = location.latitude
                    //toast("현재위치를 불러옵니다.")
                    //Toast.makeText(requireContext(), "netw: "+location?.latitude.toString()+", "+location?.longitude.toString(), Toast.LENGTH_SHORT).show()
                    return location
                }
                isGPSEnabled ->{
                    val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) //GPS 기반으로 위치를 찾음
                    //getLongitude = location?.longitude!!
                    //getLatitude = location.latitude
                    //toast("현재위치를 불러옵니다.")
                    //Toast.makeText(requireContext(), "gpsw: "+location?.latitude.toString()+", "+location?.longitude.toString(), Toast.LENGTH_SHORT).show()
                    return location
                }
                else ->{
                    return null
                }
            }
            //몇초 간격과 몇미터를 이동했을시에 호출되는 부분 - 주기적으로 위치 업데이트를 하고 싶다면 사용
            // ****주기적 업데이트를 사용하다가 사용안할시에는 반드시 해제 필요****
            /*lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, //몇초
                    1F,   //몇미터
                    gpsLocationListener)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000,
                    1F,
                    gpsLocationListener)
            //해제부분. 상황에 맞게 잘 구현하자
            lm.removeUpdates(gpsLocationListener)*/
        }
        return null
    }
}