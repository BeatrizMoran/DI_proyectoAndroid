package com.example.registroseries.modelo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginVM: ViewModel() {
/*    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    private val sharedPref = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val validUser = "admin"
    private val validPassword = "1234"

    fun login(username: String, password: String) {
        if (username == validUser && password == validPassword) {
            saveSession(true)
            _loginSuccess.value = true
        } else {
            _loginSuccess.value = false
        }
    }

    private fun saveSession(isLoggedIn: Boolean) {
        sharedPref.edit().putBoolean("is_logged_in", isLoggedIn).apply()
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPref.getBoolean("is_logged_in", false)
    }

    fun logout() {
        sharedPref.edit().putBoolean("is_logged_in", false).apply()
    }*/
}