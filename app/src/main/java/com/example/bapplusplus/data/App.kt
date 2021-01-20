package com.example.bapplusplus.data

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App : Application() {

    companion object {
        lateinit var prefs : MySharedPreferences
    }

    override fun onCreate() {
        prefs = MySharedPreferences(applicationContext)

        if(prefs.isAutoLogin){
            CoroutineScope(Dispatchers.IO).launch {
                FBUserInfo.setLogin(prefs.emailValue, prefs.passwordValue)
                println("App. Auto Login, "+prefs.emailValue+" / "+ prefs.passwordValue)
            }
        }
        super.onCreate()
    }
}