package com.example.bapplusplus

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FBUserInfo {
    companion object fbuserinfo{
        var userUid = "initialuid"
        var userName = "initialname"

        //var loginState = false
        var userEmail = "initialemail"
        var fbauth : FirebaseAuth = FirebaseAuth.getInstance()
        var fbdb : FirebaseFirestore = FirebaseFirestore.getInstance()
        var fbuser : FirebaseUser? = fbauth.currentUser
        var loginState = fbuser != null

        suspend fun setLogin(get_email: String, get_pw: String): Boolean{

            return try{
                val login_data = fbauth.signInWithEmailAndPassword(get_email, get_pw).await()
                fbuser = login_data.user
                userUid = fbuser?.uid ?: "failuid"
                userEmail = fbuser?.email.toString()
                //Log.d("fbu Testing -1", userName)
                //userName = setNameFromData(userUid)
                userName = fbuser?.displayName.toString()
                //Log.d("FBU meta", fbuser?.multiFactor.toString())
                //Log.d("fbu Testing", userName)
                loginState = true

                return true
            }catch (e: Exception){
                Log.d("fbu Testing", "fail")
                return false
            }
        }

        suspend fun setNameFromData(uid: String): String{
            return try{
                val data = fbdb.collection("AccountGroup").document(uid).get().await()
                //userName = data.getString("Name").toString()
                return data.getString("UserName").toString()
            }catch (e: Exception){
                return "failname"
            }
        }

        fun setSignOut(){
            userUid = "logoutuid"
            userName = "logoutname"
            userEmail = "logoutemail"

            loginState = false
            fbauth.signOut()
            fbuser = fbauth.currentUser
        }
    }

    //lateinit var fbauth : FirebaseAuth
    //lateinit var fbdb : FirebaseFirestore

    constructor(){
        //fbauth = FirebaseAuth.getInstance()
        //fbdb = FirebaseFirestore.getInstance()
        fbuser = fbauth.currentUser

        if(fbuser != null){
            userUid = fbuser!!.uid
            val docRef = fbdb.collection("AccountGroup").document(userUid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                        //userName = document.getString("UserName").toString()
                        userName = fbuser!!.displayName.toString()
                        loginState = true
                    } else {
                        Log.d("TAG", "No such document")

                    }
                }.addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)

                }
        }else{

        }
    }

}