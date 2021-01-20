package com.example.bapplusplus.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.bapplusplus.R
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bn4review_cell.view.*
import kotlinx.android.synthetic.main.bottom_dialog_photoview.view.*

class BottomSheetPhotoView(geturi: Uri?) : BottomSheetDialogFragment() {
    var rsidget = geturi

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.bottom_dialog_photoview, container, false)

        Glide.with(requireContext()).load(rsidget).into(view.bodpv_pv)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.findViewById<ImageButton>(R.id.bodpv_btn_close)?.setOnClickListener {
            dismiss()
        }
    }


    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal =
                d.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheetInternal?.minimumHeight=
                Resources.getSystem().getDisplayMetrics().heightPixels
        }
    }*/

    override fun onStart() {
        super.onStart()

        if (dialog != null) {
            val bottomSheet: View = dialog!!.findViewById(R.id.design_bottom_sheet)
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }

        val view = view
        view!!.post{
            val parent = view!!.parent as View
            val params = parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            val bottomSheetBehavior = behavior as BottomSheetBehavior<*>?
            bottomSheetBehavior!!.peekHeight = view!!.measuredHeight

            //set Color
            parent.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBlack1))
        }
    }

}