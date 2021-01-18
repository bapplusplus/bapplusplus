package com.example.bapplusplus.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bapplusplus.R

class MiUsermodFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootview = inflater.inflate(R.layout.fragment_mi_usermod, container, false)



        return rootview
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MiUsermodFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}