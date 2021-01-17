package com.example.bapplusplus

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.bapplusplus.data.FBUserInfo
import com.example.bapplusplus.data.HangulUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.favlistnu_cell.view.*
import java.util.*
import kotlin.collections.ArrayList

data class InfoTemp(
    val store_title: String?,
    val store_popularity: Double,
    val store_category: String?,
    val store_time: String?,
    val pos_x: Double,
    val pos_y: Double,
    val call_num: String?,
    val street_address: String?,
    val delivery_able: Boolean,

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(store_title)
        parcel.writeDouble(store_popularity)
        parcel.writeString(store_category)
        parcel.writeString(store_time)
        parcel.writeDouble(pos_x)
        parcel.writeDouble(pos_y)
        parcel.writeString(call_num)
        parcel.writeString(street_address)
        parcel.writeByte(if (delivery_able) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InfoTemp> {
        override fun createFromParcel(parcel: Parcel): InfoTemp {
            return InfoTemp(parcel)
        }

        override fun newArray(size: Int): Array<InfoTemp?> {
            return arrayOfNulls(size)
        }
    }
}



class FavListAdapter(val context: Context, val gni: ArrayList<GetNumsInfo>) : RecyclerView.Adapter<FavListAdapter.Holder>(), Filterable{

    var fbfs = FirebaseFirestore.getInstance()
    var fbauth = FirebaseAuth.getInstance()
    //var fvlist = FavListNu(arrayListOf())
    val unFilteredList = gni
    var filteredList = gni

    //private var selectedItems: SparseBooleanArray = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        //println(listnu.resultList[10].RestTitle + "ttrs")
        val view = LayoutInflater.from(context).inflate(R.layout.favlistnu_cell, parent, false)
        //println("listtrytest in recycler"+gni.get(5).RestTitle + " / " + gni.get(37).RestTitle)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        //val item = infoList[position]
        val gni_item = filteredList[position]
        val fbdb = FirebaseFirestore.getInstance()
        val listener = View.OnClickListener { it->
            val intent = Intent(context, BottomNaviActivity::class.java)
            var pppx = 9.8
            var pppy = 7.3

            fbdb.collection("tmp3v")
                .whereEqualTo("RestNo", gni_item.RestNo)
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents)
                        if (document != null) {
                            Log.d("TAG", "FFFF DocumentSnapshot data: ${document.data}")
                            pppx = document.get("RestPosx") as Double
                            pppy = document.get("RestPosy") as Double
                            println("FFFF pppx "+pppx)
                            println("FFFF pppy "+pppy)
                            intent.putExtra("pppx", pppx)
                            intent.putExtra("pppy", pppy)
                        } else {
                            Log.d("TAG", "No such document")
                        }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }




            //intent.putExtra("posx", infoList[position].pos_x)
            //intent.putExtra("posy", infoList[position].pos_y)
            //intent.putExtra("infoList", infoList[position])
            intent.putExtra("gni_num", gni_item.RestNo)
            intent.putExtra("gni_title", gni_item.RestTitle)
            intent.putExtra("gni_category", gni_item.RestCategory)
            if(FBUserInfo.myLikesArray.contains(gni_item.RestNo)){
                intent.putExtra("gni_my_like", true)
            }
            intent.putExtra("gni_rating_avg", gni_item.RestRatingAvg)
            intent.putExtra("gni_review_num", gni_item.RestReviewNum)
            intent.putExtra("BottomNaviNum", 1)
            context.startActivity(intent)
            //Handler(Looper.getMainLooper()).postDelayed(Runnable { context.startActivity(intent) }, 10)


        }
        holder.apply {
            bind(listener, gni_item)
            itemView.tag = gni_item
        }

    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val container = itemView.findViewById<ConstraintLayout>(R.id.favlistnu_item_const)
        val title_f = itemView.findViewById<TextView>(R.id.fnc_item_title)
        val category_f = itemView.findViewById<TextView>(R.id.fnc_item_category)
        val star_f = itemView.findViewById<TextView>(R.id.fnc_item_star)
        val callnum_f = itemView.findViewById<TextView>(R.id.fnc_item_expand_call)


        fun bind(listener: View.OnClickListener, gni: GetNumsInfo){
            title_f.text = gni.RestTitle
            category_f.text = gni.RestCategory
            star_f.text = gni.RestRatingAvg.toString()
            callnum_f.text = "Callnot not supported"
            itemView.fnc_tv_reviewnum.text = "리뷰 "+gni.RestReviewNum.toString()+"건"

            if(FBUserInfo.myLikesArray.contains(gni.RestNo)){
                itemView.fnc_img_mylike.visibility = View.VISIBLE
            }else{
                itemView.fnc_img_mylike.visibility = View.GONE
            }

            container.setOnClickListener(listener)
        }
    }

    fun getSearchFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charString = p0.toString()
                filteredList = if (charString=="전체 목록") { //⑶
                    unFilteredList
                } else {
                    var filteringList = ArrayList<GetNumsInfo>()
                    for (item in unFilteredList) {
                        when(charString){
                            "분식", "일식", "중식", "한식", "카페/찻집", "경양식"->{
                                if(item.RestCategory == charString){
                                    filteringList.add(item)
                                }
                            }
                            "김밥/도시락"->{
                                if(item.RestCategory!!.contains("김밥")){
                                    filteringList.add(item)
                                }
                            }
                            "패스트푸드/피자"->{
                                if(item.RestCategory!!.contains("피") || item.RestCategory!!.contains("패스")){
                                    filteringList.add(item)
                                }
                            }
                            "치킨/호프"->{
                                if(item.RestCategory!!.contains("치킨")){
                                    filteringList.add(item)
                                }
                            }
                            "주점"->{
                                if(item.RestCategory!!.contains("대포")){
                                    filteringList.add(item)
                                }
                            }
                            "기타"->{
                                if(item.RestCategory!!.contains("기타") || item.RestCategory!!.contains("외국") || item.RestCategory!!.contains("레스토랑")){
                                    filteringList.add(item)
                                }
                            }
                        }
//                        if(item.RestCategory == charString){
//                            filteringList.add(item)
//                        }
                    }
                    filteringList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                filteredList = p1?.values as ArrayList<GetNumsInfo>
                notifyDataSetChanged()
            }
        }
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                var charString = p0.toString()
                charString = charString.toLowerCase(Locale.getDefault())
                filteredList = if(charString.isEmpty()) { //⑶
                    println("getfilter charstring is empty")
                    unFilteredList
                }else {
                    var filteringList = ArrayList<GetNumsInfo>()
                    for(item in unFilteredList) {
                        var iniStr = HangulUtils.getHangulInitialSound(item.RestTitle, charString)

                        //if(item.RestTitle?.contains(charString) == true){
                        if(iniStr.indexOf(charString) >=0){
                            filteringList.add(item)
                        }else if(item.RestTitle?.toLowerCase(Locale.getDefault())!!.contains(charString) == true){
                            filteringList.add(item)
                        }
                    }
                    println("getfilter new filteringlist")
                    filteringList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                filteredList = p1?.values as ArrayList<GetNumsInfo>
                notifyDataSetChanged()
            }
        }
    }

    fun getRestNoByPosition(position: Int): Int {
        return gni.get(position).RestNo
    }

    fun getPositionbyRestNo(restNo: Int): Int{
        for(ii in 0 until gni.size){
            if(gni.get(ii).RestNo == restNo){
                return ii
            }
        }
        return -1
    }

    fun getItemByRestNo(restNo: Int): GetNumsInfo? {
        for(item in gni){
            if(item.RestNo == restNo){
                return item
            }
        }
        return null
    }
}