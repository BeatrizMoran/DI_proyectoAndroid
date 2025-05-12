package com.example.registroseries.modelo

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.registroseries.MainActivity

class SignUpVM(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("usuario", Context.MODE_PRIVATE)

    fun registrarUsuario(usuario: Usuario) {
        val editor = sharedPreferences.edit()
        editor.putString("nombre", usuario.nombre)
        editor.putString("apellidos", usuario.apellidos)
        editor.putString("email", usuario.email)
        editor.putString("password", usuario.password)
        editor.apply()
    }

    fun obtenerUsuario(): Usuario? {
        val nombre = sharedPreferences.getString("nombre", null)
        val apellidos = sharedPreferences.getString("apellidos", null)
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)

        return if (nombre != null && apellidos != null && email != null && password != null) {
            Usuario(nombre, apellidos, email, password)
        } else {
            null
        }
    }
}
