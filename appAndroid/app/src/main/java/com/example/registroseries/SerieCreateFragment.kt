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
import android.widget.AdapterView
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
import com.example.registroseries.utils.mostrarMensajePersonalizado
import java.util.Date
import java.util.Locale


class SerieCreateFragment : Fragment() {
    private var _binding: FragmentSerieCreateBinding? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var fechaProximoEstreno: Date? = null

    private var estadoVisualizacion: String = "Viendo"

    private var imagenSeleccionadaBytes: ByteArray? = null // almacena la imagen para la serie
    private var serieEnEMision: Boolean = false


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
        val cbProgresoSerie = binding.scfcbProgresoSerie

        binding.scfcbProgresoSerie.visibility = if (estadoVisualizacion == "Viendo") View.VISIBLE else View.GONE

        binding.scfcbFechaEmision.visibility = if (serieEnEMision) View.VISIBLE else View.GONE



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

        cbProgresoSerie.setOnCheckedChangeListener { _, isChecked ->
            binding.scfllProgresoTemporadaCapitulo.visibility = if (isChecked) View.VISIBLE else View.GONE
        }




        binding.btnCrearSerie.setOnClickListener {
            val serie = validarDatos()

            if (serie != null) {

                (activity as MainActivity).serieViewModel.insertarSerie(serie)
                mostrarMensajePersonalizado(
                    requireContext(),
                    "Serie creada", R.layout.custom_toast_info
                )
                findNavController().navigate(R.id.action_serieCreateFragment_to_seriesListFragment)

            }





            /*
        binding.suftvIrLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }*/
        }
        binding.spinnerEstadoUsuario.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                estadoVisualizacion = parent.getItemAtPosition(position).toString()
                binding.scfcbProgresoSerie.visibility = if (estadoVisualizacion == "Viendo") View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        binding.switchFinalizada.setOnCheckedChangeListener { _, isChecked ->
            serieEnEMision = isChecked
            binding.scfcbFechaEmision.visibility = if (serieEnEMision) View.VISIBLE else View.GONE

        }




    }


    fun validarDatos(): Serie? {
        val errores = StringBuilder()

        if (binding.inputTitulo.text.toString().isBlank()) {
            errores.append("El campo: Titulo es obligatorio\n")
            binding.inputTitulo.setBackgroundColor(Color.parseColor("#FFCDD2"))
        } else {
            binding.scftvTitulo.setBackgroundColor(Color.TRANSPARENT)
        }

        val puntuacion = binding.scfetnPuntuacion.text.toString().toDoubleOrNull()

        if (puntuacion != null && (puntuacion < 0.0 || puntuacion > 10.0)) {
            errores.append("La puntuación debe estar entre 0 y 10\n")
            binding.scfetnPuntuacion.setBackgroundColor(Color.parseColor("#FFCDD2"))
        } else {
            binding.scfetnPuntuacion.setBackgroundColor(Color.TRANSPARENT)
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
            mostrarMensajePersonalizado(
                requireContext(),
                errores.toString(), R.layout.custom_toast_layout
            )
            return null
        }

        // Crear y retornar la serie si todo está bien
        return Serie(
            titulo = binding.inputTitulo.text.toString(),
            genero = binding.inputGenero.text.toString(),
            temporadaActual = binding.inputTemporadaActual.text.toString().toIntOrNull(),
            captituloActual = binding.inputCapituloActual.text.toString().toIntOrNull(),
            puntuacion = binding.scfetnPuntuacion.text.toString().toDoubleOrNull(),
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


