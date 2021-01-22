package com.example.bapplusplus.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bapplusplus.BottomNaviActivity
import com.example.bapplusplus.MainHot_Data
import com.example.bapplusplus.R
import kotlinx.android.synthetic.main.mainhot_cell.view.*

class MainHotAdapter(val context: Context, val hot_list: ArrayList<MainHot_Data>) : RecyclerView.Adapter<MainHotAdapter.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.mainhot_cell, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = hot_list[position]
        holder.apply {
            bind(item, position)
            itemView.tag = position
        }
    }

    override fun getItemCount(): Int {
        return hot_list.count()
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(info: MainHot_Data, position: Int){
            itemView.mhc_tv_lead.text = (position+1).toString()+"위"
            itemView.mhc_tv_title.text = info.restTitle
            itemView.mhc_tv_rating.text = info.restRatingAvg.toString()

            itemView.mhc_const.setOnClickListener {
                val itt = Intent(context, BottomNaviActivity::class.java)
                itt.putExtra("gni_title", info.restTitle)
                itt.putExtra("gni_num", info.restNo)
                context.startActivity(itt)
            }
        }
    }



}