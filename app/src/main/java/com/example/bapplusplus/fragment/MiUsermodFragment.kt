package com.example.bapplusplus.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bapplusplus.R
import com.example.bapplusplus.data.App
import com.example.bapplusplus.data.FBUserInfo
import kotlinx.android.synthetic.main.fragment_mi_usermod.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MiUsermodFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootview = inflater.inflate(R.layout.fragment_mi_usermod, container, false)

        if(App.prefs.isAutoLogin){
            rootview.miu_switch_autologin.isChecked = true
            rootview.miu_switch_autologin.text = "사용 중"
        }else{
            rootview.miu_switch_autologin.isChecked = false
            rootview.miu_switch_autologin.text = "사용 안 함"
        }

        rootview.miu_switch_autologin.setOnCheckedChangeListener { compoundButton, bool ->
            if(bool){
                //rootview.miu_switch_autologin.isChecked = true
                rootview.miu_switch_autologin.text = "사용 중"
                App.prefs.isAutoLogin = true
            }else{
                //rootview.miu_switch_autologin.isChecked = false
                rootview.miu_switch_autologin.text = "사용 안 함"
                App.prefs.isAutoLogin = false
            }
        }

        rootview.miu_const_namereset.setOnClickListener {
            Toast.makeText(requireContext(), "Name_Change", Toast.LENGTH_SHORT).show()
        }

        rootview.miu_const_pwreset.setOnClickListener {
            Toast.makeText(requireContext(), "PW_Change", Toast.LENGTH_SHORT).show()
        }

        rootview.miu_const_withdraw.setOnClickListener {
            Toast.makeText(requireContext(), "Withdraw", Toast.LENGTH_SHORT).show()
            var withdrawDialog = AlertDialog.Builder(requireContext())
            withdrawDialog.setTitle("회원 탈퇴").setMessage("탈퇴하시겠습니까?\n유저 데이터가 즉시 삭제됩니다.")
            withdrawDialog.setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, which ->
                CoroutineScope(Dispatchers.Main).launch {
                    val withdrawResult = FBUserInfo.setUserWithdrawal()

                    if(withdrawResult){
                        FBUserInfo.setSignOut()
                        Toast.makeText(requireContext(), "탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
                        delay(2000)
                        requireActivity().finish()
                    }else{
                        Toast.makeText(requireContext(), "탈퇴에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            withdrawDialog.setNegativeButton("취소", null)
            withdrawDialog.create()
            withdrawDialog.show()
        }

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