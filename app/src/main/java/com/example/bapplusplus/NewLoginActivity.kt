package com.example.bapplusplus

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.example.bapplusplus.data.FBUserInfo
import com.example.bapplusplus.fragment.MiFavFragment
import com.example.bapplusplus.fragment.MiFirstFragment
import com.example.bapplusplus.fragment.NewFindPWFragment
import com.example.bapplusplus.fragment.NewLoginFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_new_login.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main


class NewLoginActivity : AppCompatActivity() {

    enum class NewLoginErrorCode{
        LOGIN_SUCCESS, EMAIL_IS_EMPTY, PW_IS_EMPTY, FAILURE_LOGIN_INFO_WRONG, FAILURE_ELSE;
    }

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

        //fbauth = FirebaseAuth.getInstance()
        //fbdb = FirebaseFirestore.getInstance()
        //user = fbauth.currentUser
        userUid = user?.uid ?: ""

        setContentView(R.layout.activity_new_login)

        var ftr = supportFragmentManager.beginTransaction()
        ftr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ftr.replace(R.id.newl_frame, NewLoginFragment())
        //ftr.addToBackStack(null)
        ftr.commit()

        //deprecated
        /*
        //if(FBUserInfo.fbauth.currentUser != null){
        if(FBUserInfo.loginState == true){
            newl_btn_login2.text = "LOG OUT"
            newl_til_email.visibility = View.GONE
            newl_til_pw.visibility = View.GONE
            newl_const_reg.visibility = View.GONE
            newl_const_forgotpw.visibility = View.GONE
            //newl_tv_one.text = user!!.email.toString()
            newl_tv_one.text = FBUserInfo.userName
            newl_btn_withdraw.visibility = View.VISIBLE
        }

        var shake_anim = AnimationUtils.loadAnimation(applicationContext, R.anim.shake1)

        //newl_pbar.progressTintList = ColorStateList.valueOf(resources.getColor(R.color.colorBlue1, ))

        newl_btn_reg.setOnClickListener {
            val intent = Intent(this, NewRegisterActivity::class.java)
            //startActivity(intent)
            startActivityForResult(intent, 16)
        }

        newl_btn_login2.setOnClickListener {
            //newl_pbar.visibility = View.GONE
            if(FBUserInfo.loginState == true){
                Toast.makeText(this, "LogoutButtonPressed", Toast.LENGTH_SHORT).show()
                //fbauth.signOut()
                //FBUserInfo.fbauth.signOut()
                //FBUserInfo.loginState = false
                FBUserInfo.setSignOut()
                finish()
            }else{
                Toast.makeText(this, "LoginButtonPressed", Toast.LENGTH_SHORT).show()

                lemail = newl_til_edt_email.text.toString().trim()
                lpw = newl_til_edt_pw.text.toString().trim()

                if(lemail.isEmpty()){
                    newl_pbar.visibility = View.GONE
                    newl_tv_one.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.colorRed1
                        )
                    )
                    newl_tv_one.text = " ❗ 이메일을 입력하세요."
                    newl_tv_one.startAnimation(shake_anim)
                }else if(lpw.isEmpty()){
                    newl_pbar.visibility = View.GONE
                    newl_tv_one.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.colorRed1
                        )
                    )
                    newl_tv_one.text = " ❗ 비밀번호를 입력하세요."
                    newl_tv_one.startAnimation(shake_anim)
                }else{
                    newl_pbar.visibility = View.VISIBLE
                    newl_tv_one.setTextColor(ContextCompat.getColor(this, R.color.colorBlue1))
                    newl_tv_one.text = "로그인 중 ..."

                    CoroutineScope(IO).launch {
                        val loginResult = FBUserInfo.setLogin(lemail, lpw)

                        withContext(Main){
                            if(loginResult != true){
                                Log.d("func trylogin fail", "nosuchuser")
                                newl_pbar.visibility = View.GONE
                                newl_tv_one.setTextColor(
                                    ContextCompat.getColor(
                                        applicationContext,
                                        R.color.colorRed1
                                    )
                                )
                                newl_tv_one.text = " ❗ 이메일 또는 비밀번호가 틀렸습니다."
                                newl_tv_one.startAnimation(shake_anim)
                            }else{
                                newl_pbar.visibility = View.GONE
                                newl_tv_one.setTextColor(
                                    ContextCompat.getColor(
                                        applicationContext,
                                        R.color.colorGreen1
                                    )
                                )
                                newl_tv_one.text = FBUserInfo.userName + "(으)로 로그인"
                                delay(600)
                                //finish()
                                val ittd = Intent(applicationContext, MainActivity::class.java)
                                ittd.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                Toast.makeText(applicationContext, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                                startActivity(ittd)
                            }
                        }
                    }



                    /*FBUserInfo.fbauth.signInWithEmailAndPassword(lemail, lpw).addOnCompleteListener {
                        if (it.isSuccessful) {
                            //user = FirebaseAuth.getInstance().currentUser
                            FBUserInfo.fbuser = fbauth.currentUser
                            //userUid = user?.uid ?: "nun"
                            FBUserInfo.userUid = FBUserInfo.fbuser?.uid ?: "nun2uid"
                            val docRef = fbdb.collection("AccountGroup").document(FBUserInfo.userUid)
                            docRef.get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                                        UserName = document.getString("name").toString()
                                        FBUserInfo.userName = document.getString("name").toString()
                                        logincode = NewLoginErrorCode.LOGIN_SUCCESS.ordinal
                                        FBUserInfo.loginState = true
                                        CoroutineScope(Main).launch {
                                            newl_pbar.visibility = View.GONE
                                            newl_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorGreen1))
                                            newl_tv_one.text = FBUserInfo.userName + "(으)로 로그인"
                                            delay(600)
                                            finish()
                                        }

                                    } else {
                                        Log.d("TAG", "No such document")
                                        logincode = NewLoginErrorCode.FAILURE_ELSE.ordinal
                                        newl_pbar.visibility = View.GONE
                                        newl_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                                        newl_tv_one.text = " ❗ Fatal Error"
                                        newl_tv_one.startAnimation(shake_anim)
                                    }
                                }.addOnFailureListener { exception ->
                                    Log.d("TAG", "get failed with ", exception)
                                    logincode = NewLoginErrorCode.FAILURE_ELSE.ordinal
                                    newl_pbar.visibility = View.GONE
                                    newl_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                                    newl_tv_one.text = " ❗ Fatal Error"
                                    newl_tv_one.startAnimation(shake_anim)
                                }
                        }else{

                            logincode = NewLoginErrorCode.FAILURE_LOGIN_INFO_WRONG.ordinal
                            Log.d("func trylogin fail", "nosuchuser")
                            newl_pbar.visibility = View.GONE
                            newl_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                            newl_tv_one.text = " ❗ 이메일 또는 비밀번호가 틀렸습니다."
                            newl_tv_one.startAnimation(shake_anim)
                        }
                    }*/


                            //tryLogin(lemail, lpw)
                    /*Log.d("newlogin200", "sssresultcode "+logincode)

                    Log.d("newlogin201", "sssr")

                    if(logincode == NewLoginErrorCode.FAILURE_LOGIN_INFO_WRONG.ordinal){
                        newl_pbar.visibility = View.GONE
                        newl_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                        newl_tv_one.text = " ❗ Please Check Your E-Mail or Password."
                        newl_tv_one.startAnimation(shake_anim)

                    }else if(logincode == NewLoginErrorCode.FAILURE_ELSE.ordinal){
                        newl_pbar.visibility = View.GONE
                        newl_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                        newl_tv_one.text = " ❗ Fatal Error"
                        newl_tv_one.startAnimation(shake_anim)

                    }else if(logincode == NewLoginErrorCode.LOGIN_SUCCESS.ordinal){
                        Log.d("newlogin210", "ssss")
                        newl_pbar.visibility = View.GONE
                        newl_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorGreen1))
                        newl_tv_one.text = "Welcome, "+UserName

                    }*/
                }
            }
        }

        newl_btn_withdraw.setOnClickListener {
            var withdrawDialog = AlertDialog.Builder(this)
            withdrawDialog.setTitle("회원 탈퇴").setMessage("탈퇴하시겠습니까? 유저 데이터가 즉시 삭제됩니다.")
            withdrawDialog.setPositiveButton(
                "확인",
                DialogInterface.OnClickListener { dialogInterface, which ->
                    CoroutineScope(Main).launch {
                        val withdrawResult = FBUserInfo.setUserWithdrawal()

                        if (withdrawResult) {
                            FBUserInfo.setSignOut()
                            Toast.makeText(applicationContext, "탈퇴되었습니다.", Toast.LENGTH_SHORT)
                                .show()
                            delay(2000)
                            finish()
                        } else {
                            Toast.makeText(applicationContext, "탈퇴에 실패하였습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                })
            withdrawDialog.setNegativeButton("취소", null)
            withdrawDialog.create()
            withdrawDialog.show()
        }

        newl_btn_forgotpw.setOnClickListener {
            //val bundle = Bundle()
            //findPwFrag = NewFindPWFragment()
            //findPwFrag!!.arguments = bundle
            //supportFragmentManager.beginTransaction().replace(R.id.newl_frame, findPwFrag!!, findPwFrag!!.javaClass.simpleName).commit()

            var ftr = supportFragmentManager.beginTransaction()
            ftr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            ftr.replace(R.id.newl_frame, NewFindPWFragment())
            ftr.addToBackStack(null)
            ftr.commit()
        }*/

    }


    suspend fun tryLogin(get_email: String, get_pw: String){
        var resultcode = 0

            fbauth.signInWithEmailAndPassword(get_email, get_pw).addOnCompleteListener {
                    if (it.isSuccessful) {
                        user = FirebaseAuth.getInstance().currentUser
                        userUid = user?.uid ?: "nun"

                        val docRef = fbdb.collection("AccountGroup").document(userUid)
                        docRef.get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                                    UserName = document.getString("name").toString()
                                    resultcode = 1
                                    logincode = NewLoginErrorCode.LOGIN_SUCCESS.ordinal
                                } else {
                                    Log.d("TAG", "No such document")
                                    logincode = NewLoginErrorCode.FAILURE_ELSE.ordinal
                                }
                            }.addOnFailureListener { exception ->
                                Log.d("TAG", "get failed with ", exception)
                                logincode = NewLoginErrorCode.FAILURE_ELSE.ordinal
                            }
                    }else{
                        resultcode = 2
                        logincode = NewLoginErrorCode.FAILURE_LOGIN_INFO_WRONG.ordinal
                        Log.d("func trylogin fail", "nosuchuser")
                    }
                }
                //return 3


            //job1.join()

            //Log.d("func trylogin", "resc: "+resultcode)


        /*when(resultcode){
            0 -> return NewLoginErrorCode.FAILURE_ELSE.ordinal
            1 -> return NewLoginErrorCode.LOGIN_SUCCESS.ordinal
            2 -> return NewLoginErrorCode.FAILURE_LOGIN_INFO_WRONG.ordinal
            else -> return NewLoginErrorCode.FAILURE_ELSE.ordinal
        }*/

        //return NewLoginErrorCode.FAILURE_ELSE.ordinal
    }

    fun tryLogin_beta(get_email: String, get_pw: String){
        var resultcode = 0
        logincode = -1

        //val job1 = CoroutineScope(IO).launch {
        fbauth.signInWithEmailAndPassword(get_email, get_pw).addOnCompleteListener {
            if (it.isSuccessful) {
                user = FirebaseAuth.getInstance().currentUser
                userUid = user?.uid ?: "nun"

                val docRef = fbdb.collection("AccountGroup").document(userUid)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                            UserName = document.getString("name").toString()
                            resultcode = 1
                            logincode = NewLoginErrorCode.LOGIN_SUCCESS.ordinal
                        } else {
                            Log.d("TAG", "No such document")
                            logincode = NewLoginErrorCode.FAILURE_ELSE.ordinal
                        }
                    }.addOnFailureListener { exception ->
                        Log.d("TAG", "get failed with ", exception)
                        logincode = NewLoginErrorCode.FAILURE_ELSE.ordinal
                    }
            }else{
                resultcode = 2
                logincode = NewLoginErrorCode.FAILURE_LOGIN_INFO_WRONG.ordinal
                Log.d("func trylogin fail", "nosuchuser")
            }
        }
        //}

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(resultCode){
            16 -> finish()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}