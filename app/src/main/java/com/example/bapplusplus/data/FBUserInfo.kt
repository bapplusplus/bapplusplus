package com.example.bapplusplus.data

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
        var myLikesArray = arrayListOf<Int>()

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

                //App.prefs.emailValue = get_email
                //App.prefs.passwordValue = get_pw
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

            if(!App.prefs.isMaintainEmail){
                App.prefs.emailValue = ""
            }

            App.prefs.isAutoLogin = false
            App.prefs.passwordValue = ""

            loginState = false
            fbauth.signOut()

            fbuser = fbauth.currentUser
        }

        suspend fun setWithdrawal(): Boolean{
            try{
                fbuser?.delete()?.await()
                return true
            }catch (e: Exception){
                Log.e("fbu", "set withdrawal fail, "+e.printStackTrace())
                return false
            }
        }

        suspend fun setUserWithdrawal(): Boolean{
            val withdrawResult = setWithdrawal()
            if(withdrawResult){
                try {
                    fbdb.collection("AccountGroup").document(userUid).delete().await()
                    return true
                }catch (e: Exception){
                    Log.e("fbu", "withdraw data delete fail, "+e.printStackTrace())
                    return false
                }
            }

            return false
        }

        suspend fun getMyLikesArray(){
            myLikesArray.clear()
            if(fbauth.currentUser == null){
                return
            }
            val doc = fbdb.collection("AccountGroup").document(fbauth.currentUser!!.uid).get().await()
            val listGetter = doc.get("MyFavoritesArray") as List<*>
            for(cc in 0..listGetter.size-1){
                var map1 = listGetter.get(cc) as HashMap<*, *>
                myLikesArray.add((map1.get("RestNo") as Long).toInt())
            }
        }
    }

    //lateinit var fbauth : FirebaseAuth
    //lateinit var fbdb : FirebaseFirestore

    constructor(){
        //fbauth = FirebaseAuth.getInstance()
        //fbdb = FirebaseFirestore.getInstance()
        fbuser = fbauth.currentUser

        if(fbuser != null){
            Log.d("fbu", "fbuser is not null"+ fbuser.toString()+": "+ fbuser!!.displayName.toString())
            userUid = fbuser!!.uid
            userName = fbuser!!.displayName.toString()
            loginState = true
            /*val docRef = fbdb.collection("AccountGroup").document(userUid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                        //userName = document.getString("UserName").toString()
                        userName = fbuser!!.displayName.toString()
                        loginState = true
                    } else {
                        Log.d("fbu", "No such document")

                    }
                }.addOnFailureListener { exception ->
                    Log.d("fbu", "get failed with ", exception)

                }*/
        }else{
            loginState = false
            userName ="initial_notLoggedIn"
            Log.d("fbu", "fbuser is null "+ fbuser.toString())
        }
    }

}