package com.example.bapplusplus.fragment

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.example.bapplusplus.R
import com.example.bapplusplus.RestInfoTemp
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolylineOverlay
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.fragment_bn2.*
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class BnFragment2 : Fragment(), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var mapN: NaverMap
    //private var locationButtonMode = 0
    private var distanceEstimate = 0.0
    private lateinit var locpos: Location
    private lateinit var lm: LocationManager
    lateinit var loclis: LocationListener
    private val REQUEST_CODE_LOCATION: Int = 2
    private var positionX: Double = 0.0
    private var positionY: Double = 0.0
    private lateinit var getinfopar: RestInfoTemp
    private var RestPosx = 0.0
    private var RestPosy = 0.0
    private var getPosx = 11.0
    private var getPosy = 12.0
    private var RestNo = 0
    private lateinit var RestLoc: Location
    var currentLatLng: Location? = null
    lateinit var fusedLocationClient: FusedLocationProviderClient



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootview = inflater.inflate(R.layout.fragment_bn2, container, false)

        val bundle = arguments
        val fbdb = FirebaseFirestore.getInstance()
        RestNo = bundle?.getInt("RestNo") ?: 0
        getPosx = bundle?.getDouble("pppx") ?: 1.1
        getPosy = bundle?.getDouble("pppy") ?: 1.2
        println("Bn3bundle "+ RestNo)
        println("Bn3bundlex "+ getPosx)
        println("Bn3bundley "+ getPosy)
        RestLoc = Location("pov")
        RestLoc.latitude = getPosy
        RestLoc.longitude = getPosx
        lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager


        //positionX = bundle?.getDouble("posx") ?: 36.5613999
        //positionY = bundle?.getDouble("posy") ?: 127.0384896
        //getinfopar = bundle?.getParcelable<RestInfoTemp>("infotemp")!!
        //positionX = getinfopar.locpos?.latitude ?: 33.333333
        //positionY = getinfopar.locpos?.longitude ?: 127.127127
        //RestPosx = bundle?.getDouble("RestPosx")!!
        //RestPosy = bundle?.getDouble("RestPosy")!!
        //RestLoc = Location("provider")
//        RestLoc.latitude = RestPosx
//        RestLoc.longitude = RestPosy
        var mapFragment : MapFragment? //= childFragmentManager.findFragmentById(R.id.bn_frame_2) as MapFragment?
            /*?: run {
                println("mapfr")
                /*fbdb.collection("tmp3v")
                    .whereEqualTo("RestNo", RestNo)
                    .get()
                    .addOnSuccessListener { documents ->
                        for(document in documents)
                            if (document != null) {
                                Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                                RestPosx = document.getDouble("RestPosx")!!
                                RestPosy = document.getDouble("RestPosy")!!
                                RestLoc = Location("provider")
                                RestLoc.latitude = RestPosx
                                RestLoc.longitude = RestPosy
                                println("Bn3x "+ RestPosx)
                                println("Bn3y "+ RestPosy)
                            } else {
                                Log.d("TAG", "No such document - Fragment1")
                            }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("TAG", "get failed with ", exception)
                    }*/

                val options = NaverMapOptions()
                    .camera(CameraPosition(LatLng(RestPosx, RestPosy), 16.0, 0.0, 0.0))
                    .locationButtonEnabled(true)
                MapFragment.newInstance(options).also {
                    childFragmentManager.beginTransaction().add(R.id.bn_frame_2, it).commit()
                }
            }
        mapFragment.getMapAsync(this)*/

        fbdb.collection("tmp7vBasic").document("RestBasic"+RestNo.toString())
            .get()
            .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d("TAG", "RRRR DocumentSnapshot data: ${document.data}")
                        RestPosx = document.getDouble("RestPosx")!!
                        RestPosy = document.getDouble("RestPosy")!!
                        RestLoc.latitude = RestPosy
                        RestLoc.longitude = RestPosx
                            mapFragment = childFragmentManager.findFragmentById(R.id.bn_frame_2) as MapFragment?
                                ?:run {
                                println("mapfr")
                                /*fbdb.collection("tmp3v")
                                    .whereEqualTo("RestNo", RestNo)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        for(document in documents)
                                            if (document != null) {
                                                Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                                                RestPosx = document.getDouble("RestPosx")!!
                                                RestPosy = document.getDouble("RestPosy")!!
                                                RestLoc = Location("provider")
                                                RestLoc.latitude = RestPosx
                                                RestLoc.longitude = RestPosy
                                                println("Bn3x "+ RestPosx)
                                                println("Bn3y "+ RestPosy)
                                            } else {
                                                Log.d("TAG", "No such document - Fragment1")
                                            }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d("TAG", "get failed with ", exception)
                                    }*/

                                val options = NaverMapOptions()
                                    .camera(CameraPosition(LatLng(RestPosy, RestPosx), 16.0, 0.0, 0.0))
                                    .locationButtonEnabled(true)
                                MapFragment.newInstance(options).also {
                                    childFragmentManager.beginTransaction().add(R.id.bn_frame_2, it).commit()
                                }
                            }
                        mapFragment?.getMapAsync(this)


                    } else {
                        Log.d("TAG", "No such document - Fragment2")
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }


        //mapFragment.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        //lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locpos = Location("point")
        locpos.latitude = 35.5613217
        locpos.longitude = 127.0384896
//        distanceEstimate = locationSource.lastLocation?.distanceTo(locpos)?.toDouble()!!

        return rootview
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



    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    override fun onMapReady(naverMap: NaverMap) {
        mapN = naverMap
        mapN.locationSource = locationSource
        //mapN.cameraPosition

        mapN.addOnOptionChangeListener {
            val mode = naverMap.locationTrackingMode

            locationSource.isCompassEnabled = mode == LocationTrackingMode.Follow || mode == LocationTrackingMode.Face

        }
        mapN.locationTrackingMode = LocationTrackingMode.None


        val marker = Marker().apply {
            position = LatLng(RestPosy, RestPosx)
            setOnClickListener {
//                mapN.locationTrackingMode = LocationTrackingMode.None
//                distanceEstimate = locationSource.lastLocation!!.distanceTo(locpos).toDouble()

                var presentLoc = getCurrentLocationNu()
                //locationSource.isCompassEnabled = true
                //distanceEstimate = presentLoc?.distanceTo(RestLoc)?.toDouble() ?: 111.0
                distanceEstimate = getCurrentLocationNu()?.distanceTo(RestLoc)?.toDouble() ?: 111.0
//                Toast.makeText(activity, distanceEstimate.toString(), Toast.LENGTH_SHORT).show()
//                var toast_test = View.inflate(requireContext(), R.layout.toast_custom_1, null)
//                var tst = Toast(requireContext())
//                tst.view = toast_test
//                toastc1_tv.text = "Distance: "+distanceEstimate.toString()
                if(distanceEstimate > 1000){
//                    toast_test.findViewById<TextView>(R.id.toastc1_tv).text = "Distance: "+Math.round((distanceEstimate/1000.0)).toString() +"km"
//                    toast_test.findViewById<TextView>(R.id.toastc1_tv).text = "Distance: "+((distanceEstimate/1000.0)*10).roundToInt() / 10f + "km"
                    bn_info_add_txt.text = "거리 "+((distanceEstimate/1000.0)*10).roundToInt() / 10f + "km"

                }
                else{
//                    toast_test.findViewById<TextView>(R.id.toastc1_tv).text = "Distance: "+distanceEstimate.toString() +"m"
                    bn_info_add_txt.text = "거리 "+distanceEstimate.roundToInt().toString() +"m"
                }
                bn_const_info_add.startAnimation(slideDownAndVanish3(bn_const_info_add))

                PolylineOverlay().also {
                    it.width = 10
                    it.coords = listOf(LatLng(presentLoc!!), LatLng(RestPosy, RestPosx))
                    it.color = ResourcesCompat.getColor(resources, R.color.colorBlue1, requireActivity().theme)
                    it.map = naverMap
                }.apply {
                    mapN.locationTrackingMode = LocationTrackingMode.Face
                    val position = CameraPosition(LatLng(presentLoc!!), 8.0)
                    val animation = CameraAnimation.Easing
                    naverMap.moveCamera(CameraUpdate.toCameraPosition(position).animate(animation, 2000))

                }

//                tst.setGravity(Gravity.TOP or Gravity.RIGHT, 50, 180)
//                tst.duration = Toast.LENGTH_LONG
//                tst.show()
                Toast.makeText(context, "LatLng Marker "+ RestPosx +" / " + RestPosy, Toast.LENGTH_SHORT).show()
                true
            }
            tag = 10
            map = mapN
        }



    }

    private fun getLatLng(): Location? {
        //var currentLatLng: Location? = null
        var  isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                this.REQUEST_CODE_LOCATION
            )
            getLatLng()
        }
        else{
            /*if (isGPSEnabled) {
                Log.d("loc xy", "gps enabled")
                if (currentLatLng == null) {
                    Log.d("loc xy", "currentlatlng is null")
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.toFloat(), loclis);
                    if (lm != null) {
                        Log.d("loc xy", "lm is not null")
                        currentLatLng = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (currentLatLng != null) {
                            Log.d("loc xy", "currentlatlng is not null")
                            return currentLatLng
                        }
                    }
                }
            }*/

            getLocSource()
            val locationProvider = LocationManager.GPS_PROVIDER
            currentLatLng = getCurrentLocationNu()

            if(currentLatLng == null){
                Log.d("currentlatlng", "currentLatLng is null")
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1.toFloat(), loclis)
                currentLatLng = lm.getLastKnownLocation(locationProvider)
            }

            //var lastlocknown =



            //currentLatLng = lm.getLastKnownLocation(locationProvider)
            Log.d("Loc xy", currentLatLng?.longitude.toString()+", "+currentLatLng?.latitude.toString())
            return currentLatLng
        }

        return currentLatLng!!
    }

    fun slideDownAndVanish3(view: View): AnimationSet {
        var animset: AnimationSet = AnimationSet(true)
        animset.isFillEnabled = true
        animset.fillAfter = true
//        view.visibility = View.VISIBLE
        var animate1 = TranslateAnimation(0F, 0F, -view.height.toFloat(), 0F)
        animate1.startOffset = 0
        animate1.duration = 400
        animate1.isFillEnabled = true
        animate1.fillAfter = true
//        view.startAnimation(animate1)

        //var animate2 = TranslateAnimation(0F, 0F, 0F, -view.height.toFloat())
        //animate2.startOffset = 3200
        //animate2.duration = 400
        //animate2.fillAfter = true
//        view.startAnimation(animate2)
        animset.addAnimation(animate1)
        //animset.addAnimation(animate2)

        return animset
    }

    fun getCurrentLocationNu(): Location? {
        val isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (Build.VERSION.SDK_INT >= 23 && requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        } else {
            when { //프로바이더 제공자 활성화 여부 체크
                isNetworkEnabled ->{
                    val location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) //인터넷기반으로 위치를 찾음
                    //getLongitude = location?.longitude!!
                    //getLatitude = location.latitude
                    //toast("현재위치를 불러옵니다.")
                    Toast.makeText(requireContext(), "netw: "+location?.latitude.toString()+", "+location?.longitude.toString(), Toast.LENGTH_SHORT).show()
                    return location
                }
                isGPSEnabled ->{
                    val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) //GPS 기반으로 위치를 찾음
                    //getLongitude = location?.longitude!!
                    //getLatitude = location.latitude
                    //toast("현재위치를 불러옵니다.")
                    Toast.makeText(requireContext(), "gpsw: "+location?.latitude.toString()+", "+location?.longitude.toString(), Toast.LENGTH_SHORT).show()
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

    fun getLocSource(){
        //locationSource.lastLocation

        Toast.makeText(context, locationSource.lastLocation?.latitude.toString() +", "+locationSource.lastLocation?.longitude.toString(), Toast.LENGTH_SHORT).show()
    }

}