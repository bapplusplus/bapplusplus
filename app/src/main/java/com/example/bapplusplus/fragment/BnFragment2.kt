package com.example.bapplusplus.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
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
import com.example.bapplusplus.R
import com.example.bapplusplus.RestInfoTemp
import com.google.firebase.firestore.FirebaseFirestore
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
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

    override fun onCreate(savedInstanceState: Bundle?) {


        /*val bundle = arguments
        val fbdb = FirebaseFirestore.getInstance()
        RestNo = bundle?.getInt("RestNo") ?: 0
        fbdb.collection("tmp3v")
            .whereEqualTo("RestNo", RestNo)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents)
                    if (document != null) {
                        Log.d("TAG", "OOOO DocumentSnapshot data: ${document.data}")
                        RestPosx = document.getDouble("RestPosx")!!
                        RestPosy = document.getDouble("RestPosy")!!

                        println("Bn3x "+ RestPosx)
                        println("Bn3y "+ RestPosy)
                    } else {
                        Log.d("TAG", "No such document - Fragment1")
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }*/
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

        fbdb.collection("tmp3v")
            .whereEqualTo("RestNo", RestNo)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents)
                    if (document != null) {
                        Log.d("TAG", "RRRR DocumentSnapshot data: ${document.data}")
                        RestPosx = document.getDouble("RestPosx")!!
                        RestPosy = document.getDouble("RestPosy")!!
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
                        Log.d("TAG", "No such document - Fragment1")
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }


        //mapFragment.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
                var presentLoc = getLatLng()
                distanceEstimate = presentLoc.distanceTo(RestLoc).toDouble()

//                Toast.makeText(activity, distanceEstimate.toString(), Toast.LENGTH_SHORT).show()
//                var toast_test = View.inflate(requireContext(), R.layout.toast_custom_1, null)
//                var tst = Toast(requireContext())
//                tst.view = toast_test
//                toastc1_tv.text = "Distance: "+distanceEstimate.toString()
                if(distanceEstimate > 1000){
//                    toast_test.findViewById<TextView>(R.id.toastc1_tv).text = "Distance: "+Math.round((distanceEstimate/1000.0)).toString() +"km"
//                    toast_test.findViewById<TextView>(R.id.toastc1_tv).text = "Distance: "+((distanceEstimate/1000.0)*10).roundToInt() / 10f + "km"
                    bn_info_add_txt.text = "Distance: "+((distanceEstimate/1000.0)*10).roundToInt() / 10f + "km"

                }
                else{
//                    toast_test.findViewById<TextView>(R.id.toastc1_tv).text = "Distance: "+distanceEstimate.toString() +"m"
                    bn_info_add_txt.text = "Distance: "+distanceEstimate.roundToInt().toString() +"m"
                }
                bn_const_info_add.startAnimation(slideDownAndVanish3(bn_const_info_add))

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

    private fun getLatLng(): Location {
        var currentLatLng: Location? = null
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
            val locationProvider = LocationManager.GPS_PROVIDER
            currentLatLng = lm.getLastKnownLocation(locationProvider)
        }

        return currentLatLng!!
    }

    fun slideDownAndVanish3(view: View): AnimationSet {
        var animset: AnimationSet = AnimationSet(true)
//        view.visibility = View.VISIBLE
        var animate1 = TranslateAnimation(0F, 0F, -view.height.toFloat(), 0F)
        animate1.duration = 400
        animate1.fillAfter = true
//        view.startAnimation(animate1)

        var animate2 = TranslateAnimation(0F, 0F, 0F, -view.height.toFloat())
        animate2.startOffset = 3200
        animate2.duration = 400
        animate2.fillAfter = true
//        view.startAnimation(animate2)
        animset.addAnimation(animate1)
        animset.addAnimation(animate2)

        return animset
    }
}