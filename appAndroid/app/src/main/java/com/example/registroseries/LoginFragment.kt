package com.example.registroseries

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isNotEmpty
import androidx.navigation.fragment.findNavController
import com.example.registroseries.databinding.FragmentFirstBinding
import com.example.registroseries.databinding.FragmentLoginBinding
import com.example.registroseries.utils.mostrarMensajePersonalizado
import com.google.android.material.bottomnavigation.BottomNavigationView


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        binding.lfbLogin.setOnClickListener {
            comprobarIniciarSesion()
        }
        binding.lftvIrSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        //(activity as MainActivity).loginVM.loginExitoso.removeObservers(viewLifecycleOwner)
    }

    fun comprobarIniciarSesion() {
        val email = binding.lftvEmail.text.toString()
        val password = binding.lftvPassword.text.toString()

        if (email.isBlank() || password.isBlank()) {
            mostrarMensajePersonalizado(
                requireContext(),
                "Debes completar ambos campos", R.layout.custom_toast_layout
            )
            return
        }

        (activity as MainActivity).loginVM.login(email, password)

        if ((activity as MainActivity).loginVM.loginExitoso){
            //findNavController().navigate(R.id.action_loginFragment_to_seriesListFragment)
            findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
        }else{
            mostrarMensajePersonalizado(requireContext(),
                "Usuario o contraseña incorrectos", R.layout.custom_toast_layout)
        }





    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        // ocultar opciones del menu
        menu.findItem(R.id.action_cargar_datos_formulario)?.isVisible = false
        menu.findItem(R.id.action_limpiar_campos_formulario)?.isVisible = false
        menu.findItem(R.id.action_logout)?.isVisible = false
    }



}