package com.example.bapplusplus.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.example.bapplusplus.MainActivity
import com.example.bapplusplus.NewRegisterActivity
import com.example.bapplusplus.R
import com.example.bapplusplus.data.App
import com.example.bapplusplus.data.FBUserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_new_login.*
import kotlinx.android.synthetic.main.fragment_new_login.view.*
import kotlinx.coroutines.*

class NewLoginFragment : Fragment() {

    var lemail: String = ""
    var lpw: String = ""

    var fbauth : FirebaseAuth = FirebaseAuth.getInstance()
    var fbdb : FirebaseFirestore = FirebaseFirestore.getInstance()
    var user: FirebaseUser? = fbauth.currentUser
    var user2 = FBUserInfo.fbuser
    var userUid: String = ""
    var UserName =""
    var logincode = 0
    var findPwFrag: NewFindPWFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootview = inflater.inflate(R.layout.fragment_new_login, container, false)
        userUid = user?.uid ?: ""

        if(FBUserInfo.loginState == true){
            rootview.newlog_btn_login2.text = "LOG OUT"
            rootview.newlog_til_email.visibility = View.GONE
            rootview.newlog_til_pw.visibility = View.GONE
            rootview.newlog_const_reg.visibility = View.GONE
            rootview.newlog_const_forgotpw.visibility = View.GONE
            //rootview.newlog_tv_one.text = user!!.email.toString()
            rootview.newlog_tv_one.text = FBUserInfo.userName
            rootview.newlog_btn_withdraw.visibility = View.VISIBLE
        }

        var shake_anim = AnimationUtils.loadAnimation(requireContext(), R.anim.shake1)

        //rootview.newlog_pbar.progressTintList = ColorStateList.valueOf(resources.getColor(R.color.colorBlue1, ))

        if(App.prefs.isMaintainEmail){
            rootview.newlog_check_saveemail.isChecked = true
            rootview.newlog_til_edt_email.setText(App.prefs.emailValue)
        }else{
            rootview.newlog_check_saveemail.isChecked = false
        }

        rootview.newlog_btn_reg.setOnClickListener {
            val intent = Intent(requireContext(), NewRegisterActivity::class.java)
            //startActivity(intent)
            startActivityForResult(intent, 16)
        }

        rootview.newlog_btn_login2.setOnClickListener {
            //rootview.newlog_pbar.visibility = View.GONE
            if(FBUserInfo.loginState == true){
                Toast.makeText(requireContext(), "LogoutButtonPressed", Toast.LENGTH_SHORT).show()

                FBUserInfo.setSignOut()
                requireActivity().finish()
            }else{
                Toast.makeText(requireContext(), "LoginButtonPressed", Toast.LENGTH_SHORT).show()

                lemail = rootview.newlog_til_edt_email.text.toString().trim()
                lpw = rootview.newlog_til_edt_pw.text.toString().trim()

                if(lemail.isEmpty()){
                    rootview.newlog_pbar.visibility = View.GONE
                    rootview.newlog_tv_one.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed1))
                    rootview.newlog_tv_one.text = " ❗ 이메일을 입력하세요."
                    rootview.newlog_tv_one.startAnimation(shake_anim)
                }else if(lpw.isEmpty()){
                    rootview.newlog_pbar.visibility = View.GONE
                    rootview.newlog_tv_one.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed1))
                    rootview.newlog_tv_one.text = " ❗ 비밀번호를 입력하세요."
                    rootview.newlog_tv_one.startAnimation(shake_anim)
                }else{
                    rootview.newlog_pbar.visibility = View.VISIBLE
                    rootview.newlog_tv_one.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlue1))
                    rootview.newlog_tv_one.text = "로그인 중 ..."

                    CoroutineScope(Dispatchers.IO).launch {
                        val loginResult = FBUserInfo.setLogin(lemail, lpw)

                        withContext(Dispatchers.Main){
                            if(loginResult != true){
                                Log.d("func trylogin fail", "nosuchuser")
                                rootview.newlog_pbar.visibility = View.GONE
                                rootview.newlog_tv_one.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed1))
                                rootview.newlog_tv_one.text = " ❗ 이메일 또는 비밀번호가 틀렸습니다."
                                rootview.newlog_tv_one.startAnimation(shake_anim)
                            }else{
                                if(rootview.newlog_check_autologin.isChecked){
                                    App.prefs.isAutoLogin = true
                                }
                                App.prefs.emailValue = lemail
                                App.prefs.passwordValue = lpw

                                rootview.newlog_pbar.visibility = View.GONE
                                rootview.newlog_tv_one.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGreen1))
                                rootview.newlog_tv_one.text = FBUserInfo.userName + "(으)로 로그인"
                                delay(600)
                                //finish()
                                val ittd = Intent(requireContext(), MainActivity::class.java)
                                ittd.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                Toast.makeText(requireContext(), "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                                startActivity(ittd)
                            }
                        }
                    }

                }
            }
        }

        rootview.newlog_check_saveemail.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                App.prefs.isMaintainEmail = true
            }else{
                App.prefs.isMaintainEmail = false
            }
        }

        rootview.newlog_btn_forgotpw.setOnClickListener {
            var ftr = requireActivity().supportFragmentManager.beginTransaction()
            ftr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            ftr.replace(R.id.newl_frame, NewFindPWFragment())
            ftr.addToBackStack(null)
            ftr.commit()
        }

        rootview.newlog_til_edt_email.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if(App.prefs.isMaintainEmail){
                    App.prefs.emailValue = rootview.newlog_til_edt_email.text.toString()
                }
            }
        })

        return rootview
    }

    companion object {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(resultCode){
            16 -> requireActivity().finish()
        }
    }
}