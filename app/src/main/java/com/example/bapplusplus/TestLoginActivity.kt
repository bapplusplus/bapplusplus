package com.example.bapplusplus

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_test_login.*
import kotlinx.android.synthetic.main.activity_test_signup.*

class TestLoginActivity : AppCompatActivity() {
    var lemail: String = ""
    var lpw: String = ""
    var user: FirebaseUser? = null
    var userUid: String = ""
    var UserName =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_login)

        var fbauth = FirebaseAuth.getInstance()
        var fbdb = FirebaseFirestore.getInstance()
        user = fbauth.currentUser
        userUid = user?.uid ?: ""

        var shake_anim = AnimationUtils.loadAnimation(applicationContext, R.anim.shake1)

        if(user == null){
            Toast.makeText(applicationContext, "Not Logged In",  Toast.LENGTH_SHORT).show()
            tl_tv_test1.text = "Not Signed In"

            tl_btn_signout.visibility = View.INVISIBLE
        }
        else{
            //userUid = user!!.uid
            fbdb.collection("AccountGroup").document(userUid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                        //tl_tv_test1.text = "welcome, "+ document.getString("name")
                        UserName = document.getString("name").toString()
                        tl_btn_login.text = UserName
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }

            Handler().postDelayed({
                //tl_btn_login.text = UserName
            }, 360)

            tl_tv_test1.text = "Signed in as " + user!!.email
            tl_tv_one.text = "Signed In"
            tl_btn_signout.visibility = View.VISIBLE
            tl_til_email.visibility = View.GONE
            tl_til_pw.visibility = View.GONE
            //tl_til_email_edt.isEnabled = false
            //tl_til_pw_edt.isEnabled = false
            tl_tv_notmem.visibility = View.GONE
            tl_btn_reg.visibility = View.GONE
            tl_btn_login.text = ""
            tl_btn_login.isEnabled = false
            tl_btn_reg.isEnabled = false
        }

        tl_container.setOnClickListener {
            CloseKeyboard()
        }


        tl_btn_login.setOnClickListener {
            CloseKeyboard()
            lemail = tl_til_email_edt.text.toString().trim()
            lpw = tl_til_pw_edt.text.toString().trim()

            if(lemail.equals("")){
                tl_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                tl_tv_one.text = " ❗ E-mail is empty."
                tl_tv_one.startAnimation(shake_anim)
                //Toast.makeText(applicationContext, "Email empty",  Toast.LENGTH_SHORT).show()
            }else if (lpw.equals("")){
                tl_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                tl_tv_one.text = " ❗ Password is empty."
                tl_tv_one.startAnimation(shake_anim)
                //Toast.makeText(applicationContext, "Password empty",  Toast.LENGTH_SHORT).show()
            } else{
                fbauth.signInWithEmailAndPassword(lemail, lpw).addOnCompleteListener {
                    if(it.isSuccessful){
                        user = FirebaseAuth.getInstance().currentUser
                        userUid = user?.uid ?: "nun"
                        val docRef = fbdb.collection("AccountGroup").document(userUid)
                        docRef.get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                                    tl_tv_test1.text = "welcome, "+ document.getString("name")
                                    UserName = document.getString("name").toString()

                                    Handler().postDelayed({
                                        Toast.makeText(applicationContext, "Login Success",  Toast.LENGTH_SHORT).show()
                                        tl_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorGreen1))
                                        tl_tv_one.text = " Welcome, "+UserName+ " !"
                                        Handler().postDelayed({
                                            finish()
                                        }, 600)
                                    }, 400)

                                } else {
                                    Log.d("TAG", "No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d("TAG", "get failed with ", exception)
                            }
                    }else{
                        tl_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                        tl_tv_one.text = " ❗ Check Your E-mail or PW."
                        tl_tv_one.startAnimation(shake_anim)
                        //Toast.makeText(applicationContext, "Login failed. Please try again.",  Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        tl_btn_signout.setOnClickListener {
            fbauth.signOut()
            finish()
            startActivity(Intent(this, TestLoginActivity::class.java))
        }

        tl_btn_reg.setOnClickListener {
            val intent = Intent(this, TestSignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun CloseKeyboard() {
        var view = this.currentFocus
        if(view != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}