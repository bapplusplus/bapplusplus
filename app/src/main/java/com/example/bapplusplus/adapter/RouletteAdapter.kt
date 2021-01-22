package com.example.bapplusplus.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.bapplusplus.GetRouletteItemInfo
import com.example.bapplusplus.R
import kotlinx.android.synthetic.main.roulette_cell.view.*


class RouletteAdapter(val context: Context, val rouletteItemList: ArrayList<GetRouletteItemInfo>) : RecyclerView.Adapter<RouletteAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.roulette_cell, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return rouletteItemList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val singleItem = rouletteItemList[position]

        holder.apply {
            bind(singleItem)
        }
//        holder.bind(rouletteItemList[position])
    }

    var countItem: Int = 1


    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val tvRouletteRestNum = itemView.findViewById<TextView>(R.id.tvCellNum)
//        val tvRouletteRestName = itemView.findViewById<TextView>(R.id.tvRestName)

        val container = itemView.findViewById<ConstraintLayout>(R.id.roulette_container)

        fun bind(getRouletteItemInfo: GetRouletteItemInfo) {
//            itemView.tvCellNum.text = getRouletteItemInfo.RestNo.toString()
            itemView.tvCellNum.text = (countItem++).toString()
            itemView.tvRestName.text = getRouletteItemInfo.RestTitle

            // 해당 cell을 누르면 detail로 넘어갈 수 있게 만들기 -> 시간 되면
            container.setOnClickListener{
                Toast.makeText(context, "눌림", Toast.LENGTH_SHORT).show()
            }

        }

    }
}