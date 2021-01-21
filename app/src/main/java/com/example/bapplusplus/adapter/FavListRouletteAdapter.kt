package com.example.bapplusplus.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bapplusplus.FavListRouletteActivity
import com.example.bapplusplus.GetNumsInfo
import com.example.bapplusplus.GetRouletteItemInfo
import com.example.bapplusplus.R
import com.example.bapplusplus.data.FBUserInfo
import com.example.bapplusplus.data.HangulUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.favlistnu_cell.view.*
import kotlinx.android.synthetic.main.favlistroulette_cell.view.*
import java.util.*
import kotlin.collections.ArrayList

class FavListRouletteAdapter(val context: Context, var gni: ArrayList<GetRouletteItemInfo>, var set: ArrayList<GetRouletteItemInfo>?) : RecyclerView.Adapter<FavListRouletteAdapter.Holder>(), Filterable {

    var fbfs = FirebaseFirestore.getInstance()
    var fbauth = FirebaseAuth.getInstance()
    //var fvlist = FavListNu(arrayListOf())
    val unFilteredList = gni
    var filteredList = gni

    //private var selectedItems: SparseBooleanArray = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.favlistroulette_cell, parent, false)

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        //val item = infoList[position]
        val gni_item = filteredList[position]

        holder.apply {
            bind(gni_item)
            itemView.tag = gni_item
        }

    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        //val container = itemView.findViewById<ConstraintLayout>(R.id.favlistnu_item_const)


        fun bind(gni: GetRouletteItemInfo){

            //itemView.favlistroulette_item_const
            itemView.favroc_item_title.text = gni.RestTitle
            itemView.favroc_item_category.text = gni.RestCategory
            itemView.favroc_item_star.text = gni.RestRatingAvg.toString()
            itemView.favroc_tv_reviewnum.text = "리뷰 "+gni.RestReviewNum.toString()+"건"

            if(FBUserInfo.myLikesArray.contains(gni.RestNo)){
                itemView.favroc_img_mylike.visibility = View.VISIBLE
            }else{
                itemView.favroc_img_mylike.visibility = View.GONE
            }

            itemView.favroc_img_represent.setImageResource(
                when(gni.RestCategory){
                    "한식"-> R.drawable.korean_240_cut
                    "일식"-> R.drawable.sushi_240_cut
                    "중식"-> R.drawable.chinese_240_n
                    "분식"-> R.drawable.toppokki_240_cut
                    "카페/찻집"-> R.drawable.coffee_240_cut
                    "경양식"-> R.drawable.cutlet_240_cut
                    "김밥(도시락)"-> R.drawable.gimbap_240_cut
                    "피자"-> R.drawable.pizza_240_cut
                    "패스트푸드"-> R.drawable.burger_240_cut
                    "치킨"-> R.drawable.chicken_240_cut
                    "호프/치킨"-> R.drawable.hof_240_cut
                    "정종/대포집/소주방"-> R.drawable.glass_240_cut
                    "패밀리레스토랑"-> R.drawable.spaghetti_240_cut
                    else-> R.drawable.pizza_240_cut
                }
            )

            if(gni.isChecked){
                itemView.favroc_check.isChecked = true
                itemView.favlistroulette_item_const.setBackgroundColor(ContextCompat.getColor(context, R.color.colorYellow1))
            }else{
                itemView.favroc_check.isChecked = false
                itemView.favlistroulette_item_const.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite1))
            }

            itemView.favlistroulette_item_const.setOnClickListener {
                if(gni.isChecked){
                    gni.isChecked = false
                    notifyItemChanged(position)
                    set!!.remove(gni)
                    //FavListRouletteActivity.favroListSet!!.remove(gni)
                    FavListRouletteActivity.changeText()
                    println("favrolistset size: "+FavListRouletteActivity.favroListSet.size.toString())

                    //itemView.favroc_check.isChecked = false
                    //itemView.favlistroulette_item_const.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite1))
                }else{
                    gni.isChecked = true
                    notifyItemChanged(position)
                    set?.add(gni)
                    //FavListRouletteActivity.favroListSet!!.add(gni)
                    FavListRouletteActivity.changeText()
                    println("favrolistset size: "+FavListRouletteActivity.favroListSet.size.toString())
                    //itemView.favroc_check.isChecked = true
                    //itemView.favlistroulette_item_const.setBackgroundColor(ContextCompat.getColor(context, R.color.colorYellow1))
                }
            }

        }
    }

    fun getSearchFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charString = p0.toString()
                filteredList = if (charString=="전체 목록") { //⑶
                    unFilteredList
                } else {
                    var filteringList = ArrayList<GetRouletteItemInfo>()
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
                                if(item.RestCategory!!.contains("피자") || item.RestCategory!!.contains("패스트푸드")){
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
                                if(item.RestCategory!!.contains("기타") || item.RestCategory!!.contains("외국") || item.RestCategory!!.contains("패밀리")){
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
                filteredList = p1?.values as ArrayList<GetRouletteItemInfo>
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
                    var filteringList = ArrayList<GetRouletteItemInfo>()
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
                filteredList = p1?.values as ArrayList<GetRouletteItemInfo>
                notifyDataSetChanged()
            }
        }
    }
}