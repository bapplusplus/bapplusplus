package com.example.bapplusplus

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_test_signup.*

data class MapFavEl(
    var RestNo: Int,
    var MyRating: Double
)

class TestSignupActivity : AppCompatActivity() {

    var remail: String? = null
    var rname: String? = null
    var rpw: String? = null
    var rpwcheck: String? = null
    var listfav: ArrayList<MapFavEl> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_signup)
        tsu2_til_phone.isEnabled = false
        tsu2_til_phone_edt.isEnabled = false

        var fbauth = FirebaseAuth.getInstance()
        var fbdb = FirebaseFirestore.getInstance()
        //listfav.add(MapFavEl(0, 9.9))

        var shake_anim = AnimationUtils.loadAnimation(applicationContext, R.anim.shake1)

        tsu_container.setOnClickListener {
            CloseKeyboard()
        }

        tsu_btn_reg.setOnClickListener{
            remail = tsu_til_email_edt.text.toString().trim()
            rname = tsu_til_name_edt.text.toString().trim()
            rpw = tsu_til_pw1_edt.text.toString().trim()
            rpwcheck = tsu_til_pw2_edt.text.toString().trim()

            if(rname.isNullOrEmpty()){
                tsu_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                tsu_tv_one.text = " ❗ Enter your Name."
                tsu_tv_one.startAnimation(shake_anim)
            }else if(remail.isNullOrEmpty()){
                tsu_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                tsu_tv_one.text = " ❗ Enter your E-mail Address."
                tsu_tv_one.startAnimation(shake_anim)
            }else if(rpw.isNullOrEmpty()){
                tsu_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                tsu_tv_one.text = " ❗ Enter your Password."
                tsu_tv_one.startAnimation(shake_anim)
            }else if (rpwcheck.isNullOrEmpty()){
                tsu_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                tsu_tv_one.text = " ❗ Confirm your Password."
                tsu_tv_one.startAnimation(shake_anim)
            }else if(rpw.equals(rpwcheck)){
                CloseKeyboard()
                //var mDialog = AlertDialog.Builder(applicationContext)
                tsu_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorOrange1))
                tsu_tv_one.text = "Trying to Sign Up..."
                //tsu_tv_one.startAnimation(shake_anim)
                Toast.makeText(applicationContext, "가입 중",  Toast.LENGTH_SHORT).show()

                fbauth.createUserWithEmailAndPassword(remail!!, rpw!!).addOnCompleteListener(this) {
                    if(it.isSuccessful){
                        val user = fbauth.currentUser
                        var gemail = user?.email
                        var guid = user?.uid
                        //val upreq = UserProfileChangeRequest.Builder().setDisplayName(rname).build()
                        //user?.updateProfile(upreq)?.addOnCompleteListener {  }


//                        var hashmap = HashMap<Object, String>()
//                        hashmap.put("uid", guid)
                        val newUser = hashMapOf(
                            "id" to gemail,
                            "uid" to guid,
                            "name" to rname,
                            "isAdmin" to 0,
                            "favoritesList" to listfav
                        )

                        if (guid != null) {
                            fbdb.collection("AccountGroup").document(guid)
                                .set(newUser)
                                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
                                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
                        }else{
                            Toast.makeText(applicationContext, "uid error",  Toast.LENGTH_SHORT).show()
                        }

                        tsu_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorGreen1))
                        tsu_tv_one.text = " Welcome!"
                        Toast.makeText(applicationContext, "Registration succeeded.",  Toast.LENGTH_SHORT).show()
                        //Handler: Deprecated
                        Handler().postDelayed({finish()}, 1000)
                        //finish()

                    } else{
                        //it failed
                        tsu_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                        tsu_tv_one.text = " ❗ Email address already exists."
                        tsu_tv_one.startAnimation(shake_anim)
                        Toast.makeText(applicationContext, "Registration failed: same email exists.",  Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                CloseKeyboard()
                //pw != pwcheck
                if(!remail!!.contains("@")){
                    tsu_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                    tsu_tv_one.text = " ❗ '@' Missing in E-Mail Address."
                    tsu_tv_one.startAnimation(shake_anim)
                }else{
                    tsu_tv_one.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorRed1))
                    tsu_tv_one.text = " ❗ Confirm your password."
                    tsu_tv_one.startAnimation(shake_anim)
                    Toast.makeText(applicationContext, "pwcheck failed.",  Toast.LENGTH_SHORT).show()
                }

            }

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