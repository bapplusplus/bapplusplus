package com.example.bapplusplus.fragment

import android.content.Intent
import android.os.*
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.bapplusplus.*
import com.example.bapplusplus.data.FBUserInfo
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_bottom_navi.*
import kotlinx.android.synthetic.main.activity_my_favorites.*
import kotlinx.android.synthetic.main.fragment_myfav_first.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

data class MyFav_Data(
    var restNo: Int,
    var restCategory: String? = "",
    var restTitle: String? = "",
    var restCallNum: String? = "",
    var restAddressOld: String? = "",
    var restAddressRoad: String? = "",
    var restOrderTime: String? = "",
    var restPosx: Double,
    var restPosy: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(restNo)
        parcel.writeString(restCategory)
        parcel.writeString(restTitle)
        parcel.writeString(restCallNum)
        parcel.writeString(restAddressOld)
        parcel.writeString(restAddressRoad)
        parcel.writeString(restOrderTime)
        parcel.writeDouble(restPosx)
        parcel.writeDouble(restPosy)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyFav_Data> {
        override fun createFromParcel(parcel: Parcel): MyFav_Data {
            return MyFav_Data(parcel)
        }

        override fun newArray(size: Int): Array<MyFav_Data?> {
            return arrayOfNulls(size)
        }
    }
}

class MyFavFirstFragment : Fragment() {

    var myFavNewArray = arrayListOf<MyFav_Data>()
    var fbdb = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootview = inflater.inflate(R.layout.fragment_myfav_first, container, false)
        CoroutineScope(IO).launch {
            FBUserInfo.getMyLikesArray()
            getFavsData(FBUserInfo.fbauth.currentUser!!.uid)
            withContext(Main){
                mff_tv_likenum.text = myFavNewArray.count().toString()+"개"
            }
        }



        val ab = (requireActivity() as MyFavoritesActivity).supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.ic_dehaze_white_24dp)
        ab!!.setDisplayHomeAsUpEnabled(true)

        requireActivity().myfav_toolbar_title.text = "내 좋아요 목록"

        var bn_navi_view = requireActivity().mff_navi.getHeaderView(0)
        bn_navi_view.findViewById<TextView>(R.id.navihead_title).text = FBUserInfo.fbauth.currentUser?.displayName ?: "로그인되지 않음"
        bn_navi_view.findViewById<TextView>(R.id.navihead_subtitle).text = FBUserInfo.fbauth.currentUser?.email ?: "Guest"
        bn_navi_view.findViewById<ImageButton>(R.id.navihead_btn_settings).setOnClickListener {
            val itts = Intent(requireContext(), MyInfoActivity::class.java)
            startActivity(itts)
            Handler(Looper.getMainLooper()).postDelayed({
                requireActivity()!!.mafav_drawer.closeDrawers()
            }, 400)
        }

        requireActivity().mff_navi.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navimenu_one->{
                    requireActivity().finish()
                    val itt = Intent(requireContext(), MainActivity::class.java)
                    startActivity(itt)
//                val itt = Intent(this, MainActivity::class.java)
//                startActivity(itt)
                }
                R.id.navimenu_two->{
                    //Roulette
                    val intent = Intent(requireContext(), Roulette::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
                R.id.navimenu_three->{
                    //MyFavorites
                    //Do Nothing
                    requireActivity().mafav_drawer.closeDrawers()
                }
                R.id.navimenu_four->{
                    //Search
                    requireActivity().finish()
                    val itt = Intent(requireContext(), FavoritesListActivity::class.java)
                    itt.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(itt)
                }
            }
            false
        }


        return rootview
    }

    companion object {

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.myfav_menu_file, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            android.R.id.home->{
                //requireActivity().onBackPressed()
                requireActivity().mafav_drawer.openDrawer(GravityCompat.START)
            }
            R.id.mafav_menu_map->{
                if(FBUserInfo.myLikesArray.isEmpty()){
                    Toast.makeText(requireContext(), "좋아요를 추가 후 이용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    var ftr = requireActivity().supportFragmentManager.beginTransaction()
                    var myfavmapfrag = MyFavMapFragment()
                    var mbb = Bundle()
                    mbb.putString("sendtry", "Sent?")
                    mbb.putParcelableArrayList("get_array", myFavNewArray)
                    myfavmapfrag.arguments = mbb
                    ftr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    ftr.replace(R.id.myfav_frame, myfavmapfrag)
                    ftr.addToBackStack(null)
                    ftr.commit()
                }
            }
            R.id.mafav_menu_set->{
                var ittfav = Intent(requireContext(), MyInfoActivity::class.java)
                ittfav.putExtra("Mode", 100)
                startActivityForResult(ittfav, 13)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(resultCode){
            13->refreshFragment()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun refreshFragment() {
        CoroutineScope(IO).launch {
            FBUserInfo.getMyLikesArray()
            var ft: FragmentTransaction = requireActivity()!!.supportFragmentManager.beginTransaction()
            ft.detach(MyFavFirstFragment()).attach(MyFavFirstFragment()).commit()
        }
    }

    suspend fun getFavsData(uid: String): Boolean{
        try{
            myFavNewArray.clear()
            val doca = fbdb!!.collection("AccountGroup").document(uid).get().await()
            var listGetter = doca.data!!.get("MyFavoritesArray") as List<*>

            if(listGetter.isEmpty()){
                return true
            }

            for(cc in 0..listGetter.size-1){
                var doc = listGetter.get(cc) as HashMap<*, *>
                var map1 = MyFav_Data(
                    (doc.get("RestNo") as Long).toInt(), doc.get("RestCategory").toString(), doc.get("RestTitle").toString(),
                    doc.get("RestCallNum").toString(), doc.get("RestAddressOld").toString(), doc.get("RestAddressRoad").toString(),
                    doc.get("RestOrderTime").toString(), doc.get("RestPosx") as Double, doc.get("RestPosy") as Double)
                myFavNewArray.add(map1)
            }

            return true
        }catch (e: Exception){
            Log.e("MyFav", "Likearray fail: $uid, "+e.printStackTrace()+", "+e.localizedMessage)
            return false
        }
    }


}