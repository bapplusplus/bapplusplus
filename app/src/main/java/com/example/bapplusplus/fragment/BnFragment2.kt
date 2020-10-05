package com.example.bapplusplusTemp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bapplusplus.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker


class BnFragment2 : Fragment(), OnMapReadyCallback {

    private lateinit var mapView:MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView = inflater.inflate(R.layout.fragment_bn2, container, false)
        mapView = rootView.findViewById(R.id.bnf2_frame)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Inflate the layout for this fragment
        /*val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: run {
                val options = NaverMapOptions()
                    .camera(CameraPosition(LatLng(posx, posy), 14.0, 0.0, 0.0))
                    .locationButtonEnabled(true)
                MapFragment.newInstance(options).also {
                    supportFragmentManager.beginTransaction().add(R.id.map_fragment, it).commit()
                }
            }
        mapFragment.getMapAsync(this)*/




        return rootView
    }

    override fun onMapReady(naverMap: NaverMap) {

        naverMap.mapType = NaverMap.MapType.Basic
        val cameraPosition = CameraPosition(
            LatLng(37.560063, 127.041204),  // 위치 지정
            15.5,  // 줌 레벨
            0.0,  // 기울임 각도
            0.0 // 방향
        )
        naverMap.cameraPosition = cameraPosition

        naverMap.uiSettings.isLocationButtonEnabled = true


        val marker = Marker().apply {
            position = LatLng(37.560063, 127.041204)
            //setOnClickListener {
             //   infoWindow.open(this)
             //   true
            //
            tag = 10
            map = naverMap
        }
    }

    override fun onStart() {
        var addr: String
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    //fun onSaveInstanceState(outState: Bundle?) {
    //    super.onSaveInstanceState(outState!!)
    //    mapView.onSaveInstanceState(outState)
    //}

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState!!)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}