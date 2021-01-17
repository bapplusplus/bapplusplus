package com.example.bapplusplus.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.example.bapplusplus.MyInfoActivity
import com.example.bapplusplus.R
import com.example.bapplusplus.data.FBUserInfo
import kotlinx.android.synthetic.main.activity_my_info.*
import kotlinx.android.synthetic.main.activity_user_info_mode.*
import kotlinx.android.synthetic.main.fragment_mi_first.*
import kotlinx.android.synthetic.main.fragment_mi_first.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MiFirstFragment : Fragment() {

//    var fm = requireActivity().supportFragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        var rootview = inflater.inflate(R.layout.fragment_mi_first, container, false)

        var toolbar = requireActivity().myinfo_toolbar
        requireActivity().myinfo_toolbar_title.text = "내 정보"

        rootview.findViewById<TextView>(R.id.myinfo_tv_user_name).text = FBUserInfo.fbuser!!.displayName.toString()
        rootview.findViewById<TextView>(R.id.myinfo_tv_user_email).text = FBUserInfo.fbuser!!.email.toString()


        rootview.findViewById<ConstraintLayout>(R.id.myinfo_const_user).setOnClickListener {
            Toast.makeText(requireContext(), "User Clicked", Toast.LENGTH_SHORT).show()
        }

        rootview.findViewById<ConstraintLayout>(R.id.myinfo_const_favs).setOnClickListener {
            Toast.makeText(requireContext(), "Favs Clicked", Toast.LENGTH_SHORT).show()
        }

        rootview.findViewById<ConstraintLayout>(R.id.myinfo_const_review).setOnClickListener {
            Toast.makeText(requireContext(), "Review Clicked", Toast.LENGTH_SHORT).show()
            var ftr = requireActivity().supportFragmentManager.beginTransaction()
            ftr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            ftr.replace(R.id.myinfo_frame, MiReviewFragment())
            ftr.addToBackStack(null)
            ftr.commit()
        }

        rootview.myinfo_btn_logout_temp.setOnClickListener {
            Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            //fbauth.signOut()
            //FBUserInfo.fbauth.signOut()
            //FBUserInfo.loginState = false
            FBUserInfo.setSignOut()
            requireActivity().finish()
        }

        rootview.myinfo_btn_withdraw_temp.setOnClickListener {
            var withdrawDialog = AlertDialog.Builder(requireContext())
            withdrawDialog.setTitle("회원 탈퇴").setMessage("탈퇴하시겠습니까? 유저 데이터가 즉시 삭제됩니다.")
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

        //myinfo_toolbar_title.text = "SS"

    }

    companion object {
        
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MiFirstFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}