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


data class Rest(
    val name: String,
    val distance: Double,
    val rating: Double
)

data class RestList(
    val results: ArrayList<Rest>
)

data class RestInfoTemp(
    val store_title: String?,
    val store_popularity: Double,
    val store_category: String?,
    val store_time: String?,
    val pos_x: Double,
    val pos_y: Double,
    val street_address: String?,
    val call_num: String?,
    var locpos: Location?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        null
    ) {
        locpos = Location(store_title)
        locpos!!.latitude = pos_x
        locpos!!.longitude = pos_y
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(store_title)
        parcel.writeDouble(store_popularity)
        parcel.writeString(store_category)
        parcel.writeString(store_time)
        parcel.writeDouble(pos_x)
        parcel.writeDouble(pos_y)
        parcel.writeString(street_address)
        parcel.writeString(call_num)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RestInfoTemp> {
        override fun createFromParcel(parcel: Parcel): RestInfoTemp {
            return RestInfoTemp(parcel)
        }

        override fun newArray(size: Int): Array<RestInfoTemp?> {
            return arrayOfNulls(size)
        }
    }
}

class RestAdapter(val context: Context, val restList: ArrayList<RestInfoTemp>) : RecyclerView.Adapter<RestAdapter.Holder>() {
    private val selectedItems = SparseBooleanArray()

    // 직전에 클릭됐던 Item의 position
    private var prePosition = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.favlist_cell, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = restList[position]

        val listener = View.OnClickListener { it->
            val intent = Intent(context, BottomNaviActivity::class.java)

            intent.putExtra("posx", restList[position].pos_x)
            intent.putExtra("posy", restList[position].pos_y)
            intent.putExtra("infoList", restList[position])
            context.startActivity(intent)
        }
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    override fun getItemCount(): Int {
        return restList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val tvName
        val container = itemView.findViewById<ConstraintLayout>(R.id.favlist_item_const)
        val title_f = itemView.findViewById<TextView>(R.id.fav_item_title)
        val category_f = itemView.findViewById<TextView>(R.id.fav_item_category)
        val star_f = itemView.findViewById<TextView>(R.id.fav_item_star)
        val callnum_f = itemView.findViewById<TextView>(R.id.fav_item_expand_call)



        fun bind(listener: View.OnClickListener, restinfotemp: RestInfoTemp){
            title_f.text = restinfotemp.store_title
            category_f.text = restinfotemp.store_category
            star_f.text = restinfotemp.store_popularity.toString()
            callnum_f.text = restinfotemp.call_num
            container.setOnClickListener(listener)
        }
    }

}