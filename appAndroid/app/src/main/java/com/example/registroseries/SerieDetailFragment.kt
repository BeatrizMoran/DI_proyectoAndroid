package com.example.registroseries

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
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
import com.example.registroseries.utils.mostrarCalendarioConFecha
import com.example.registroseries.utils.mostrarMensajePersonalizado
import java.text.SimpleDateFormat
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
        _binding = FragmentSerieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setInputsEditable(parent: ViewGroup, editable: Boolean) {
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)

            when (view) {
                is EditText -> {
                    view.isEnabled = editable
                    view.setBackgroundColor(Color.TRANSPARENT) // Restaurar fondo
                }
                is Spinner -> {
                    view.isEnabled = editable
                    view.setBackgroundColor(Color.TRANSPARENT)
                }
                is Switch -> {
                    view.isEnabled = editable
                    view.setBackgroundColor(Color.TRANSPARENT)
                }
                is CheckBox -> {
                    view.isEnabled = editable
                    view.setBackgroundColor(Color.TRANSPARENT)
                }
                is ImageView -> {
                    view.isEnabled = editable
                    // No suele tener fondo de error, no hace falta cambiar color
                }
                is ViewGroup -> setInputsEditable(view, editable)
            }
        }
    }


    private fun actualizarModo() {
        val editable = accion == "editar"
        setInputsEditable(binding.serieDetailLayout, editable)
        binding.sdfbCancelarEdicion.visibility = if (accion == "ver") View.GONE else View.VISIBLE
        binding.sdfbEditar.visibility = if (accion == "ver") View.VISIBLE else View.GONE
        binding.sdfbActualizar.visibility = if (accion == "ver") View.GONE else View.VISIBLE
        binding.sdfbCambiarImagen.visibility = if (accion == "ver") View.GONE else View.VISIBLE
    }

    private fun formatoFecha(fecha: Date): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(fecha)
    }

    private fun uriToByteArray(uri: Uri): ByteArray? {
        return requireContext().contentResolver.openInputStream(uri)?.use {
            it.readBytes()
        }
    }

    private fun inicializarDatos(usandoImagenSeleccionada: Boolean = true) {
        val spinner = binding.sdfspinnerEstadoVisualizacion

        serieFiltrada?.let { serie ->
            binding.sdfinputTitulo.setText(serie.titulo)
            binding.sdfetnPuntuacion.setText(serie.puntuacion?.toString() ?: "")
            binding.sdftilGenero.setText(serie.genero)
            binding.inputTemporadaActual.setText( if (serie.temporadaActual != null) serie.temporadaActual.toString() else (""))
            binding.inputCapituloActual.setText(if(serie.captituloActual != null) serie.captituloActual.toString() else(""))


            if (serie.temporadaActual != null){
                binding.sdfcbProgresoSerie.isChecked = true
            }
            // Spinner
            val adapter = spinner.adapter
            val estadoActual = serie.estadoVisualizacion
            var posicion = 0
            for (i in 0 until adapter.count) {
                if (adapter.getItem(i) == estadoActual) {
                    posicion = i
                    break
                }
            }
            spinner.setSelection(posicion)

            // Switch
            binding.sdfswitchFinalizada.isChecked = serie.serieEnEmision ?: false

            // Fecha
            if (serie.fechaProximoEstreno == null) {
                binding.etFechaEmision.hint = "Sin próxima fecha de emisión"
            } else {
                binding.etFechaEmision.setText(formatoFecha(serie.fechaProximoEstreno!!))
            }



            // Notas
            binding.sdfetNotas.setText(serie.notas ?: "")
            binding.sdfetNotas.hint = if (serie.notas.isNullOrBlank()) "No hay notas para esta serie" else ""

            // Imagen
            // Mostrar imagen
            if (usandoImagenSeleccionada && imagenSeleccionadaBytes != null) {
                val bitmap = BitmapFactory.decodeByteArray(imagenSeleccionadaBytes, 0, imagenSeleccionadaBytes!!.size)
                binding.sdfivImagenSerie.setImageBitmap(bitmap)
            } else if (serie.imagenUrl != null) {
                imagenOriginalBytes = serie.imagenUrl
                val bitmap = BitmapFactory.decodeByteArray(serie.imagenUrl, 0, serie.imagenUrl!!.size)
                binding.sdfivImagenSerie.setImageBitmap(bitmap)
            } else {
                binding.sdfivImagenSerie.setImageResource(R.drawable.serie_default_image)
            }


            // Botón cambiar imagen
            binding.sdfbCambiarImagen.setOnClickListener {
                imagePickerLauncher.launch("image/*")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cbProgresoSerie = binding.sdfcbProgresoSerie

        binding.sdfcbProgresoSerie.visibility = if (estadoVisualizacion == "Viendo") View.VISIBLE else View.GONE

        binding.etFechaEmision.visibility = if (serieEnEmision) View.VISIBLE else View.GONE



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

        binding.etFechaEmision.setOnClickListener {
            mostrarCalendarioConFecha(requireContext(), binding.etFechaEmision) { fecha ->
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
                (activity as MainActivity).serieViewModel.actualizarSerie(serie)
                accion = "ver"
                actualizarModo()
                mostrarMensajePersonalizado(
                    requireContext(),
                    "Serie actualizada", R.layout.custom_toast_info
                )
            }
        }


        binding.sdfbBorrar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmación")
                .setMessage("¿Estás seguro de que quieres borrar la serie?")
                .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss()
                    serieFiltrada?.let { it1 -> viewModel.borrarSerie(it1)
                        mostrarMensajePersonalizado(
                            requireContext(),
                            "Serie borrada", R.layout.custom_toast_info
                        )
                        findNavController().navigate(R.id.action_serieDetailFragment_to_seriesListFragment)
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        binding.sdfswitchFinalizada.setOnCheckedChangeListener { _, isChecked ->
            serieEnEmision = isChecked
            binding.etFechaEmision.visibility = if (serieEnEmision) View.VISIBLE else View.GONE

        }

        cbProgresoSerie.setOnCheckedChangeListener { _, isChecked ->
            binding.sdfllprogresoTemporadaCapitulo.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding.sdfspinnerEstadoVisualizacion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                estadoVisualizacion = parent.getItemAtPosition(position).toString()
                val esViendo = estadoVisualizacion == "Viendo"

                binding.sdfcbProgresoSerie.visibility = if (esViendo) View.VISIBLE else View.GONE

                // Oculta el layout si ya no está en "Viendo"
                if (!esViendo) {
                    binding.sdfcbProgresoSerie.isChecked = false
                    binding.inputTemporadaActual.setText("")
                    binding.inputCapituloActual.setText("")
                    binding.sdfllprogresoTemporadaCapitulo.visibility = View.GONE
                    binding.sdfcbProgresoSerie.isChecked = false // opcional, para evitar incoherencias
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

        val formatoFecha = android.icu.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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
        return serieFiltrada?.let { it1 ->
            Serie(
                id = it1.id,  // Asegúrate de incluir esto si tu modelo Serie lo requiere
                titulo = binding.sdfinputTitulo.text.toString(),
                genero = binding.sdftilGenero.text.toString(),
                temporadaActual = binding.inputTemporadaActual.text.toString().toIntOrNull(),
                captituloActual = binding.inputCapituloActual.text.toString().toIntOrNull(),
                puntuacion = binding.sdfetnPuntuacion.text.toString().toDoubleOrNull(),
                fechaProximoEstreno = fechaProximoEstreno,
                estadoVisualizacion = binding.sdfspinnerEstadoVisualizacion.selectedItem.toString(),
                serieEnEmision = binding.sdfswitchFinalizada.isChecked,
                notas = binding.sdfetNotas.text.toString(),
                imagenUrl = imagenSeleccionadaBytes ?: it1.imagenUrl,  // ← mantiene imagen original si no se cambia
                fechaCreacion = it1.fechaCreacion
            )
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
