package com.example.bapplusplus.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bapplusplus.R
import com.example.bapplusplusTemp.fragment.BN3Info_Review
import kotlinx.android.synthetic.main.bn3review_cell.view.*

class Bn3ReviewsAdapter(val context: Context, val review_list: ArrayList<BN3Info_Review>, val uid: String) : RecyclerView.Adapter<Bn3ReviewsAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.bn3review_cell, parent, false)
        //println("listtrytest in recycler"+gni.get(5).RestTitle + " / " + gni.get(37).RestTitle)
        println("Bn3reviewAdapter size/ " + review_list.size)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = review_list[position]
        holder.apply {
            bind(item)
            itemView.tag = position
        }
        Log.d("TAG Rec","Recyclerview called")
        println("Bn3reviewAdapter size is " + review_list.size)
        println("Bn3reviewAdapter position is " + position)
    }

    override fun getItemCount(): Int {
        return review_list.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        fun bind(listener: View.OnClickListener, gni: GetNumsInfo){
//
//        }

        fun bind(info: BN3Info_Review){
            itemView.bn3rcvc_uid.text = info.uid
            if(info.uid.equals(uid)){
                itemView.bn3rcvc_tv_mine.visibility = View.VISIBLE
            }else{
                itemView.bn3rcvc_tv_mine.visibility = View.INVISIBLE
            }
            itemView.bn3rcvc_date.text = info.date
            itemView.bn3rcvc_content.text = info.content
            itemView.bn3rcvc_content.isClickable = false
        }
    }
}