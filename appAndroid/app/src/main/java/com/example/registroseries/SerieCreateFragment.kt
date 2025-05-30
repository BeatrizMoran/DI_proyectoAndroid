package com.example.registroseries

import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.registroseries.databinding.FragmentSerieCreateBinding
import com.example.registroseries.modelo.Serie
import com.example.registroseries.utils.mostrarCalendarioConFecha
import com.example.registroseries.utils.mostrarMensajePersonalizado
import java.util.Date
import java.util.Locale

class SerieCreateFragment : Fragment() {
    private var _binding: FragmentSerieCreateBinding? = null
    private val binding get() = _binding!!

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var fechaProximoEstreno: Date? = null
    private var estadoVisualizacion: String = "Viendo"
    private var imagenSeleccionadaBytes: ByteArray? = null
    private var serieEnEmision: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
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
        cbFechaEmision.visibility = if (serieEnEmision) View.VISIBLE else View.GONE

        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri != null) {
                    ivImagen.setImageURI(uri)
                    imagenSeleccionadaBytes = uriToByteArray(uri)
                }
            }

        subirImagen.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.etFechaEmision.setOnClickListener {
            mostrarCalendarioConFecha(requireContext(), binding.etFechaEmision) { fecha ->
                fechaProximoEstreno = fecha
            }
        }

        cbFechaEmision.setOnCheckedChangeListener { _, isChecked ->
            etFechaEmision.visibility = if (isChecked && serieEnEmision) View.VISIBLE else View.GONE
            if (!isChecked) {
                fechaProximoEstreno = null
                etFechaEmision.setText("")
            }
        }

        cbProgresoSerie.setOnCheckedChangeListener { _, isChecked ->
            binding.scfllProgresoTemporadaCapitulo.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding.btnCrearSerie.setOnClickListener {
            val serie = validarDatos()

            if (serie != null) {
                (activity as MainActivity).serieViewModel.insertarSerie(serie) { serieInsertada ->
                    if (serieInsertada) {
                        mostrarMensajePersonalizado(
                            requireContext(),
                            "Serie creada", R.layout.custom_toast_info
                        )
                        findNavController().navigate(R.id.action_serieCreateFragment_to_seriesListFragment)
                    } else {
                        mostrarMensajePersonalizado(
                            requireContext(),
                            "Error.- Ya has guardado una serie con ese titulo", R.layout.custom_toast_layout
                        )
                    }
                }
            }
        }

        binding.spinnerEstadoUsuario.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                estadoVisualizacion = parent.getItemAtPosition(position).toString()
                binding.scfcbProgresoSerie.visibility = if (estadoVisualizacion == "Viendo") View.VISIBLE else View.GONE
                binding.scfcbProgresoSerie.isChecked = if (estadoVisualizacion == "Viendo") true else false

                if (estadoVisualizacion != "Viendo"){
                    binding.scfcbProgresoSerie.isChecked = false
                    binding.inputTemporadaActual.text = null
                    binding.inputCapituloActual.text = null
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.switchFinalizada.setOnCheckedChangeListener { _, isChecked ->
            serieEnEmision = isChecked
            //limpiarCampos
            if(!isChecked){
                cbFechaEmision.isChecked = false
                etFechaEmision.text = null
            }
            cbFechaEmision.visibility = if (serieEnEmision) View.VISIBLE else View.GONE
            etFechaEmision.visibility = if (serieEnEmision && cbFechaEmision.isChecked) View.VISIBLE else View.GONE
        }

        binding.scfcbProgresoSerie.setOnCheckedChangeListener { _, isChecked ->
            var guardar = isChecked
            binding.scfllProgresoTemporadaCapitulo.visibility =  if (guardar) View.VISIBLE else View.GONE
        }
    }

    fun validarDatos(): Serie? {
        val errores = StringBuilder()

        if (binding.inputTitulo.text.toString().isBlank()) {
            errores.append("El campo: Titulo es obligatorio\n")
            binding.inputTitulo.setBackgroundColor(Color.parseColor("#FFCDD2"))
        } else {
            binding.inputTitulo.setBackgroundColor(Color.TRANSPARENT)
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
        formatoFecha.isLenient = false

        if (binding.scfcbFechaEmision.isChecked && fechaProximoEstreno != null) {
            if (fechaProximoEstreno!! < Date()) {
                errores.append("La fecha de emisión no puede ser anterior a hoy\n")
            }
        }

        if (errores.isNotEmpty()) {
            mostrarMensajePersonalizado(
                requireContext(),
                errores.toString(), R.layout.custom_toast_layout
            )
            return null
        }

        return Serie(
            titulo = binding.inputTitulo.text.toString(),
            genero = binding.inputGenero.text.toString(),
            temporadaActual = if(binding.scfcbProgresoSerie.isChecked) binding.inputTemporadaActual.text.toString().toIntOrNull() else null,
            captituloActual = if (binding.scfcbProgresoSerie.isChecked) binding.inputCapituloActual.text.toString().toIntOrNull() else null,
            puntuacion = puntuacion,
            fechaProximoEstreno = if (binding.scfcbFechaEmision.isChecked) fechaProximoEstreno else null,
            estadoVisualizacion = binding.spinnerEstadoUsuario.selectedItem.toString(),
            serieEnEmision =if(binding.switchFinalizada.isChecked) binding.switchFinalizada.isChecked else false,
            notas = binding.etNotas.text.toString(),
            imagenUrl = imagenSeleccionadaBytes,
            fechaCreacion = Date()
        )
    }

    fun cargarDatosPruebaFormulario() {
        binding.inputTitulo.setText("The Walking Dead")
        binding.scfetnPuntuacion.setText("10")
        binding.inputGenero.setText("Terror")
        binding.scfcbProgresoSerie.isChecked = true
        binding.inputTemporadaActual.setText("7")
        binding.inputCapituloActual.setText("1")
        binding.etNotas.setText("Llegará el día en que no estarás...")

        binding.ivImagen.setImageResource(R.drawable.twd)

        val drawable = requireContext().resources.getDrawable(R.drawable.twd, null)
        val bitmap = (drawable as android.graphics.drawable.BitmapDrawable).bitmap
        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
        imagenSeleccionadaBytes = stream.toByteArray()
    }

    fun limpiarCamposFormulario() {
        binding.inputTitulo.setText("")
        binding.scfetnPuntuacion.setText("")
        binding.inputGenero.setText("")
        binding.inputTemporadaActual.setText("")
        binding.inputCapituloActual.setText("")
        binding.etNotas.setText("")
        binding.etFechaEmision.setText("")

        binding.scfcbProgresoSerie.isChecked = false
        binding.scfcbFechaEmision.isChecked = false
        binding.switchFinalizada.isChecked = false

        binding.scfllProgresoTemporadaCapitulo.visibility = View.GONE
        binding.scfcbFechaEmision.visibility = View.GONE

        binding.spinnerEstadoUsuario.setSelection(0)

        binding.ivImagen.setImageDrawable(null)
        imagenSeleccionadaBytes = null

        fechaProximoEstreno = null

        binding.inputTitulo.setBackgroundColor(Color.TRANSPARENT)
        binding.scfetnPuntuacion.setBackgroundColor(Color.TRANSPARENT)
        binding.inputGenero.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_cambiar_vista)?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cargar_datos_formulario -> {
                cargarDatosPruebaFormulario()
                true
            }
            R.id.action_limpiar_campos_formulario -> {
                limpiarCamposFormulario()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Función para formatear fecha si la necesitas
    fun formatoFecha(date: Date): String {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formato.format(date)
    }
}
