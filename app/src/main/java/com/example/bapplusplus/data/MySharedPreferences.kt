package com.example.bapplusplus.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class MySharedPreferences(context: Context) {

    val PREFS_FILENAME = "prefs"
    val PREF_KEY_MY_EDITTEXT = "myEditText"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    val PREF_AUTO_LOGIN = "AUTO_LOGIN"
    val PREF_MAINTAIN_EMAIL = "MAINTAIN_EMAIL"
    val PREF_EMAIL_VALUE = "EMAIL_VALUE"
    val PREF_PASSWORD_VALUE = "PASSWORD_VALUE"


    var myEditText: String
        get() = prefs.getString(PREF_KEY_MY_EDITTEXT, "")!!
        set(value) = prefs.edit().putString(PREF_KEY_MY_EDITTEXT, value).apply()

    var isAutoLogin: Boolean
        get() = prefs.getBoolean(PREF_AUTO_LOGIN, false)!!
        set(value) = prefs.edit().putBoolean(PREF_AUTO_LOGIN, value).apply()

    var isMaintainEmail: Boolean
        get() = prefs.getBoolean(PREF_MAINTAIN_EMAIL, false)!!
        set(value) = prefs.edit().putBoolean(PREF_MAINTAIN_EMAIL, value).apply()

    var emailValue: String
        get() = prefs.getString(PREF_EMAIL_VALUE, "")!!
        set(value) = prefs.edit().putString(PREF_EMAIL_VALUE, value).apply()

    var passwordValue: String
        get() = prefs.getString(PREF_PASSWORD_VALUE, "")!!
        set(value) = prefs.edit().putString(PREF_PASSWORD_VALUE, value).apply()

    fun setFirstLogin(){
        if(isAutoLogin){
            CoroutineScope(Main).launch {
                FBUserInfo.setLogin(emailValue, passwordValue)
            }
        }
    }

}