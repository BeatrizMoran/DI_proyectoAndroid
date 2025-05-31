package com.example.registroseries

import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.registroseries.databinding.FragmentSerieCreateBinding
import com.example.registroseries.modelo.Serie
import com.example.registroseries.utils.formatearFecha
import com.example.registroseries.utils.mostrarCalendarioConFecha
import com.example.registroseries.utils.mostrarMensajePersonalizado
import com.example.registroseries.utils.parsearFecha
import java.util.*

class SerieCreateFragment : Fragment() {

    private var _binding: FragmentSerieCreateBinding? = null
    private val binding get() = _binding!!

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var fechaProximoEstreno: Date? = null
    private var estadoVisualizacion: String = "Viendo"
    private var imagenSeleccionadaBytes: ByteArray? = null
    private var serieEnEmision: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        _binding = FragmentSerieCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun uriToByteArray(uri: Uri): ByteArray? {
        return requireContext().contentResolver.openInputStream(uri)?.use { it.readBytes() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            scfcbProgresoSerie.visibility = if (estadoVisualizacion == "Viendo") View.VISIBLE else View.GONE
            scfcbFechaEmision.visibility = if (serieEnEmision) View.VISIBLE else View.GONE

            imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    ivImagen.setImageURI(it)
                    imagenSeleccionadaBytes = uriToByteArray(it)
                }
            }

            scfllImagen.setOnClickListener {
                imagePickerLauncher.launch("image/*")
            }

            etFechaEmision.setOnClickListener {
                mostrarCalendarioConFecha(requireContext(), etFechaEmision) { fecha ->
                    fecha?.let {
                        fechaProximoEstreno = it
                        etFechaEmision.setText(formatearFecha(it))
                    }
                }
            }

            scfcbFechaEmision.setOnCheckedChangeListener { _, isChecked ->
                etFechaEmision.visibility = if (isChecked && serieEnEmision) View.VISIBLE else View.GONE
                if (!isChecked) {
                    fechaProximoEstreno = null
                    etFechaEmision.setText("")
                }
            }

            scfcbProgresoSerie.setOnCheckedChangeListener { _, isChecked ->
                scfllProgresoTemporadaCapitulo.visibility = if (isChecked) View.VISIBLE else View.GONE
                if (!isChecked){
                    inputTemporadaActual.text = null
                    inputCapituloActual.text = null
                }
            }

            btnCrearSerie.setOnClickListener {
                validarDatos()?.let { serie ->
                    (activity as MainActivity).serieViewModel.insertarSerie(serie) { fueInsertada ->
                        if (fueInsertada) {
                            mostrarMensajePersonalizado(requireContext(), "Serie creada", R.layout.custom_toast_info)
                            findNavController().navigate(R.id.action_serieCreateFragment_to_seriesListFragment)
                        } else {
                            mostrarMensajePersonalizado(requireContext(), "Error.- Ya has guardado una serie con ese título", R.layout.custom_toast_layout)
                        }
                    }
                }
            }

            spinnerEstadoUsuario.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    estadoVisualizacion = parent.getItemAtPosition(position).toString()
                    val viendo = estadoVisualizacion == "Viendo"
                    scfcbProgresoSerie.visibility = if (viendo) View.VISIBLE else View.GONE
                    scfcbProgresoSerie.isChecked = viendo

                    if (!viendo) {
                        inputTemporadaActual.text = null
                        inputCapituloActual.text = null
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            switchFinalizada.setOnCheckedChangeListener { _, isChecked ->
                serieEnEmision = isChecked
                if (!isChecked) {
                    scfcbFechaEmision.isChecked = false
                    etFechaEmision.text = null
                }
                scfcbFechaEmision.visibility = if (isChecked) View.VISIBLE else View.GONE
                etFechaEmision.visibility = if (isChecked && scfcbFechaEmision.isChecked) View.VISIBLE else View.GONE
            }
        }
    }

    private fun validarDatos(): Serie? {
        val errores = StringBuilder()

        with(binding) {
            if (inputTitulo.text.isNullOrBlank()) {
                errores.append("El campo: Título es obligatorio\n")
                inputTitulo.setBackgroundColor(Color.parseColor("#FFCDD2"))
            } else {
                inputTitulo.setBackgroundColor(Color.TRANSPARENT)
            }

            val puntuacion = scfetnPuntuacion.text.toString().toDoubleOrNull()
            if (puntuacion != null && (puntuacion < 0.0 || puntuacion > 10.0)) {
                errores.append("La puntuación debe estar entre 0 y 10\n")
                scfetnPuntuacion.setBackgroundColor(Color.parseColor("#FFCDD2"))
            } else {
                scfetnPuntuacion.setBackgroundColor(Color.TRANSPARENT)
            }

            if (inputGenero.text.toString().any { it.isDigit() }) {
                errores.append("El campo 'Género' no puede contener valores numéricos\n")
                inputGenero.setBackgroundColor(Color.parseColor("#FFCDD2"))
            } else {
                inputGenero.setBackgroundColor(Color.TRANSPARENT)
            }

            if (scfcbFechaEmision.isChecked && !etFechaEmision.text.isNullOrBlank()) {
                val fechaParseada = parsearFecha(etFechaEmision.text.toString())
                if (fechaParseada == null) {
                    errores.append("El formato de la fecha de emisión no es válido (usa dd/MM/yyyy)\n")
                    etFechaEmision.setBackgroundColor(Color.parseColor("#FFCDD2"))
                } else {
                    etFechaEmision.setBackgroundColor(Color.TRANSPARENT)
                    fechaProximoEstreno = fechaParseada
                }
            }


            if (scfcbFechaEmision.isChecked && fechaProximoEstreno != null && fechaProximoEstreno!! < Date()) {
                errores.append("La fecha de emisión no puede ser anterior a hoy\n")
            }

            if (errores.isNotEmpty()) {
                mostrarMensajePersonalizado(requireContext(), errores.toString(), R.layout.custom_toast_layout)
                return null
            }

            return Serie(
                titulo = inputTitulo.text.toString(),
                genero = inputGenero.text.toString(),
                temporadaActual = if (scfcbProgresoSerie.isChecked) inputTemporadaActual.text.toString().toIntOrNull() else null,
                captituloActual = if (scfcbProgresoSerie.isChecked) inputCapituloActual.text.toString().toIntOrNull() else null,
                puntuacion = puntuacion,
                fechaProximoEstreno = if (scfcbFechaEmision.isChecked) fechaProximoEstreno else null,
                estadoVisualizacion = spinnerEstadoUsuario.selectedItem.toString(),
                serieEnEmision = switchFinalizada.isChecked,
                notas = etNotas.text.toString(),
                imagenUrl = imagenSeleccionadaBytes,
                fechaCreacion = Date()
            )
        }
    }

    fun cargarDatosPruebaFormulario() {
        with(binding) {
            inputTitulo.setText("The Walking Dead")
            scfetnPuntuacion.setText("10")
            inputGenero.setText("Terror")
            scfcbProgresoSerie.isChecked = true
            inputTemporadaActual.setText("7")
            inputCapituloActual.setText("1")
            etNotas.setText("Llegará el día en que no estarás...")
            ivImagen.setImageResource(R.drawable.twd)

            val drawable = requireContext().getDrawable(R.drawable.twd)
            val bitmap = (drawable as android.graphics.drawable.BitmapDrawable).bitmap
            val stream = java.io.ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
            imagenSeleccionadaBytes = stream.toByteArray()
        }
    }

    fun limpiarCamposFormulario() {
        with(binding) {
            inputTitulo.setText("")
            scfetnPuntuacion.setText("")
            inputGenero.setText("")
            inputTemporadaActual.setText("")
            inputCapituloActual.setText("")
            etNotas.setText("")
            etFechaEmision.setText("")
            scfcbProgresoSerie.isChecked = false
            scfcbFechaEmision.isChecked = false
            switchFinalizada.isChecked = false
            scfllProgresoTemporadaCapitulo.visibility = View.GONE
            scfcbFechaEmision.visibility = View.GONE
            spinnerEstadoUsuario.setSelection(0)
            ivImagen.setImageDrawable(null)
            imagenSeleccionadaBytes = null
            fechaProximoEstreno = null
            inputTitulo.setBackgroundColor(Color.TRANSPARENT)
            scfetnPuntuacion.setBackgroundColor(Color.TRANSPARENT)
            inputGenero.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_cambiar_vista)?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cargar_datos_formulario -> {
                cargarDatosPruebaFormulario(); true
            }
            R.id.action_limpiar_campos_formulario -> {
                limpiarCamposFormulario(); true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
