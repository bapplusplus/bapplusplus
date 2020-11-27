package com.example.bapplusplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_test_login.*

class TestLoginActivity : AppCompatActivity() {
    var lemail: String = ""
    var lpw: String = ""
    var user: FirebaseUser? = null
    var userUid: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_login)

        var fbauth = FirebaseAuth.getInstance()
        var fbdb = FirebaseFirestore.getInstance()
        user = fbauth.currentUser

        if(user == null){
            Toast.makeText(applicationContext, "Not Logged In",  Toast.LENGTH_SHORT).show()
            tl_tv_test1.text = "Not Logged In"
            tl_btn_signout.visibility = View.INVISIBLE
        }
        else{
            tl_tv_test1.text = "Logged in as " + user!!.email
            tl_btn_signout.visibility = View.VISIBLE
        }


        tl_btn_login.setOnClickListener {
            lemail = tl_til_email_edt.text.toString().trim()
            lpw = tl_til_pw_edt.text.toString().trim()

            if(lemail.equals("")){
                Toast.makeText(applicationContext, "Email empty",  Toast.LENGTH_SHORT).show()
            }else if (lpw.equals("")){
                Toast.makeText(applicationContext, "Password empty",  Toast.LENGTH_SHORT).show()
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
                                } else {
                                    Log.d("TAG", "No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d("TAG", "get failed with ", exception)
                            }

                        Toast.makeText(applicationContext, "Login Success",  Toast.LENGTH_SHORT).show()


                    }else{
                        Toast.makeText(applicationContext, "Login failed. Please try again.",  Toast.LENGTH_SHORT).show()
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
        }
    }
}