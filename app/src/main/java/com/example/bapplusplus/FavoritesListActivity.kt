package com.example.bapplusplus

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bapplusplus.data.FBUserInfo
import com.example.bapplusplus.deprecated.ShowMapActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PropertyName
import kotlinx.android.synthetic.main.activity_favorites_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


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

class FavoritesListActivity : AppCompatActivity() {
    val infoList: ArrayList<InfoTemp> = arrayListOf(
        InfoTemp(
            "삼삼구 한양대점", 4.27, "육류, 고기요리", "매일 11:00 - 22:00", 37.5594683, 127.0382745,
            "0507-1473-3394", "서울 성동구 마조로 17 1층 삼삼구", true
        ),
        InfoTemp(
            "Beta", 4.20, "육류, 고기요리", "매일 11:00 - 22:00", 37.5594700, 127.0382752,
            "0507-1473-3394", "서울 성동구 마조로 17 1층 삼삼구", false
        ),
        InfoTemp(
            "다시올치킨", 4.1, "치킨, 닭강정", "매일 07:00 - 22:00\n일요일 휴무", 37.5604271, 127.0445195,
            "02-2293-8979", "서울 성동구 사근동8길 8", false
        )
    )

    var fbfs = FirebaseFirestore.getInstance()
    var fbauth = FirebaseAuth.getInstance()
    //var favlistnu: FavListNu? = null
    //
    var listtry: ArrayList<GetNumsInfo> = arrayListOf()
    var categorySpinnerArray = arrayListOf<String>("전체 목록", "분식", "경양식", "한식", "패스트푸드/피자", "치킨/호프", "중식", "일식", "김밥/도시락","카페/찻집", "주점", "기타")
    var adapter: FavListAdapter? = null
    var categorySelectAdapter : ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites_list)
        var favlistnu: FavListNu
        val docRef = fbfs.collection("tmp5vValuesBeta").document("eb022c50-3005-11eb-bf1d-09863b642d3c")
        val docRefNew = fbfs.collection("tmp7vList").document("bf6c7ba0-537c-11eb-85d6-f96783d0ff1e")
        val ddtt = fbfs.collection("FSTestDocs").document("tt1")
        adapter = FavListAdapter(this, listtry)
        /*val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading List...")
        //progressDialog.setCancelable(true)
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.show()*/

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

        /*docRef.get()
            .addOnSuccessListener { document ->
                if(document != null){
                    var ss = document.get("ArrayBeta") as ArrayList<GetNumsInfo>
                    //println("ss" + ss.get(2).RestTitle)
                    println(ss[2])
                    listtry.clear()
                    for(kk in 0.. ss.size){
                        listtry.add(GetNumsInfo(ss[kk].RestNo, ss[kk].RestTitle, ss[kk].RestRatingAvg, ss[kk].RestReviewNum))
                    }

                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                }else{
                    Log.d("TAG", "No such document")
                }
            }*/



        /*docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                    //t2_tv_getlist.text = document.getString("RestTitle")
                    //var hhh : DocumentSnapshot? = document
                    favlistnu = document.toObject(FavListNu::class.java)!!
                    println(favlistnu!!.resultList[2].RestTitle)
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }*/

        //tmp3v tmp5v
        /*docRef.get().addOnCompleteListener{ task->
            if(task.isSuccessful){
                var docc = task.getResult()!!
                var list1 = docc.getData()!!.get("ArrayBeta") as List<*>
                for (kk in 0..list1.size-1){
                    var map1 = list1.get(kk) as HashMap<*, *>
                    var gni = GetNumsInfo(map1.get("RestNo").toString().toInt(), map1.get("RestTitle").toString(), map1.get("RestRatingAvg").toString().toDouble(), map1.get("RestReviewNum").toString().toInt())
                    listtry.add(gni)
                }
                println("listtrytest "+listtry.get(5).RestTitle + " / " + listtry.get(37).RestTitle)
//                var favlistnu2 = docc!!.toObject(FavListNu::class.java)!!
//                println("letssee0" + favlistnu2!!.resultList?.size.toString())
//                println("letssee" + favlistnu2!!.resultList!![2].RestTitle)
                adapter.notifyDataSetChanged()
                //progressDialog.dismiss()
                fav_progressbar.visibility = View.GONE

            }
        }*/

        CoroutineScope(IO).launch {
            FBUserInfo.getMyLikesArray()
        }

        //tmp7vList
        docRefNew.get().addOnCompleteListener{ task->
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
                adapter!!.notifyDataSetChanged()
                //progressDialog.dismiss()
                fav_progressbar.visibility = View.GONE

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


        val fav_toolbar = findViewById(R.id.fav_toolbar) as Toolbar
        setSupportActionBar(fav_toolbar)

        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)


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

                adapter!!.getSearchFilter().filter(categorySelectAdapter!!.getItem(p2))
                fav_recycler.scrollToPosition(0)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.ftb_maps -> {
                Toast.makeText(this, "Menu Test", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ShowMapActivity::class.java)
                intent.putExtra("infoArray", infoList)
                startActivity(intent)
//                finish()
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

        menuInflater.inflate(R.menu.fav_toolbar_menu_file, menu)

        var searchItem = menu?.findItem(R.id.ftb_search)
        searchItem!!.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            //var selpos: Int = 0
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {

                selbool = false
                selpos = fav_toolbar_spinner.selectedItemPosition
                val layoutm = fav_recycler.layoutManager as LinearLayoutManager
                selviewpos = layoutm.findFirstVisibleItemPosition()

                //println("expand selpos is: "+selpos)
                println("expand selviewpos is: "+selviewpos)
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                //fav_toolbar_spinner.setSelection(selpos)
                //adapter!!.getSearchFilter().filter(categorySpinnerArray[selpos])
                //println("collapse selpos is: "+selpos)
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
                    println("stringisnull selviewpos is: "+selviewpos)
                    return false
                }
                adapter!!.getFilter().filter(newText)
                println("still selviewpos is: "+selviewpos)
                return false
            }
        })

        return true
    }

}