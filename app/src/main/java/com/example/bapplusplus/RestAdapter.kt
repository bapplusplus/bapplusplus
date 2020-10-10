package com.example.bapplusplus

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView

data class Rest(
    val name : String,
    val distance : Double,
    val rating : Double
)

data class RestList(
    val results: ArrayList<Rest>
)

class RestAdapter (val context: Context, val restList: ArrayList<Rest>) : RecyclerView.Adapter<RestAdapter.Holder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val tvName
    }

}