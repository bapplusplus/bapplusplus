package com.example.bapplusplus.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.bapplusplus.FavListRouletteActivity
import com.example.bapplusplus.R
import com.example.bapplusplus.data.App
import com.example.bapplusplus.data.FBUserInfo
import kotlinx.android.synthetic.main.fragment_mi_change_pw.*
import kotlinx.android.synthetic.main.fragment_mi_change_pw.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MiChangePWFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootview = inflater.inflate(R.layout.fragment_mi_change_pw, container, false)

        rootview.micpw_btn_original.setOnClickListener {
            if(micpw_til_edt_original.text.toString() != App.prefs.passwordValue){
                rootview.micpw_tv_one.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed1))
                rootview.micpw_tv_one.text = "비밀번호가 틀립니다."
                rootview.micpw_inner_const.visibility = View.GONE
            }else{
                rootview.micpw_tv_one.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlue1))
                rootview.micpw_tv_one.text = "비밀번호가 확인되었습니다."
                rootview.micpw_inner_const.visibility = View.VISIBLE
                rootview.micpw_btn_original.text = "확인 완료"
                rootview.micpw_btn_original.isEnabled = false
                rootview.micpw_til_edt_original.isEnabled = false
                rootview.micpw_til_original.isEnabled = false
            }
        }

        rootview.micpw_btn_new.setOnClickListener {
            if(micpw_til_edt_new_one.text.toString().count() < 6){
                rootview.micpw_tv_two.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed1))
                rootview.micpw_tv_two.text = "새로운 비밀번호를 입력해 주세요. (6자 이상)"
            }else{
                if(micpw_til_edt_new_two.text.toString() != micpw_til_edt_new_one.text.toString()){
                    rootview.micpw_tv_two.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed1))
                    rootview.micpw_tv_two.text = "새로운 비밀번호를 다시 확인해주세요."
                }else{
                    CoroutineScope(Main).launch {
                        var res = updateNewPassword(micpw_til_edt_new_two.text.toString())
                        if(res){
                            Toast.makeText(context, "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            requireActivity().onBackPressed()
                        }else{
                            Toast.makeText(context, "비밀번호 변경 실패\n재로그인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }


        return rootview
    }

    companion object {

    }

    suspend fun updateNewPassword(new_pw: String): Boolean{
        return try{
            FBUserInfo.fbauth.currentUser!!.updatePassword(new_pw).await()
            return true
        }catch (e: Exception){
            Log.d("ChangePW", "task failure: " + ", " + e.stackTrace+e.localizedMessage)
            return false
        }
    }
}