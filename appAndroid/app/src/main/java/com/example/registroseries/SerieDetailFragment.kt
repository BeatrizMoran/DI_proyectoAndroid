package com.example.registroseries

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.registroseries.databinding.FragmentSerieDetailBinding
import com.example.registroseries.modelo.Serie
import com.example.registroseries.modelo.SerieVM
import com.example.registroseries.utils.mostrarCalendarioConFecha
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
                is EditText -> view.isEnabled = editable
                is Spinner -> view.isEnabled = editable
                is Switch -> view.isEnabled = editable
                is CheckBox -> view.isEnabled = editable
                is ImageView -> view.isEnabled = editable
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
            binding.inputTemporadaActual.setText(serie.temporadaActual.toString())
            binding.inputCapituloActual.setText(serie.captituloActual.toString())

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
            binding.etFechaEmision.setText(
                serie.fechaProximoEstreno?.let { formatoFecha(it) } ?: "No próxima fecha de emisión"
            )

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
            val serie = serieFiltrada?.let { it1 ->
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

            if (serie != null) {
                (activity as MainActivity).serieViewModel.actualizarSerie(serie)
                accion = "ver"
                actualizarModo()
                Toast.makeText(requireContext(), "Serie actualizada", Toast.LENGTH_SHORT).show()
            }
        }


        binding.sdfbBorrar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmación")
                .setMessage("¿Estás seguro de que quieres borrar la serie?")
                .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
