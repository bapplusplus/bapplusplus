package com.example.bapplusplus.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bapplusplus.R
import com.example.bapplusplus.data.App
import com.example.bapplusplus.data.FBUserInfo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_sheet_main_quit.*

class BottomSheetMainQuit : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet_main_quit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bsmq_btn_no.setOnClickListener {
            //Toast.makeText(requireContext(),"No", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        bsmq_btn_yes.setOnClickListener {
            //Toast.makeText(requireContext(),"Yes", Toast.LENGTH_SHORT).show()
            if(!App.prefs.isAutoLogin){
                FBUserInfo.setSignOut()
                //Toast.makeText(requireContext(), "Logout, Finish", Toast.LENGTH_SHORT).show()
            }
            requireActivity().finish()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {

    }
}