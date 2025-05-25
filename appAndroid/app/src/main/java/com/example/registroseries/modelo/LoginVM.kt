package com.example.registroseries.modelo

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginVM(application: Application) : AndroidViewModel(application) {
   //private val _loginExitoso = MutableLiveData<Boolean>()
    //val loginExitoso: LiveData<Boolean> get() = _loginExitoso
    var loginExitoso: Boolean = false

    private val user = application.getSharedPreferences("usuario", Context.MODE_PRIVATE)

    private val emailValido =  user.getString("email", "")

    private val passwdValido = user.getString("password", "")

    fun login(email: String, password: String) {
        if (email == emailValido && password == passwdValido) {
            saveSession(true)
            loginExitoso = true
        } else {
            loginExitoso = false
        }
    }

    private fun saveSession(isLoggedIn: Boolean) {
        user.edit().putBoolean("is_logged_in", isLoggedIn).apply()
    }

    fun isUserLoggedIn(): Boolean {
        return user.getBoolean("is_logged_in", false)
    }

    fun logout() {
        user.edit().putBoolean("is_logged_in", false).apply()
    }


}