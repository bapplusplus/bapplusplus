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
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.favlistnu_cell.view.*

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



class FavListAdapter(val context: Context, val gni: ArrayList<GetNumsInfo>) : RecyclerView.Adapter<FavListAdapter.Holder>(){

    var fbfs = FirebaseFirestore.getInstance()
    var fbauth = FirebaseAuth.getInstance()
    //var fvlist = FavListNu(arrayListOf())

    //private var selectedItems: SparseBooleanArray = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        //println(listnu.resultList[10].RestTitle + "ttrs")
        val view = LayoutInflater.from(context).inflate(R.layout.favlistnu_cell, parent, false)
        //println("listtrytest in recycler"+gni.get(5).RestTitle + " / " + gni.get(37).RestTitle)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        //val item = infoList[position]
        val gni_item = gni[position]
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
        return gni.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val container = itemView.findViewById<ConstraintLayout>(R.id.favlistnu_item_const)
        val title_f = itemView.findViewById<TextView>(R.id.fnc_item_title)
        val category_f = itemView.findViewById<TextView>(R.id.fnc_item_category)
        val star_f = itemView.findViewById<TextView>(R.id.fnc_item_star)
        val callnum_f = itemView.findViewById<TextView>(R.id.fnc_item_expand_call)



        fun bind(listener: View.OnClickListener, gni: GetNumsInfo){
            title_f.text = gni.RestTitle
            category_f.text = "Category not supported"
            star_f.text = gni.RestRatingAvg.toString()
            callnum_f.text = "Callnot not supported"
            itemView.fnc_tv_reviewnum.text = gni.RestReviewNum.toString()
            container.setOnClickListener(listener)
        }
    }
}