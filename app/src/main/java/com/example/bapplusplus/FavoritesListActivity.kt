package com.example.bapplusplus

import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bapplusplus.data.FBUserInfo
import com.example.bapplusplus.deprecated.ShowMapActivity
import com.example.bapplusplus.fragment.BottomSheetListFilter
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PropertyName
import kotlinx.android.synthetic.main.activity_favorites_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.io.Serializable


data class GetNumsInfo(
    @PropertyName("RestNo") val RestNo: Int = 0,
    @PropertyName("RestTitle") val RestTitle: String? = "b",
    @PropertyName("RestRatingAvg") val RestRatingAvg: Double = 1.0,
    @PropertyName("RestReviewNum") val RestReviewNum: Int = 0,
    @PropertyName("RestCategory") val RestCategory: String? = "c"
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(RestNo)
        parcel.writeString(RestTitle)
        parcel.writeDouble(RestRatingAvg)
        parcel.writeInt(RestReviewNum)
        parcel.writeString(RestCategory)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetNumsInfo> {
        override fun createFromParcel(parcel: Parcel): GetNumsInfo {
            return GetNumsInfo(parcel)
        }

        override fun newArray(size: Int): Array<GetNumsInfo?> {
            return arrayOfNulls(size)
        }
    }
}

data class FavListNu(
    var resultList: ArrayList<GetNumsInfo>? = null
)



class FavoritesListActivity() : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    companion object{
        var isrmode = false

        @JvmName("setIsrmode1")
        fun setIsrmode(b: Boolean){
            isrmode = b
        }
    }

    var fbfs = FirebaseFirestore.getInstance()
    var fbauth = FirebaseAuth.getInstance()
    //var favlistnu: FavListNu? = null
    //
    var listtry: ArrayList<GetNumsInfo> = arrayListOf()
    var categorySpinnerArray = arrayListOf<String>("전체 목록", "분식", "경양식", "한식", "패스트푸드/피자", "치킨/호프", "중식", "일식", "김밥/도시락","카페/찻집", "주점", "기타")
    var adapter: FavListAdapter? = null
    var categorySelectAdapter : ArrayAdapter<String>? = null
    var mDrawerLayout: DrawerLayout? = null
    var docRefNew: DocumentReference? = null

    var bslfFilterMode = 0
    var bslfSpinnerValue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites_list)
        var favlistnu: FavListNu
        val docRef = fbfs.collection("tmp5vValuesBeta").document("eb022c50-3005-11eb-bf1d-09863b642d3c")
        docRefNew = fbfs.collection("tmp7vList").document("bf6c7ba0-537c-11eb-85d6-f96783d0ff1e")
        val ddtt = fbfs.collection("FSTestDocs").document("tt1")
        adapter = FavListAdapter(this, listtry)
        /*val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading List...")
        //progressDialog.setCancelable(true)
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.show()*/
        fav_appbar.visibility = View.GONE
        fav_fab.visibility = View.GONE



        ddtt.get()
            .addOnSuccessListener { document ->
               if(document != null){
                   var ss = document.get("array") as ArrayList<String>
                   println("ss" + ss[2])
                   Log.d("TAG", "DocumentSnapshot data: ${document.data}")
               }else{
                   Log.d("TAG", "No such document")
               }
            }

        CoroutineScope(IO).launch {

            FBUserInfo.getMyLikesArray()
        }



        //tmp7vList
        docRefNew?.get()?.addOnCompleteListener{ task->
            if(task.isSuccessful){
                var docc = task.getResult()!!
                var list1 = docc.getData()!!.get("tmp7vArray") as List<*>
                for (kk in 0..list1.size-1){
                    var map1 = list1.get(kk) as HashMap<*, *>
                    var gni = GetNumsInfo(
                        map1.get("RestNo").toString().toInt(),
                        map1.get("RestTitle").toString(),
                        map1.get("RestRatingAvg").toString().toDouble(),
                        map1.get("RestReviewNum").toString().toInt(),
                        map1.get("RestCategory").toString()
                    )

                    listtry.add(gni)
                }
                //listtry.sortedBy { it.RestNo } //as ArrayList<GetNumsInfo>
                listtry.sortBy { it.RestNo }

                println("listtrytest " + listtry.get(0).RestTitle + " / " + listtry.get(1).RestTitle)
//                var favlistnu2 = docc!!.toObject(FavListNu::class.java)!!
//                println("letssee0" + favlistnu2!!.resultList?.size.toString())
//                println("letssee" + favlistnu2!!.resultList!![2].RestTitle)

                fav_progressbar.visibility = View.GONE
                fav_appbar.visibility = View.VISIBLE
                fav_fab.visibility = View.VISIBLE
                adapter!!.notifyDataSetChanged()
                //progressDialog.dismiss()
                if(savedInstanceState!= null){
                    //fav_toolbar_spinner.setSelection(9)
                }

            }
        }


        //println("listtrytestnew "+listtry.get(5).RestTitle + " / " + listtry.get(37).RestTitle)

        //val adapter = favlistnu?.let { FavListAdapter(this, infoList, it) }
        fav_recycler.addItemDecoration(
            DividerItemDecoration(
                applicationContext,
                DividerItemDecoration.VERTICAL
            )
        )
        fav_recycler.adapter = adapter
        adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY


        val fav_toolbar = findViewById(R.id.fav_toolbar) as Toolbar
        setSupportActionBar(fav_toolbar)

        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.ic_dehaze_white_24dp)

        mDrawerLayout = findViewById(R.id.fav_drawer)
        fav_navi.setNavigationItemSelectedListener(this)

        var fav_navi_view = fav_navi.getHeaderView(0)
        fav_navi_view.findViewById<TextView>(R.id.navihead_title).text = FBUserInfo.fbauth.currentUser?.displayName ?: "로그인되지 않음"
        fav_navi_view.findViewById<TextView>(R.id.navihead_subtitle).text = FBUserInfo.fbauth.currentUser?.email ?: "Guest"
        fav_navi_view.findViewById<ImageButton>(R.id.navihead_btn_settings).setOnClickListener {

            val itts = Intent(this, MyInfoActivity::class.java)
            startActivity(itts)
            Handler(Looper.getMainLooper()).postDelayed({
                mDrawerLayout!!.closeDrawers()
            }, 400)

        }

        fav_fab.setOnClickListener {
            fav_recycler.scrollToPosition(0)
        }


        //var categorySelectAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorySpinnerArray)
        categorySelectAdapter = ArrayAdapter(
            this,
            R.layout.custom_spinner_item_one,
            R.id.csp_item_tv,
            categorySpinnerArray
        )
        fav_toolbar_spinner.adapter = categorySelectAdapter
        fav_toolbar_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //p2 is position
//                if(p2 == 0){
//                    adapter!!.getSearchFilter().filter("")
//                }else{
//                    adapter!!.getSearchFilter().filter(categorySelectAdapter!!.getItem(p2))
//                }
                fav_recycler.scrollToPosition(0)
                bslfSpinnerValue = fav_toolbar_spinner.selectedItemPosition

                CoroutineScope(Main).launch {
                    adapter!!.getSearchFilter().filter(categorySelectAdapter!!.getItem(p2))
                }


            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                //finish()
                mDrawerLayout!!.openDrawer(GravityCompat.START)
                return true
            }
            R.id.ftb_filter->{
                //Toast.makeText(this, "Filter Clicked", Toast.LENGTH_SHORT).show()
                val bslf = BottomSheetListFilter(bslfFilterMode){ itval ->
                    when(itval){
                        0 ->{
                            //Toast.makeText(this, "기본", Toast.LENGTH_SHORT).show()
                            bslfSpinnerValue = fav_toolbar_spinner.selectedItemPosition
                            println("listtry 0: "+listtry.get(0).RestTitle)
                            listtry.sortBy { it.RestNo }
                            println("listtry 0: "+listtry.get(0).RestTitle)
                            fav_recycler.scrollToPosition(0)
                            adapter!!.notifyDataSetChanged()
                            adapter!!.getSearchFilter().filter(categorySpinnerArray.get(bslfSpinnerValue))

                            bslfFilterMode = 0
                        }
                        1 ->{
                            //Toast.makeText(this, "별점 순", Toast.LENGTH_SHORT).show()
                            bslfSpinnerValue = fav_toolbar_spinner.selectedItemPosition
                            listtry.sortByDescending { it.RestRatingAvg }
                            fav_recycler.scrollToPosition(0)
                            adapter!!.notifyDataSetChanged()
                            adapter!!.getSearchFilter().filter(categorySpinnerArray.get(bslfSpinnerValue))
                            bslfFilterMode = 1
                        }
                        2 ->{
                            //Toast.makeText(this, "리뷰 순", Toast.LENGTH_SHORT).show()
                            bslfSpinnerValue = fav_toolbar_spinner.selectedItemPosition
                            listtry.sortByDescending { it.RestReviewNum }
                            fav_recycler.scrollToPosition(0)
                            adapter!!.notifyDataSetChanged()
                            adapter!!.getSearchFilter().filter(categorySpinnerArray.get(bslfSpinnerValue))
                            bslfFilterMode = 2
                        }
                        3 ->{
                            //Toast.makeText(this, "가나다 순", Toast.LENGTH_SHORT).show()
                            bslfSpinnerValue = fav_toolbar_spinner.selectedItemPosition
                            listtry.sortBy { it.RestTitle }
                            fav_recycler.scrollToPosition(0)
                            adapter!!.notifyDataSetChanged()
                            adapter!!.getSearchFilter().filter(categorySpinnerArray.get(bslfSpinnerValue))

                            bslfFilterMode = 3
                        }
                    }
                    //adapter!!.notifyDataSetChanged()
                }
                bslf.show(supportFragmentManager, bslf.tag)
                return true
            }
            R.id.ftb_refresh->{
                listRefresh()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if(mDrawerLayout!!.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout!!.closeDrawers()
        }else{
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        var selpos: Int = -1
        var selbool = false
        var selviewpos = -1
        var restno = -1
        var selviewpos2 = -1

        menuInflater.inflate(R.menu.fav_toolbar_menu_file, menu)

        var searchItem = menu?.findItem(R.id.ftb_search)
        searchItem!!.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            //var selpos: Int = 0
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {

                selbool = false
                selpos = fav_toolbar_spinner.selectedItemPosition
                val layoutm = fav_recycler.layoutManager as LinearLayoutManager
                var selviewpos = layoutm.findFirstVisibleItemPosition()

                println("expand selpos is: "+selpos)
                println("expand selviewpos is: "+selviewpos)
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                /////fav_toolbar_spinner.setSelection(selpos)
                /////adapter!!.getSearchFilter().filter(categorySpinnerArray[selpos])
                println("collapse selpos is: "+selpos)
                selbool = true
                return true
            }

        })

        var scv = searchItem?.actionView as SearchView?
        scv?.imeOptions = EditorInfo.IME_ACTION_DONE

        val txtSearch = scv!!.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        txtSearch.setTextColor(Color.WHITE)

        scv!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if(selbool == true){
                    adapter!!.getSearchFilter().filter(categorySpinnerArray.get(selpos))
                    fav_recycler.scrollToPosition(selviewpos)
                    println("after selviewpos is: "+selviewpos)
                    return false
                }else if(newText.isNullOrEmpty()){
                    if(selpos != -1 && selviewpos != -1){
                        adapter!!.getSearchFilter().filter(categorySpinnerArray.get(selpos))
                        fav_recycler.scrollToPosition(selviewpos)
                        return false
                    }
                    adapter!!.getFilter().filter(newText)
                    fav_recycler.scrollToPosition(0)
                    println("stringisnull selviewpos is: "+selviewpos)
                    return false
                }
                adapter!!.getFilter().filter(newText)
                fav_recycler.scrollToPosition(0)
                println("still selviewpos is: "+selviewpos)
                return false
            }

            /*override fun onQueryTextChange(newText: String): Boolean {
                if(newText.isNullOrEmpty()){

                }
                adapter!!.getFilter().filter(newText)
                //println("still selviewpos is: "+selviewpos)
                return false
            }*/
        })

        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navimenu_one->{
                //Main
                //this.finish()
                val itt = Intent(this, MainActivity::class.java)
                startActivity(itt)
            }
            R.id.navimenu_two->{
                //Roulette
                //startActivity to Roulette
            }
            R.id.navimenu_three->{
                //MyLike
                //Toast.makeText(this, "Menu3", Toast.LENGTH_SHORT).show()
                if(FBUserInfo.fbauth.currentUser == null){
                    Toast.makeText(this, "로그인 후 이용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    this.finish()
                    val itt = Intent(this, MyFavoritesActivity::class.java)
                    startActivity(itt)
                }

            }
            R.id.navimenu_four->{
                //Toast.makeText(this, "Menu4", Toast.LENGTH_SHORT).show()
                //search list == this
                mDrawerLayout!!.closeDrawers()
            }
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val selitempos = fav_toolbar_spinner.selectedItemPosition
        val selrecpos = (fav_recycler.layoutManager!! as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        //outState.putInt("sel_spinner_pos", selitempos)
        //outState.putInt("sel_item_pos", selrecpos)
    }

    fun listRefresh() {

        listtry.clear()
        adapter!!.notifyDataSetChanged()
        fav_progressbar.visibility = View.VISIBLE
        fav_recycler.visibility = View.INVISIBLE

        docRefNew!!.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var docc = task.getResult()!!
                var list1 = docc.getData()!!.get("tmp7vArray") as List<*>
                for (kk in 0..list1.size - 1) {
                    var map1 = list1.get(kk) as HashMap<*, *>
                    var gni = GetNumsInfo(
                        map1.get("RestNo").toString().toInt(),
                        map1.get("RestTitle").toString(),
                        map1.get("RestRatingAvg").toString().toDouble(),
                        map1.get("RestReviewNum").toString().toInt(),
                        map1.get("RestCategory").toString()
                    )

                    listtry.add(gni)
                }
                //listtry.sortedBy { it.RestNo } //as ArrayList<GetNumsInfo>
                listtry.sortBy { it.RestNo }

                println("listtrytest " + listtry.get(0).RestTitle + " / " + listtry.get(1).RestTitle)
//                var favlistnu2 = docc!!.toObject(FavListNu::class.java)!!
//                println("letssee0" + favlistnu2!!.resultList?.size.toString())
//                println("letssee" + favlistnu2!!.resultList!![2].RestTitle)

                fav_progressbar.visibility = View.GONE
                //fav_appbar.visibility = View.VISIBLE
                fav_recycler.visibility = View.VISIBLE
                fav_recycler.scrollToPosition(0)
                adapter!!.notifyDataSetChanged()
                adapter!!.getSearchFilter().filter(categorySpinnerArray.get(bslfSpinnerValue))
            }
        }
    }

}