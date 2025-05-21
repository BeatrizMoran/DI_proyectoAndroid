package com.example.registroseries

import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
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
import com.example.registroseries.modelo.Serie
import com.example.registroseries.modelo.Usuario
import com.example.registroseries.utils.mostrarCalendarioConFecha
import java.util.Date
import java.util.Locale


class SerieCreateFragment : Fragment() {
    private var _binding: FragmentSerieCreateBinding? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var fechaProximoEstreno: Date? = null

    private var imagenSeleccionadaBytes: ByteArray? = null // almacena la imagen para la serie


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

    private fun uriToByteArray(uri: Uri): ByteArray? {
        return requireContext().contentResolver.openInputStream(uri)?.use {
            it.readBytes()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cbFechaEmision = binding.scfcbFechaEmision
        val etFechaEmision = binding.etFechaEmision
        val ivImagen = binding.ivImagen
        val subirImagen = binding.scfllImagen


        // Registrar launcher para seleccionar imagen
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri != null) {
                    ivImagen.setImageURI(uri)
                    // Convertimos la imagen a bytes y la almacenamos
                    imagenSeleccionadaBytes = uriToByteArray(uri)
                }
            }

        // Al hacer clic en la imagen, abre la galería
        subirImagen.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // Muestra un DatePicker al hacer clic y pone la fecha seleccionada en el EditText.
        binding.etFechaEmision.setOnClickListener {
            mostrarCalendarioConFecha(requireContext(), binding.etFechaEmision) { fecha ->
                fechaProximoEstreno = fecha
            }
        }


        cbFechaEmision.setOnCheckedChangeListener { _, isChecked ->
            etFechaEmision.visibility = if (isChecked) View.VISIBLE else View.GONE
        }



        binding.btnCrearSerie.setOnClickListener {
            val serie = validarDatos()

            if (serie != null) {

                (activity as MainActivity).serieViewModel.insertarSerie(serie)
                findNavController().navigate(R.id.action_serieCreateFragment_to_seriesListFragment)

            }



            /*
        binding.suftvIrLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }*/
        }


    }
    fun mostrarMensajePersonalizado(message: String, layoutRes: Int) {
        val toast = Toast(requireContext())
        val inflater = layoutInflater
        val layout = inflater.inflate(layoutRes, null)

        val textViewId = if (layoutRes == R.layout.custom_toast_info) {
            R.id.ctitext
        } else {
            R.id.text
        }

        val text = layout.findViewById<TextView>(textViewId)
        text?.text = message

        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }

    fun validarDatos(): Serie? {
        val errores = StringBuilder()

        if (binding.inputTitulo.text.toString().isBlank()) {
            errores.append("El campo: Titulo es obligatorio\n")
            binding.inputTitulo.setBackgroundColor(Color.parseColor("#FFCDD2"))
        } else {
            binding.scftvTitulo.setBackgroundColor(Color.TRANSPARENT)
        }

        if (binding.inputGenero.text.toString().any { it.isDigit() }) {
            errores.append("El campo 'Género' no puede contener valores numéricos\n")
            binding.inputGenero.setBackgroundColor(Color.parseColor("#FFCDD2"))
        } else {
            binding.inputGenero.setBackgroundColor(Color.TRANSPARENT)
        }

        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatoFecha.isLenient = false // Para que valide fechas incorrectas como 32/01/2024

        if (binding.etFechaEmision.text.toString().isNotBlank()){
            try {
                formatoFecha.parse(binding.etFechaEmision.text.toString())
                if(formatoFecha.parse(binding.etFechaEmision.text.toString()) < Date()){
                    errores.append("La fecha de emision no puede ser anteror a la fecha actual\n")

                }
            } catch (e: Exception) {
                errores.append("El campo 'Fecha Emisión' no tiene un formato de fecha adecuado (dd/MM/yyyy)\n")
            }
        }

        // Mostrar errores si hay
        if (errores.isNotEmpty()) {
            mostrarMensajePersonalizado(errores.toString(), R.layout.custom_toast_layout)
            return null
        }

        // Crear y retornar la serie si todo está bien
        return Serie(
            titulo = binding.inputTitulo.text.toString(),
            genero = binding.inputGenero.text.toString(),
            temporadaActual = binding.inputTemporadaActual.text.toString().toIntOrNull()
                ?: 0,
            captituloActual = binding.inputCapituloActual.text.toString().toIntOrNull()
                ?: 0,
            puntuacion = binding.scfetnPuntuacion.text.toString().toDoubleOrNull() ?: 0.0,
            fechaProximoEstreno = fechaProximoEstreno,
            estadoVisualizacion = binding.spinnerEstadoUsuario.selectedItem.toString(),
            serieEnEmision = binding.switchFinalizada.isChecked,
            notas = binding.etNotas.text.toString(),
            imagenUrl = imagenSeleccionadaBytes,
            fechaCreacion = Date()
        )


    }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }


