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
import com.example.bapplusplus.data.FBUserInfo
import com.example.bapplusplus.fragment.MiFavFragment
import com.example.bapplusplus.fragment.MiFav_Data
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.mifav_cell.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class MiFavAdapter(val context: Context, val fav_list: ArrayList<MiFav_Data>) : RecyclerView.Adapter<MiFavAdapter.Holder>() {
    var fbdb = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.mifav_cell, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return fav_list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = fav_list[position]
        holder.apply {
            bind(item)
            itemView.tag = position

            itemView.mifavc_const_title.setOnClickListener {
                val intent = Intent(context, BottomNaviActivity::class.java)
                intent.putExtra("gni_num", item.restNo)
                intent.putExtra("gni_title", item.restTitle)
                intent.putExtra("gni_category", item.restCategory)
                intent.putExtra("BottomNaviNum", 1)
                context.startActivity(intent)
            }

            itemView.mifavc_btn_delete.setOnClickListener {
                Toast.makeText(context, "Like Delete Request", Toast.LENGTH_SHORT).show()
                CoroutineScope(Main).launch {
                    if(MiFavFragment.likeCancel(item, item.restNo, FBUserInfo.fbauth.currentUser!!.uid)){
                        fav_list.removeAt(position)
                        Toast.makeText(context, "좋아요 목록에서 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        MiFavFragment.mifavtvnum?.text = "좋아요 항목 "+fav_list.size.toString()+"개"
                        if(fav_list.isEmpty()){
                            MiFavFragment.mifavtvnum?.visibility = View.GONE
                            MiFavFragment.mifavsepline?.visibility = View.GONE
                            MiFavFragment.mifavtvnofav?.visibility = View.VISIBLE
                        }else{
                            notifyItemRemoved(position)
                        }
                    }else{
                        Toast.makeText(context, "Like Delete fail", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(info: MiFav_Data){
            itemView.mifavc_tv_resttitle.text = info.restTitle
            itemView.mifavc_tv_category.text = info.restCategory
            itemView.mifavc_tv_address.text = info.restAddressRoad
        }
    }

}