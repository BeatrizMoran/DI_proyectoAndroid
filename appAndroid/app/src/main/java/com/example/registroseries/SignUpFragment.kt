package com.example.registroseries

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.registroseries.databinding.FragmentLoginBinding
import com.example.registroseries.databinding.FragmentSignUpBinding
import com.example.registroseries.modelo.Usuario
import com.example.registroseries.utils.mostrarMensajePersonalizado


class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root

    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sufbSignUp.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmación")
                .setMessage("Al crear unanueva cuenta, se borraran todos los datos de la cuenta anterior si tiene")
                .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss()
                    val usuario = validarDatos()

                    if (usuario != null) {
                        (activity as MainActivity).signUpVM.registrarUsuario(usuario)
                        mostrarMensajePersonalizado(requireContext(),
                            "Usuario registrado correctamente. Inicia sesión",
                            R.layout.custom_toast_info)


                        // Navega al siguiente fragmento
                        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss()
                }
                .show()

        }

        binding.suftvIrLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }

    fun validarDatos(): Usuario? {
        val campos = listOf(
            Pair(binding.suftvNombre, "Nombre"),
            Pair(binding.suftvApellidos, "Apellidos"),
            Pair(binding.suftvEmail, "Email"),
            Pair(binding.suftvPassword, "Contraseña"),
            Pair(binding.suftvPassword2, "Confirmar Contraseña")
        )

        val errores = StringBuilder()
        var hayErrores = false

        // Validación de campos vacíos
        for ((editText, nombreCampo) in campos) {
            if (editText.text.toString().isBlank()) {
                errores.append("El campo: $nombreCampo es obligatorio\n")
                editText.setBackgroundColor(Color.parseColor("#FFCDD2"))
                hayErrores = true
            } else {
                editText.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        // Validación de contraseñas
        val password = binding.suftvPassword.text.toString()
        val password2 = binding.suftvPassword2.text.toString()

        if (!hayErrores && password != password2) {
            errores.append("Las contraseñas no coinciden\n")
            binding.suftvPassword.setBackgroundColor(Color.parseColor("#FFCDD2"))
            binding.suftvPassword2.setBackgroundColor(Color.parseColor("#FFCDD2"))
            hayErrores = true
        }

        // Mostrar errores si hay
        if (errores.isNotEmpty()) {
            mostrarMensajePersonalizado(requireContext(),
                errores.toString(),
                R.layout.custom_toast_layout)
            return null
        }

        // Crear y retornar el usuario si todo está bien
        return Usuario(
            nombre = binding.suftvNombre.text.toString().trim(),
            apellidos = binding.suftvApellidos.text.toString().trim(),
            email = binding.suftvEmail.text.toString().trim(),
            password = password.trim()
        )
    }







    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}