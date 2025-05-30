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
        setHasOptionsMenu(true)
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
        val cbFechaEmision = binding.scfcbFechaEmision

        serieFiltrada?.let { serie ->
            binding.sdfinputTitulo.setText(serie.titulo)
            binding.sdfetnPuntuacion.setText(serie.puntuacion?.toString() ?: "")
            binding.sdftilGenero.setText(serie.genero)
            binding.inputTemporadaActual.setText(serie.temporadaActual?.toString() ?: "")
            binding.inputCapituloActual.setText(serie.captituloActual?.toString() ?: "")

            binding.sdfcbProgresoSerie.isChecked = serie.temporadaActual != null

            // Spinner
            val adapter = spinner.adapter
            val estadoActual = serie.estadoVisualizacion
            for (i in 0 until adapter.count) {
                if (adapter.getItem(i) == estadoActual) {
                    spinner.setSelection(i)
                    break
                }
            }

            // Switch
            binding.sdfswitchFinalizada.isChecked = serie.serieEnEmision ?: false
            serieEnEmision = serie.serieEnEmision ?: false
            fechaProximoEstreno = serie.fechaProximoEstreno

            // CheckBox de fecha de emisión
            cbFechaEmision.visibility = if (serieEnEmision) View.VISIBLE else View.GONE
            cbFechaEmision.isChecked = serie.fechaProximoEstreno != null

            // Campo de fecha de emisión
            if (serie.fechaProximoEstreno != null) {
                binding.etFechaEmision.setText(formatoFecha(serie.fechaProximoEstreno!!))
            } else {
                binding.etFechaEmision.setText("")
                binding.etFechaEmision.hint = "Sin próxima fecha de emisión"
            }
            binding.etFechaEmision.visibility = if (serieEnEmision && cbFechaEmision.isChecked) View.VISIBLE else View.GONE

            // Notas
            binding.sdfetNotas.setText(serie.notas ?: "")
            binding.sdfetNotas.hint = if (serie.notas.isNullOrBlank()) "No hay notas para esta serie" else ""

            // Imagen
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

        val esViendo = spinner.selectedItem.toString() == "Viendo"
        binding.sdfcbProgresoSerie.visibility = if (esViendo) View.VISIBLE else View.GONE
        binding.sdfllprogresoTemporadaCapitulo.visibility = if (esViendo && binding.sdfcbProgresoSerie.isChecked) View.VISIBLE else View.GONE
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cbProgresoSerie = binding.sdfcbProgresoSerie
        val cbFechaEmision = binding.scfcbFechaEmision // El checkbox que controla si hay una nueva fecha
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

        //observar, para que al actualizar datos se reflejen
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
                (activity as MainActivity).serieViewModel.actualizarSerie(serie){ serieActualizada ->
                    if (serieActualizada){
                        mostrarMensajePersonalizado(
                            requireContext(),
                            "Serie actualizada", R.layout.custom_toast_info
                        )
                        accion = "ver"
                        actualizarModo()

                    }else{
                        mostrarMensajePersonalizado(
                            requireContext(),
                            "Error.- Ya hay una serie con ese titulo", R.layout.custom_toast_layout
                        )

                    }
                }


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
            cbFechaEmision.visibility = if (serieEnEmision) View.VISIBLE else View.GONE

            // Si se apaga el switch de emisión, también ocultamos el campo fecha
            if (!isChecked) {
                etFechaEmision.visibility = View.GONE
                cbFechaEmision.isChecked = false
            }
        }
        // CheckBox: ¿Hay nueva fecha de emisión?
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
                temporadaActual = if (binding.sdfcbProgresoSerie.isChecked) binding.inputTemporadaActual.text.toString().toIntOrNull() else null,
                captituloActual = if(binding.sdfcbProgresoSerie.isChecked) binding.inputCapituloActual.text.toString().toIntOrNull() else null,
                puntuacion = binding.sdfetnPuntuacion.text.toString().toDoubleOrNull(),
                fechaProximoEstreno = if(binding.scfcbFechaEmision.isChecked) fechaProximoEstreno else null,
                estadoVisualizacion = binding.sdfspinnerEstadoVisualizacion.selectedItem.toString(),
                serieEnEmision = if(binding.sdfswitchFinalizada.isChecked) binding.sdfswitchFinalizada.isChecked else false,
                notas = binding.sdfetNotas.text.toString(),
                imagenUrl = imagenSeleccionadaBytes ?: it1.imagenUrl,  // ← mantiene imagen original si no se cambia
                fechaCreacion = it1.fechaCreacion
            )
        }


    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        // ocultar opciones del menu
        menu.findItem(R.id.action_cargar_datos_formulario)?.isVisible = false
        menu.findItem(R.id.action_limpiar_campos_formulario)?.isVisible = false
        menu.findItem(R.id.action_cambiar_vista)?.isVisible = false
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
