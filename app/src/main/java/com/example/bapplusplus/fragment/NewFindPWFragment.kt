package com.example.bapplusplus.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bapplusplus.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_new_find_pw.*
import kotlinx.android.synthetic.main.fragment_new_find_pw.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.PrintWriter
import java.io.StringWriter


class NewFindPWFragment : Fragment() {

    var fbauth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var rootview = inflater.inflate(R.layout.fragment_new_find_pw, container, false)

        rootview.newfpw_btn_send.setOnClickListener {
            val get_email = newfpw_til_edt_email.text.toString().trim()
            if(get_email.isNullOrEmpty()){
                rootview.newfpw_til_email.error = "이메일 주소를 입력해 주세요."
            }else if(!get_email.contains("@")){
                rootview.newfpw_til_email.error = "이메일 주소를 올바르게 입력해 주세요."
            }else{
                rootview.newfpw_progress.visibility = View.VISIBLE
                rootview.newfpw_til_email.isErrorEnabled = false
                CoroutineScope(Main).launch {
                    val result = trySendResetEmail(get_email)
                    if(result){
                        rootview.newfpw_progress.visibility = View.INVISIBLE
                        rootview.newfpw_tv_result.text = "입력하신 주소로 메일을 보냈습니다.\n메일을 확인하여 비밀번호를 재설정해 주세요."
                        rootview.newfpw_tv_result.visibility = View.VISIBLE
                    }else{
                        rootview.newfpw_progress.visibility = View.INVISIBLE
                        rootview.newfpw_tv_result.text = " ❗ 등록되지 않은 이메일입니다."
                        rootview.newfpw_tv_result.visibility = View.VISIBLE
                    }
                }
            }
        }

        rootview.newfpw_btn_goback.setOnClickListener {
            requireActivity().onBackPressed()
        }



        return rootview
    }

    companion object {

    }

    suspend fun trySendResetEmail(get_email: String): Boolean {
        try{
            val task = fbauth.sendPasswordResetEmail(get_email).await()
            Log.d("NewPW", "task successful: " + get_email)
            return true
        }catch (e: Exception){
            Log.d("NewPW", "task failure: " + get_email + ", " + e.stackTrace)

            val sw = StringWriter()
            e.printStackTrace(PrintWriter(sw))
            val exceptionAsStrting: String = sw.toString()
            Log.e("NewPw", exceptionAsStrting)

            return false
        }
    }
}