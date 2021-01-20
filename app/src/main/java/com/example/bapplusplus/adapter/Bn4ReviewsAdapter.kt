package com.example.bapplusplus.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bapplusplus.R
import com.example.bapplusplus.data.FBUserInfo
import com.example.bapplusplus.fragment.BN4Info_Review
import com.example.bapplusplus.fragment.BottomSheetPhotoView
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.bn4review_cell.view.*


class Bn4ReviewsAdapter(
    val context: Context,
    val review_list: ArrayList<BN4Info_Review>,
    val fragmentManager: FragmentManager
) : RecyclerView.Adapter<Bn4ReviewsAdapter.Holder>(){
    var fbstr = FirebaseStorage.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.bn4review_cell, parent, false)

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = review_list[position]
        holder.apply {
            bind(item)
            itemView.tag = position
        }

    }

    override fun getItemCount(): Int {
        return review_list.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var botsheet: BottomSheetPhotoView? = null
        var geturi: Uri? = null
        fun bind(info: BN4Info_Review){
            itemView.bn4c_tv_name.text = info.displayName
            itemView.bn4c_tv_time.text = info.timeValue
            //itemView.bn4c_tv_menu.text = info.menu
            itemView.bn4c_tv_content.text = info.content
            itemView.bn4c_tv_rating.text = info.star.toString()
            itemView.bn4c_ratingbar.rating = info.star.toFloat()

            if(!info.menu.isNullOrEmpty()){
                itemView.bn4c_tv_menu.text = info.menu
                itemView.bn4c_tv_menu.visibility = View.VISIBLE
                itemView.bn4c_tv_order.visibility = View.VISIBLE
            }else{
                itemView.bn4c_tv_menu.text =""
                itemView.bn4c_tv_menu.visibility = View.GONE
                itemView.bn4c_tv_order.visibility = View.GONE
            }

            if(info.uid == FBUserInfo.fbuser?.uid){
                itemView.bn4c_tv_myreview.visibility = View.VISIBLE
            }else{
                itemView.bn4c_tv_myreview.visibility = View.INVISIBLE
            }

            var storageRef = fbstr?.reference

            if(!info.photoPath.isNullOrEmpty()){
                itemView.bn4c_img_one.visibility = View.VISIBLE
                //botsheet = BottomSheetPhotoView(geturi)
                //var storageRef = fbstr?.reference
                storageRef?.child(info.photoPath)?.downloadUrl?.addOnSuccessListener {
                    Glide.with(context).load(it).centerCrop().into(itemView.bn4c_img_one)
                    //botsheet = BottomSheetPhotoView(storageRef?.child(info.photoPath)?.downloadUrl?.result)
                    //var bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                    geturi = it
                }.addOnFailureListener {
                    Log.e(
                        "BN4Adapter",
                        it.toString() + " / Failed to load image: " + info.photoPath
                    )
                }
            }else{
                itemView.bn4c_img_one.setImageResource(android.R.color.transparent)
                itemView.bn4c_img_one.visibility = View.GONE
            }

            itemView.bn4c_img_one.setOnClickListener {

                botsheet = BottomSheetPhotoView(geturi)
                //Glide.with(context).load(it).into(botsheet.bodpv_pv)

                botsheet!!.show(fragmentManager, botsheet!!.tag)
            }

        }
    }

    fun imageDownloadTest(photopath: String): Boolean{
        var storageRef = fbstr?.reference
        return try {
            val returner = storageRef?.child("PhotoTest01/imageTestProto2.png")?.downloadUrl
            return true
        }catch (e: Exception){
            return false
        }
    }


}