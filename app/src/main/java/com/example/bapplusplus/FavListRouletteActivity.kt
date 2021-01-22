package com.example.bapplusplus

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bapplusplus.adapter.FavListRouletteAdapter
import com.example.bapplusplus.data.FBUserInfo
import com.example.bapplusplus.fragment.BottomSheetFavroItems
import com.example.bapplusplus.fragment.BottomSheetListFilter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_fav_list_roulette.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.StringBuilder

data class GetRouletteItemInfo(
    val RestNo: Int = 0,
    val RestTitle: String? = "b",
    val RestRatingAvg: Double = 1.0,
    val RestReviewNum: Int = 0,
    val RestCategory: String? = "c",
    var isChecked: Boolean = false
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(RestNo)
        parcel.writeString(RestTitle)
        parcel.writeDouble(RestRatingAvg)
        parcel.writeInt(RestReviewNum)
        parcel.writeString(RestCategory)
        parcel.writeByte(if (isChecked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetRouletteItemInfo> {
        override fun createFromParcel(parcel: Parcel): GetRouletteItemInfo {
            return GetRouletteItemInfo(parcel)
        }

        override fun newArray(size: Int): Array<GetRouletteItemInfo?> {
            return arrayOfNulls(size)
        }
    }
}

class FavListRouletteActivity : AppCompatActivity() {

    var fbfs = FirebaseFirestore.getInstance()
    var fbauth = FirebaseAuth.getInstance()
    var favroListAll: ArrayList<GetRouletteItemInfo> = arrayListOf()

    var favroCategorySpinnerArray = arrayListOf<String>("전체 목록", "분식", "경양식", "한식", "패스트푸드/피자", "치킨/호프", "중식", "일식", "김밥/도시락","카페/찻집", "주점", "기타")
    var favroAdapter: FavListRouletteAdapter? = null
    var favroCategorySelectAdapter : ArrayAdapter<String>? = null
    var bslfFilterMode = 0
    var bslfSpinnerValue = 0

    companion object {
        lateinit var favroListSet: ArrayList<GetRouletteItemInfo>
        //lateinit var bsfavro: BottomSheetFavroItems
        lateinit var tvvv : TextView

        lateinit var bstitle: TextView
        lateinit var bscontent: TextView
        lateinit var bstry: Button

        fun changeText(){
            val sb = StringBuilder()
            var tsss = "선택 목록:\n"
            if (favroListSet.isEmpty()){
                tsss = "비었음"
            }else{
                for (cc in 0..favroListSet.size-1){
                    //tsss.plus(favroListSet?.get(cc)?.RestTitle+"\n")
                    //tsss = favroListSet.get(0).RestTitle.toString()
                    sb.append(favroListSet?.get(cc)?.RestTitle+"\n")
                }
            }

            println(sb.toString())
            tvvv.text = sb.toString()

            //bsfavro.setContentText(favroListSet.count(), sb.toString())
        }

        fun changeTextNew(){
            val sb = StringBuilder()
            if (favroListSet.isEmpty()){
                sb.append("룰렛에 넣을 음식점을 추가해 주세요.")
                bstry.visibility = View.GONE
            }else{
                for (cc in 0..favroListSet.size-1){
                    //tsss.plus(favroListSet?.get(cc)?.RestTitle+"\n")
                    //tsss = favroListSet.get(0).RestTitle.toString()
                    sb.append(favroListSet?.get(cc)?.RestTitle+"\n")
                }
                bstry.visibility = View.VISIBLE
            }

            println(sb.toString())
            bscontent.text = sb.toString()
            bstitle.text = "선택한 개수 : "+ favroListSet.count().toString()


            //bsfavro.setContentText(favroListSet.count(), sb.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav_list_roulette)

        favro_appbar.visibility = View.GONE
        favro_fab.visibility = View.GONE
        favro_frame_bs.visibility = View.GONE

        val itt = intent
        //favroListAll = itt.getParcelableArrayListExtra<GetNumsInfo>("listtry") as ArrayList<GetNumsInfo>
        favroListSet = itt.getParcelableArrayListExtra("listSet") ?: arrayListOf<GetRouletteItemInfo>()

        favroAdapter = FavListRouletteAdapter(this, favroListAll, favroListSet)
        //favro_toolbar_title.text = "음식점 선택"
        tvvv = findViewById(R.id.favro_tv_test)
        bstitle = findViewById(R.id.favro_bs_title)
        bscontent = findViewById(R.id.favro_bs_content)
        bstry = findViewById(R.id.favro_bs_try)

        //setting 1
        favro_recycler.addItemDecoration(
            DividerItemDecoration(
                applicationContext,
                DividerItemDecoration.VERTICAL
            )
        )
        favro_recycler.adapter = favroAdapter
        favroAdapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY


        val favro_toolbar = findViewById(R.id.favro_toolbar) as Toolbar
        setSupportActionBar(favro_toolbar)

        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        favro_frame_bs.setOnTouchListener { view, motionEvent ->
            true
        }

        favro_bs_try.setOnClickListener {
            Toast.makeText(this, "개수: "+ favroListSet.count().toString(), Toast.LENGTH_SHORT).show()
            var ittf = Intent(this, MyFavoritesActivity::class.java)
            ittf.putParcelableArrayListExtra("sendtry", favroListSet)
            startActivity(ittf)
        }

        //ab.setHomeAsUpIndicator(R.drawable.ic_dehaze_white_24dp)

        CoroutineScope(Main).launch {
            FBUserInfo.getMyLikesArray()
            if(favroListAll.isNullOrEmpty()){
                initFavroListAll()
            }

            if(!favroListSet.isNullOrEmpty()){
                getFavroListSet()
                changeTextNew()
            }else{
                favro_progressbar.visibility = View.GONE
                favro_appbar.visibility = View.VISIBLE
                favro_fab.visibility = View.VISIBLE
                favro_frame_bs.visibility = View.VISIBLE
                favro_bs_try.visibility = View.GONE
                changeTextNew()
                favroAdapter!!.notifyDataSetChanged()
                Log.d("FAVR", "Favroset is empty")
            }
        }

        /*if(favroListAll.isNullOrEmpty()){
            CoroutineScope(IO).launch {
                initFavroListAll()
            }
        }

        CoroutineScope(IO).launch {
            FBUserInfo.getMyLikesArray()
        }

        if(!favroListSet.isNullOrEmpty()){
            CoroutineScope(IO).launch {
                getFavroListSet()
            }
        }else{
            favro_progressbar.visibility = View.GONE
            favro_appbar.visibility = View.VISIBLE
            favro_fab.visibility = View.VISIBLE
            favroAdapter!!.notifyDataSetChanged()
            Log.d("FAVR", "Favroset is empty")
        }*/

        favro_fab.setOnClickListener {
            favro_recycler.scrollToPosition(0)
        }

        favroCategorySelectAdapter = ArrayAdapter(
            this,
            R.layout.custom_spinner_item_one,
            R.id.csp_item_tv,
            favroCategorySpinnerArray
        )
        favro_toolbar_spinner.adapter = favroCategorySelectAdapter
        favro_toolbar_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //p2 is position
//                if(p2 == 0){
//                    adapter!!.getSearchFilter().filter("")
//                }else{
//                    adapter!!.getSearchFilter().filter(categorySelectAdapter!!.getItem(p2))
//                }
                favro_recycler.scrollToPosition(0)
                bslfSpinnerValue = favro_toolbar_spinner.selectedItemPosition

                CoroutineScope(Dispatchers.Main).launch {
                    favroAdapter!!.getSearchFilter().filter(favroCategorySelectAdapter!!.getItem(p2))
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
                var logoutDialog = AlertDialog.Builder(this)
                logoutDialog.setTitle("선택 취소").setMessage("만들던 목록을 취소하고 돌아가시겠습니까?")
                logoutDialog.setPositiveButton("예", DialogInterface.OnClickListener { dialogInterface, which ->
                    CoroutineScope(Dispatchers.Main).launch {
                        finish()
                    }
                })
                logoutDialog.setNegativeButton("아니오", null)
                logoutDialog.create()
                logoutDialog.show()
                return true
            }
            R.id.favro_filter->{
                //Toast.makeText(this, "Filter Clicked", Toast.LENGTH_SHORT).show()
                val bslf = BottomSheetListFilter(bslfFilterMode){ itval ->
                    when(itval){
                        0 ->{
                            //Toast.makeText(this, "기본", Toast.LENGTH_SHORT).show()
                            bslfSpinnerValue = favro_toolbar_spinner.selectedItemPosition
                            println("favroListAll 0: "+favroListAll.get(0).RestTitle)
                            favroListAll.sortBy { it.RestNo }
                            println("favroListAll 0: "+favroListAll.get(0).RestTitle)
                            favro_recycler.scrollToPosition(0)
                            favroAdapter!!.notifyDataSetChanged()
                            favroAdapter!!.getSearchFilter().filter(favroCategorySpinnerArray.get(bslfSpinnerValue))

                            bslfFilterMode = 0
                        }
                        1 ->{
                            //Toast.makeText(this, "별점 순", Toast.LENGTH_SHORT).show()
                            bslfSpinnerValue = favro_toolbar_spinner.selectedItemPosition
                            favroListAll.sortByDescending { it.RestRatingAvg }
                            favro_recycler.scrollToPosition(0)
                            favroAdapter!!.notifyDataSetChanged()
                            favroAdapter!!.getSearchFilter().filter(favroCategorySpinnerArray.get(bslfSpinnerValue))
                            bslfFilterMode = 1
                        }
                        2 ->{
                            //Toast.makeText(this, "리뷰 순", Toast.LENGTH_SHORT).show()
                            bslfSpinnerValue = favro_toolbar_spinner.selectedItemPosition
                            favroListAll.sortByDescending { it.RestReviewNum }
                            favro_recycler.scrollToPosition(0)
                            favroAdapter!!.notifyDataSetChanged()
                            favroAdapter!!.getSearchFilter().filter(favroCategorySpinnerArray.get(bslfSpinnerValue))
                            bslfFilterMode = 2
                        }
                        3 ->{
                            //Toast.makeText(this, "가나다 순", Toast.LENGTH_SHORT).show()
                            bslfSpinnerValue = favro_toolbar_spinner.selectedItemPosition
                            favroListAll.sortBy { it.RestTitle }
                            favro_recycler.scrollToPosition(0)
                            favroAdapter!!.notifyDataSetChanged()
                            favroAdapter!!.getSearchFilter().filter(favroCategorySpinnerArray.get(bslfSpinnerValue))

                            bslfFilterMode = 3
                        }
                    }
                    //adapter!!.notifyDataSetChanged()
                }
                bslf.show(supportFragmentManager, bslf.tag)
                return true
            }
            R.id.favro_reset->{
                if(favroListSet.isEmpty()){
                    favro_tv_test.text = "비어있는데?"
                    return true
                }

                for (cc in 0..favroListSet.count()-1){
                    favroListAll.find { it.RestNo == favroListSet.get(cc).RestNo}!!.isChecked = false
                }
                favroListSet.clear()
                favroAdapter!!.notifyDataSetChanged()
                changeTextNew()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        var selpos: Int = -1
        var selbool = false
        var selviewpos = -1
        var restno = -1
        var selviewpos2 = -1

        menuInflater.inflate(R.menu.favro_menu_file, menu)

        var searchItem = menu?.findItem(R.id.favro_search)
        searchItem!!.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            //var selpos: Int = 0
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {

                selbool = false
                selpos = favro_toolbar_spinner.selectedItemPosition
                val layoutm = favro_recycler.layoutManager as LinearLayoutManager
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
                    favroAdapter!!.getSearchFilter().filter(favroCategorySpinnerArray.get(selpos))
                    favro_recycler.scrollToPosition(selviewpos)
                    println("after selviewpos is: "+selviewpos)
                    return false
                }else if(newText.isNullOrEmpty()){
                    if(selpos != -1 && selviewpos != -1){
                        favroAdapter!!.getSearchFilter().filter(favroCategorySpinnerArray.get(selpos))
                        favro_recycler.scrollToPosition(selviewpos)
                        return false
                    }
                    favroAdapter!!.getFilter().filter(newText)
                    favro_recycler.scrollToPosition(0)
                    println("stringisnull selviewpos is: "+selviewpos)
                    return false
                }
                favroAdapter!!.getFilter().filter(newText)
                favro_recycler.scrollToPosition(0)
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

    suspend fun initFavroListAll(){
        favroListAll.clear()

        var docc = fbfs.collection("tmp7vList").document("bf6c7ba0-537c-11eb-85d6-f96783d0ff1e").get().await()
        var list1 = docc.getData()!!.get("tmp7vArray") as List<*>
        for (kk in 0..list1.size - 1) {
            var map1 = list1.get(kk) as HashMap<*, *>
            var gri = GetRouletteItemInfo(
                map1.get("RestNo").toString().toInt(),
                map1.get("RestTitle").toString(),
                map1.get("RestRatingAvg").toString().toDouble(),
                map1.get("RestReviewNum").toString().toInt(),
                map1.get("RestCategory").toString(),
                false
            )
            favroListAll.add(gri)
        }
        favroListAll.sortBy { it.RestNo }


        //favroListAll.find { it.RestNo == favroListSet.get(1).RestNo }!!.isChecked = true

        //favroAdapter!!.notifyDataSetChanged()
    }

    suspend fun getFavroListSet(){
        for(cc in 0..favroListSet!!.count()-1){
            favroListAll.find { it.RestNo == favroListSet!!.get(cc).RestNo }?.isChecked = true
        }
        favroAdapter!!.notifyDataSetChanged()
        favro_progressbar.visibility = View.GONE
        favro_appbar.visibility = View.VISIBLE
        favro_fab.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        var logoutDialog = AlertDialog.Builder(this)
        logoutDialog.setTitle("선택 취소").setMessage("만들던 목록을 취소하고 돌아가시겠습니까?")
        logoutDialog.setPositiveButton("예", DialogInterface.OnClickListener { dialogInterface, which ->
            CoroutineScope(Dispatchers.Main).launch {
                finish()
            }
        })
        logoutDialog.setNegativeButton("아니오", null)
        logoutDialog.create()
        logoutDialog.show()

        //super.onBackPressed()
    }


}