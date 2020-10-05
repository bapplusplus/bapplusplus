package com.example.bapplusplus

import android.content.Context
import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker

class ShowMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private class InfoWindowAdapter(private val context: Context) : InfoWindow.ViewAdapter() {
        private var rootView: View? = null
        private var icon: ImageView? = null
        private var text_title: TextView? = null
        private var text_desc: TextView? = null
        private var kind = ""
        private var name = ""

        fun getExtraInfo(kind: String, name:String){
            this.kind = kind
            this.name = name
        }

        override fun getView(infoWindow: InfoWindow): View {
            val view = rootView ?: View.inflate(context, R.layout.view_custom_info_window, null).also { rootView = it }
            val icon = icon ?: view.findViewById<ImageView>(R.id.marker_icon).also { icon = it }
            val text = text_title ?: view.findViewById<TextView>(R.id.marker_tv_title).also { text_title = it }
            val textd = text_desc ?: view.findViewById<TextView>(R.id.marker_tv_desc).also {text_desc = it }

            val marker = infoWindow.marker
            if (marker != null) {
                icon.setImageResource(R.drawable.ic_place_black_24dp)
//                text.text = marker.tag as String?
                text.text = name
                textd.text = kind

            } else {
                icon.setImageResource(R.drawable.ic_my_location_black_24dp)
                text.text = context.getString(R.string.format_coord, infoWindow.position.latitude, infoWindow.position.longitude)
            }

            return view
        }
    }

//    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_map)

//        var intent: Intent = getIntent()
        var posx: Double
        var posy: Double
//        posx = intent.getDoubleExtra("posx", 37.000000)
//        posy = intent.getDoubleExtra("posy", 127.000000)


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_frame) as MapFragment?
            ?: run {
                val options = NaverMapOptions()
                    .camera(CameraPosition(LatLng(37.560063, 127.041204), 15.5, 0.0, 0.0))
                    .locationButtonEnabled(true)
                MapFragment.newInstance(options).also {
                    supportFragmentManager.beginTransaction().add(R.id.map_frame, it).commit()
                }
            }
        mapFragment.getMapAsync(this)

//        val toolbar = findViewById(R.id.showmap_toolbar) as Toolbar
//        setSupportActionBar(toolbar)
//
//        val ab = supportActionBar!!
//        ab.setDisplayShowTitleEnabled(false)
//        ab.setDisplayHomeAsUpEnabled(true)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        when (id) {
//            android.R.id.home -> {
//                finish()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onMapReady(naverMap: NaverMap) {
        var adap = InfoWindowAdapter(this@ShowMapActivity)
        adap.getExtraInfo("Description", "Name")
        val infoWindow = InfoWindow().apply {
            anchor = PointF(0f, 1f)
            offsetX = resources.getDimensionPixelSize(R.dimen.custom_info_window_offset_x_ver2)
            offsetY = resources.getDimensionPixelSize(R.dimen.custom_info_window_offset_y_ver2)
            adapter = adap

            setOnClickListener {
                close()
                true
            }
        }


        val marker = Marker().apply {
//            position = LatLng(intent.getDoubleExtra("posx", 37.000000), intent.getDoubleExtra("posy", 127.000000))
            position = LatLng(37.560063, 127.041204)
            setOnClickListener {
                infoWindow.open(this)
                true
            }
            tag = 10
            map = naverMap
        }


        infoWindow.open(marker)

        naverMap.setOnMapClickListener { _, coord ->
            infoWindow.position = coord
            infoWindow.open(naverMap)
        }

    }
}