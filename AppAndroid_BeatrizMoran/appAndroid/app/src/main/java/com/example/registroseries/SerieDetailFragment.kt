package com.example.registroseries

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.registroseries.databinding.FragmentSerieDetailBinding
import com.example.registroseries.modelo.Serie
import com.example.registroseries.modelo.SerieVM
import com.example.registroseries.utils.formatearFecha
import com.example.registroseries.utils.mostrarCalendarioConFecha
import com.example.registroseries.utils.mostrarMensajePersonalizado
import com.example.registroseries.utils.parsearFecha
import java.util.*

class SerieDetailFragment : Fragment() {
    private var _binding: FragmentSerieDetailBinding? = null
    private val binding get() = _binding!!

    private var id: Int? = null
    private lateinit var viewModel: SerieVM
    private var imagenSeleccionadaBytes: ByteArray? = null
    private var imagenOriginalBytes: ByteArray? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var serieFiltrada: Serie? = null
    private var fechaProximoEstreno: Date? = null
    private var serieEnEmision: Boolean = false
    private var estadoVisualizacion: String = "Viendo"
    private var accion: String = "ver"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentSerieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setInputsEditable(parent: ViewGroup, editable: Boolean) {
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            when (view) {
                is EditText, is Spinner, is Switch, is CheckBox -> {
                    view.isEnabled = editable
                    view.setBackgroundColor(Color.TRANSPARENT)
                }
                is ImageView -> view.isEnabled = editable
                is ViewGroup -> setInputsEditable(view, editable)
            }
        }
    }

    private fun actualizarModo() {
        val editable = accion == "editar"
        setInputsEditable(binding.serieDetailLayout, editable)
        binding.sdfbCancelarEdicion.visibility = if (editable) View.VISIBLE else View.GONE
        binding.sdfbEditar.visibility = if (!editable) View.VISIBLE else View.GONE
        binding.sdfbActualizar.visibility = if (editable) View.VISIBLE else View.GONE
        binding.sdfbCambiarImagen.visibility = if (editable) View.VISIBLE else View.GONE
    }

    private fun uriToByteArray(uri: Uri): ByteArray? {
        return requireContext().contentResolver.openInputStream(uri)?.use { it.readBytes() }
    }

    private fun inicializarDatos(usandoImagenSeleccionada: Boolean = true) {
        val spinner = binding.sdfspinnerEstadoVisualizacion
        val cbFechaEmision = binding.scfcbFechaEmision

        serieFiltrada?.let { serie ->
            binding.sdfinputTitulo.setText(serie.titulo)
            binding.sdfetnPuntuacion.setText(serie.puntuacion?.toString() ?: "")
            binding.sdftilGenero.setText(serie.genero)
            binding.inputTemporadaActual.setText(serie.temporadaActual?.toString() ?: "")
            binding.inputCapituloActual.setText(serie.captituloActual?.toString() ?: "")
            binding.sdfcbProgresoSerie.isChecked = serie.temporadaActual != null

            val estadoActual = serie.estadoVisualizacion
            (0 until spinner.adapter.count).find {
                spinner.adapter.getItem(it) == estadoActual
            }?.let { spinner.setSelection(it) }

            binding.sdfswitchFinalizada.isChecked = serie.serieEnEmision ?: false
            serieEnEmision = serie.serieEnEmision ?: false
            fechaProximoEstreno = serie.fechaProximoEstreno

            cbFechaEmision.visibility = if (serieEnEmision) View.VISIBLE else View.GONE
            cbFechaEmision.isChecked = serie.fechaProximoEstreno != null

            binding.etFechaEmision.setText(serie.fechaProximoEstreno?.let { formatearFecha(it) } ?: "")
            binding.etFechaEmision.hint = if (serie.fechaProximoEstreno == null) "Sin próxima fecha de emisión" else ""
            binding.etFechaEmision.visibility = if (serieEnEmision && cbFechaEmision.isChecked) View.VISIBLE else View.GONE

            binding.sdfetNotas.setText(serie.notas ?: "")
            binding.sdfetNotas.hint = if (serie.notas.isNullOrBlank()) "No hay notas para esta serie" else ""

            when {
                usandoImagenSeleccionada && imagenSeleccionadaBytes != null -> {
                    val bitmap = BitmapFactory.decodeByteArray(imagenSeleccionadaBytes, 0, imagenSeleccionadaBytes!!.size)
                    binding.sdfivImagenSerie.setImageBitmap(bitmap)
                }
                serie.imagenUrl != null -> {
                    imagenOriginalBytes = serie.imagenUrl
                    val bitmap = BitmapFactory.decodeByteArray(serie.imagenUrl, 0, serie.imagenUrl!!.size)
                    binding.sdfivImagenSerie.setImageBitmap(bitmap)
                }
                else -> binding.sdfivImagenSerie.setImageResource(R.drawable.serie_default_image)
            }

            binding.sdfbCambiarImagen.setOnClickListener {
                imagePickerLauncher.launch("image/*")
            }
        }

        val esViendo = spinner.selectedItem.toString() == "Viendo"
        binding.sdfcbProgresoSerie.visibility = if (esViendo) View.VISIBLE else View.GONE
        binding.sdfllprogresoTemporadaCapitulo.visibility = if (esViendo && binding.sdfcbProgresoSerie.isChecked) View.VISIBLE else View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cbProgresoSerie = binding.sdfcbProgresoSerie
        val cbFechaEmision = binding.scfcbFechaEmision
        val etFechaEmision = binding.etFechaEmision

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                binding.sdfivImagenSerie.setImageURI(it)
                imagenSeleccionadaBytes = uriToByteArray(it)
            }
        }

        actualizarModo()
        id = arguments?.getInt("id")
        viewModel = (activity as MainActivity).serieViewModel

        viewModel.listaSeries.observe(viewLifecycleOwner) { lista ->
            serieFiltrada = lista.find { it.id == id }
            inicializarDatos()
        }

        etFechaEmision.setOnClickListener {
            mostrarCalendarioConFecha(requireContext(), etFechaEmision) { fecha ->
                fechaProximoEstreno = fecha
            }
        }

        binding.sdfbEditar.setOnClickListener {
            accion = "editar"
            actualizarModo()
        }

        binding.sdfbCancelarEdicion.setOnClickListener {
            accion = "ver"
            actualizarModo()
            imagenSeleccionadaBytes = null
            inicializarDatos(usandoImagenSeleccionada = false)
        }

        binding.sdfbActualizar.setOnClickListener {
            val serie = validarDatos()
            if (serie != null) {
                viewModel.actualizarSerie(serie) { actualizado ->
                    if (actualizado) {
                        mostrarMensajePersonalizado(requireContext(), "Serie actualizada", R.layout.custom_toast_info)
                        accion = "ver"
                        actualizarModo()
                    } else {
                        mostrarMensajePersonalizado(requireContext(), "Error.- Ya hay una serie con ese titulo", R.layout.custom_toast_layout)
                    }
                }
            }
        }

        binding.sdfbBorrar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmación")
                .setMessage("¿Estás seguro de que quieres borrar la serie?")
                .setPositiveButton("Aceptar") { dialog, _ ->
                    dialog.dismiss()
                    serieFiltrada?.let {
                        viewModel.borrarSerie(it)
                        mostrarMensajePersonalizado(requireContext(), "Serie borrada", R.layout.custom_toast_info)
                        findNavController().navigate(R.id.action_serieDetailFragment_to_seriesListFragment)
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        binding.sdfswitchFinalizada.setOnCheckedChangeListener { _, isChecked ->
            serieEnEmision = isChecked
            cbFechaEmision.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) {
                etFechaEmision.visibility = View.GONE
                cbFechaEmision.isChecked = false
            }
        }

        cbFechaEmision.setOnCheckedChangeListener { _, isChecked ->
            etFechaEmision.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        cbProgresoSerie.setOnCheckedChangeListener { _, isChecked ->
            if (binding.sdfspinnerEstadoVisualizacion.selectedItem.toString() == "Viendo") {
                binding.sdfllprogresoTemporadaCapitulo.visibility = if (isChecked) View.VISIBLE else View.GONE
            }
        }

        binding.sdfspinnerEstadoVisualizacion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                estadoVisualizacion = parent.getItemAtPosition(position).toString()
                val esViendo = estadoVisualizacion == "Viendo"
                binding.sdfcbProgresoSerie.visibility = if (esViendo) View.VISIBLE else View.GONE

                if (!esViendo) {
                    binding.sdfcbProgresoSerie.isChecked = false
                    binding.inputTemporadaActual.setText("")
                    binding.inputCapituloActual.setText("")
                    binding.sdfllprogresoTemporadaCapitulo.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    fun validarDatos(): Serie? {
        val errores = StringBuilder()

        if (binding.sdfinputTitulo.text.toString().isBlank()) {
            errores.append("El campo: Titulo es obligatorio\n")
            binding.sdfinputTitulo.setBackgroundColor(Color.parseColor("#FFCDD2"))
        } else {
            binding.sdfinputTitulo.setBackgroundColor(Color.TRANSPARENT)
        }

        val puntuacion = binding.sdfetnPuntuacion.text.toString().toDoubleOrNull()
        if (puntuacion != null && (puntuacion < 0.0 || puntuacion > 10.0)) {
            errores.append("La puntuación debe estar entre 0 y 10\n")
            binding.sdfetnPuntuacion.setBackgroundColor(Color.parseColor("#FFCDD2"))
        } else {
            binding.sdfetnPuntuacion.setBackgroundColor(Color.TRANSPARENT)
        }

        if (binding.sdftilGenero.text.toString().any { it.isDigit() }) {
            errores.append("El campo 'Género' no puede contener valores numéricos\n")
            binding.sdftilGenero.setBackgroundColor(Color.parseColor("#FFCDD2"))
        } else {
            binding.sdftilGenero.setBackgroundColor(Color.TRANSPARENT)
        }

        val textoFecha = binding.etFechaEmision.text.toString()
        val fechaParseada = if (textoFecha.isNotBlank()) parsearFecha(textoFecha) else null

        if (textoFecha.isNotBlank()) {
            if (fechaParseada == null) {
                errores.append("El campo 'Fecha Emisión' no tiene un formato de fecha adecuado (dd/MM/yyyy)\n")
            } else if (fechaParseada < Date()) {
                errores.append("La fecha de emision no puede ser anterior a la fecha actual\n")
            }
        }

        if (errores.isNotEmpty()) {
            mostrarMensajePersonalizado(requireContext(), errores.toString(), R.layout.custom_toast_layout)
            return null
        }

        return serieFiltrada?.let {
            Serie(
                id = it.id,
                titulo = binding.sdfinputTitulo.text.toString(),
                genero = binding.sdftilGenero.text.toString(),
                temporadaActual = if (binding.sdfcbProgresoSerie.isChecked) binding.inputTemporadaActual.text.toString().toIntOrNull() else null,
                captituloActual = if (binding.sdfcbProgresoSerie.isChecked) binding.inputCapituloActual.text.toString().toIntOrNull() else null,
                puntuacion = puntuacion,
                fechaProximoEstreno = if (binding.scfcbFechaEmision.isChecked) fechaParseada else null,
                estadoVisualizacion = binding.sdfspinnerEstadoVisualizacion.selectedItem.toString(),
                serieEnEmision = binding.sdfswitchFinalizada.isChecked,
                notas = binding.sdfetNotas.text.toString(),
                imagenUrl = imagenSeleccionadaBytes ?: it.imagenUrl,
                fechaCreacion = it.fechaCreacion
            )
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_cargar_datos_formulario)?.isVisible = false
        menu.findItem(R.id.action_limpiar_campos_formulario)?.isVisible = false
        menu.findItem(R.id.action_cambiar_vista)?.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
