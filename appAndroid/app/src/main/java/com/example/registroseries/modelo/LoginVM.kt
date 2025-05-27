package com.example.registroseries.modelo

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginVM(application: Application) : AndroidViewModel(application) {

    var loginExitoso: Boolean = false

    private val user = application.getSharedPreferences("usuario", Context.MODE_PRIVATE)

    fun login(email: String, password: String) {
        val emailValido = user.getString("email", "")
        val passwdValido = user.getString("password", "")

        loginExitoso = (email == emailValido && password == passwdValido)

        if (loginExitoso) {
            saveSession(true)
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
