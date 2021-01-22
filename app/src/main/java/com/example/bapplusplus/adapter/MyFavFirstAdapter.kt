package com.example.bapplusplus.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bapplusplus.BottomNaviActivity
import com.example.bapplusplus.R
import com.example.bapplusplus.fragment.MyFav_Data
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.myfav_cell.view.*


class MyFavFirstAdapter(val context: Context, val fav_list: ArrayList<MyFav_Data>) : RecyclerView.Adapter<MyFavFirstAdapter.Holder>() {
    var fbdb = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.myfav_cell, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return fav_list.count()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = fav_list[position]
        holder.apply {
            bind(item)
            itemView.tag = position

            itemView.myfavc_const.setOnClickListener {
                val intent = Intent(context, BottomNaviActivity::class.java)
                intent.putExtra("gni_num", item.restNo)
                intent.putExtra("gni_title", item.restTitle)
                intent.putExtra("gni_category", item.restCategory)
                intent.putExtra("BottomNaviNum", 1)
                context.startActivity(intent)
            }

        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(info: MyFav_Data){
            itemView.myfavc_tv_resttitle.text = info.restTitle
            itemView.myfavc_tv_category.text = info.restCategory
            itemView.myfavc_tv_address.text = info.restAddressRoad

            itemView.myfavc_img_main.setImageResource(
                when(info.restCategory){
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
        }
    }

}