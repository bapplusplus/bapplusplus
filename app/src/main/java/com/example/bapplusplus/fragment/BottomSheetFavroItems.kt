package com.example.bapplusplus.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.bapplusplus.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_sheet_favro_items.view.*


class BottomSheetFavroItems(var str: String?) : BottomSheetDialogFragment() {

    //var bsfavroTvCon = requireView().findViewById<TextView>(R.id.bsfavro_content)
    //var bsfavroTvTitle = requireView().findViewById<TextView>(R.id.bsfavro_title)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    //lateinit var bsfavroTvTitle: TextView
    //lateinit var bsfavroTvCon: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootview = inflater.inflate(R.layout.fragment_bottom_sheet_favro_items, container, false)
        rootview.bsfavro_content.text = "Created?"
        bsfavroTvTitle = rootview.bsfavro_content
        bsfavroTvCon = rootview.bsfavro_title
        rootview.bsfavro_content.text = str
        return rootview
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //bsfavroTvTitle = requireView().bsfavro_content
        //bsfavroTvCon = requireView().bsfavro_title
    }

    companion object {
        lateinit var bsfavroTvTitle: TextView
        lateinit var bsfavroTvCon: TextView
    }

    fun setContentText(getnum: Int, getcontent: String){
        bsfavroTvTitle.text = "선택한 항목: "+getnum.toString()+"개"
        bsfavroTvCon.text = getcontent
    }
}