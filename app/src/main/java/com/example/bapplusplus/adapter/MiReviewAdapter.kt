package com.example.bapplusplus.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bapplusplus.BottomNaviActivity
import com.example.bapplusplus.R
import com.example.bapplusplus.data.FBUserInfo
import com.example.bapplusplus.fragment.MiReviewFragment
import com.example.bapplusplus.fragment.MiReview_Data
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.favlistnu_cell.view.*
import kotlinx.android.synthetic.main.fragment_mi_review.*
import kotlinx.android.synthetic.main.mirv_cell.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class MiReviewAdapter(val context: Context, val review_list: ArrayList<MiReview_Data>) : RecyclerView.Adapter<MiReviewAdapter.Holder>() {

    var fbstr = FirebaseStorage.getInstance()
    var fbdb = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiReviewAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.mirv_cell, parent, false)

        return Holder(view)
    }

    override fun onBindViewHolder(holder: MiReviewAdapter.Holder, position: Int) {
        val item = review_list[position]
        holder.apply {
            bind(item)
            itemView.tag = position

            itemView.mirvc_const_title.setOnClickListener {
                val intent = Intent(context, BottomNaviActivity::class.java)
                intent.putExtra("gni_num", item.restNo)
                intent.putExtra("gni_title", item.restTitle)
                intent.putExtra("gni_category", item.restCategory)
                intent.putExtra("BottomNaviNum", 1)
                context.startActivity(intent)
            }

            itemView.mirvc_btn_delete.setOnClickListener {
                Toast.makeText(context, "Delete Request", Toast.LENGTH_SHORT).show()
                CoroutineScope(Main).launch {
                    if(MiReviewFragment.newDeleteOne(item)){
                        Toast.makeText(context, "Delete Success", Toast.LENGTH_SHORT).show()
                        review_list.removeAt(position)
                        notifyItemRemoved(position)
                        MiReviewFragment.tvNumTextSetter(review_list.size)
                        if(review_list.size == 0){
                            MiReviewFragment.spinnerview?.isEnabled = false
                            MiReviewFragment.spinnerview?.visibility = View.INVISIBLE
                            MiReviewFragment.tvnoreview?.visibility = View.VISIBLE
                        }
                        //MiReviewFragment.newInstance("", "").mirv_tv_num.text = "내가 작성한 리뷰: "+review_list.size+"건"
                    }else{
                        Toast.makeText(context, "Delete Fail", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return review_list.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(info: MiReview_Data){
            itemView.mirvc_tv_resttitle.text = info.restTitle
            itemView.mirvc_tv_time.text = info.timeValue
            //itemView.mirvc_tv_menu.text = info.menu
            itemView.mirvc_tv_content.text = info.content
            itemView.mirvc_tv_rating.text = info.star.toString()
            itemView.mirvc_ratingbar.rating = info.star.toFloat()

            if(!info.menu.isNullOrEmpty()){
                itemView.mirvc_tv_order.text ="Order"
                itemView.mirvc_tv_menu.text = info.menu
                itemView.mirvc_tv_menu.visibility = View.VISIBLE
                itemView.mirvc_tv_order.visibility = View.VISIBLE
            }else{
                itemView.mirvc_tv_order.text ="메뉴 미작성"
                itemView.mirvc_tv_menu.visibility = View.GONE
                //itemView.mirvc_tv_order.visibility = View.GONE
                itemView.mirvc_tv_order.visibility = View.VISIBLE
            }

            if(!info.photoPath.isNullOrEmpty()){
                itemView.mirvc_img_one.visibility = View.VISIBLE
                var storageRef = fbstr?.reference
                storageRef?.child(info.photoPath)?.downloadUrl?.addOnSuccessListener {
                    Glide.with(context).load(it).centerCrop().into(itemView.mirvc_img_one)

                }.addOnFailureListener {
                    Log.e("MiReviewAdapter", it.toString()+" / Failed to load image: "+info.photoPath)
                }
            }else{
                itemView.mirvc_img_one.setImageResource(android.R.color.transparent)
                itemView.mirvc_img_one.visibility = View.GONE
            }

            itemView.mirvc_img_main.setImageResource(
                when(info.restCategory){
                    "한식"->R.drawable.korean_240_cut
                    "일식"->R.drawable.sushi_240_cut
                    "중식"->R.drawable.chinese_240_n
                    "분식"->R.drawable.toppokki_240_cut
                    "카페/찻집"->R.drawable.coffee_240_cut
                    "경양식"->R.drawable.cutlet_240_cut
                    "김밥(도시락)"->R.drawable.gimbap_240_cut
                    "피자"->R.drawable.pizza_240_cut
                    "패스트푸드"->R.drawable.burger_240_cut
                    "치킨"->R.drawable.chicken_240_cut
                    "호프/치킨"->R.drawable.hof_240_cut
                    "정종/대포집/소주방"->R.drawable.glass_240_cut
                    "패밀리레스트랑"->R.drawable.spaghetti_240_cut
                    else->R.drawable.pizza_240_cut
                }
            )



        }
    }




}