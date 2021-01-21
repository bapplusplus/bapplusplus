package com.example.bapplusplus.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.bapplusplus.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_sheet_list_filter.view.*

class BottomSheetListFilter(val origin: Int, val itemClick: (Int) -> Unit) : BottomSheetDialogFragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootview =  inflater.inflate(R.layout.fragment_bottom_sheet_list_filter, container, false)
        when(origin){
            0->rootview.bslf_tv_basic.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOrange2Primary))
            1->rootview.bslf_tv_rating.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOrange2Primary))
            2->rootview.bslf_tv_review.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOrange2Primary))
            3->rootview.bslf_tv_abc.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOrange2Primary))
        }
        return rootview
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.bslf_tv_basic.setOnClickListener {
            itemClick(0)
            //view.bslf_tv_basic.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOrange2Primary))
            dialog?.dismiss()
        }
        view.bslf_tv_rating.setOnClickListener {
            itemClick(1)
            //view.bslf_tv_rating.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOrange2Primary))
            dialog?.dismiss()
        }
        view.bslf_tv_review.setOnClickListener {
            itemClick(2)
            dialog?.dismiss()
        }
        view.bslf_tv_abc.setOnClickListener {
            itemClick(3)
            dialog?.dismiss()
        }
    }

    companion object {

    }
}