package com.example.bapplusplus

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

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

data class FavList(
    val resultList: ArrayList<InfoTemp>
)

class FavListAdapter(val context: Context, val infoList: ArrayList<InfoTemp>) : RecyclerView.Adapter<FavListAdapter.Holder>(){
    private var selectedItems: SparseBooleanArray = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.favlist_cell, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = infoList[position]
        val listener = View.OnClickListener { it->
            val intent = Intent(context, BottomNaviActivity::class.java)

            intent.putExtra("posx", infoList[position].pos_x)
            intent.putExtra("posy", infoList[position].pos_y)
            intent.putExtra("infoList", infoList[position])
            context.startActivity(intent)
        }
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }

    }

    override fun getItemCount(): Int {
        return infoList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val container = itemView.findViewById<ConstraintLayout>(R.id.favlist_item_const)
        val title_f = itemView.findViewById<TextView>(R.id.fav_item_title)
        val category_f = itemView.findViewById<TextView>(R.id.fav_item_category)
        val star_f = itemView.findViewById<TextView>(R.id.fav_item_star)
        val callnum_f = itemView.findViewById<TextView>(R.id.fav_item_expand_call)



        fun bind(listener: View.OnClickListener, infotemp: InfoTemp){
            title_f.text = infotemp.store_title
            category_f.text = infotemp.store_category
            star_f.text = infotemp.store_popularity.toString()
            callnum_f.text = infotemp.call_num
            container.setOnClickListener(listener)
        }
    }
}