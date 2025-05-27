package com.example.registroseries.modelo

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.registroseries.MainActivity
import com.example.registroseries.bbdd.Repositorio
import com.example.registroseries.bbdd.SerieDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpVM(application: Application, private val miRepositorio: Repositorio) : AndroidViewModel(application) {

    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("usuario", Context.MODE_PRIVATE)

    fun registrarUsuario(usuario: Usuario) {
        val editor = sharedPreferences.edit()
        editor.putString("nombre", usuario.nombre)
        editor.putString("apellidos", usuario.apellidos)
        editor.putString("email", usuario.email)
        editor.putString("password", usuario.password)
        editor.apply()

        // Borrar las series de la base de datos (por si eran del usuario anterior)
        CoroutineScope(Dispatchers.IO).launch {
            miRepositorio.borrarTodasLasSeries()
        }

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

class SignUpViewModelFactory(
    private val application: Application,
    private val miRepositorio: Repositorio
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpVM::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpVM(application, miRepositorio) as T
        }
        throw IllegalArgumentException("ViewModel class desconocida")
    }
}
