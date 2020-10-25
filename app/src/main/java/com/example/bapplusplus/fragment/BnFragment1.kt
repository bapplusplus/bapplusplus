package com.example.bapplusplusTemp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.bapplusplus.InfoTemp
import com.example.bapplusplus.R
import com.example.bapplusplus.RestInfoTemp
import kotlinx.android.synthetic.main.fragment_bn1.*

class BnFragment1 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootview = inflater.inflate(R.layout.fragment_bn1, container, false)
        // Inflate the layout for this fragment
        val bundle = arguments
        val infoTemp = bundle?.getParcelable<RestInfoTemp>("infotemp")

        rootview.findViewById<TextView>(R.id.bn1_title).text = infoTemp?.store_title ?: "ttt"
        rootview.findViewById<TextView>(R.id.bn1_category).text = infoTemp?.store_category ?: "cc"
        rootview.findViewById<TextView>(R.id.bn1_address).text = infoTemp?.street_address ?: "aaa"
        rootview.findViewById<TextView>(R.id.bn1_call).text = infoTemp?.call_num ?: "ccc"
        rootview.findViewById<TextView>(R.id.bn1_time).text = infoTemp?.store_time ?: "tt"
        rootview.findViewById<TextView>(R.id.bn1_delivery).text = "Unavailable"

        return rootview
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}