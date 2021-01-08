package com.example.bapplusplus.dialogs

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.example.bapplusplus.BottomNaviActivity
import com.example.bapplusplus.R


class ProgressDialogOne : Dialog {
    constructor(context: Context) : super(context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_progress_one)
    }

//    constructor(context: Context) : super(context) {
//
//    }
}