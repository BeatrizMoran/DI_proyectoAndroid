package com.example.registroseries

import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.registroseries.databinding.FragmentSerieCreateBinding
import com.example.registroseries.databinding.FragmentSignUpBinding
import com.example.registroseries.modelo.Usuario


class SerieCreateFragment : Fragment() {
    private var _binding: FragmentSerieCreateBinding? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>


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

        val cbFechaEmision = binding.scfcbFechaEmision
        val etFechaEmision = binding.etFechaEmision
        val ivImagen = binding.ivImagen
        val subirImagen = binding.scfllImagen

        // Registrar launcher para seleccionar imagen
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                ivImagen.setImageURI(uri)
                // Puedes guardar el uri para subirlo a la BD o servidor
            }
        }

        // Al hacer clic en la imagen, abre la galería
        subirImagen.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // Muestra un DatePicker al hacer clic y pone la fecha seleccionada en el EditText.
        etFechaEmision.setOnClickListener {
            val calendario = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val fecha = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                    etFechaEmision.setText(fecha)
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        cbFechaEmision.setOnCheckedChangeListener { _, isChecked ->
            etFechaEmision.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

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