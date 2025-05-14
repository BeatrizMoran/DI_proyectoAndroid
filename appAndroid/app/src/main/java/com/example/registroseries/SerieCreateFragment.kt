package com.example.registroseries

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.registroseries.databinding.FragmentSerieCreateBinding
import com.example.registroseries.databinding.FragmentSignUpBinding
import com.example.registroseries.modelo.Usuario


class SerieCreateFragment : Fragment() {
    private var _binding: FragmentSerieCreateBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSerieCreateBinding.inflate(inflater, container, false)
        return binding.root

    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


/*
        binding.suftvIrLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }*/
    }









    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}