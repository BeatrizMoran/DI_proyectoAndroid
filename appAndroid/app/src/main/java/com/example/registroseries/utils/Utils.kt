package com.example.registroseries.utils

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.widget.EditText
import androidx.core.content.ContentProviderCompat.requireContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatearFecha(date: Date): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(date)
}

fun mostrarCalendarioConFecha(
    context: Context,
    editText: EditText,
    onFechaSeleccionada: (Date?) -> Unit
) {
    val calendario = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val fechaStr = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
            editText.setText(fechaStr)

            val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaDate = try {
                formatoFecha.parse(fechaStr)
            } catch (e: Exception) {
                null
            }

            onFechaSeleccionada(fechaDate)
        },
        calendario.get(Calendar.YEAR),
        calendario.get(Calendar.MONTH),
        calendario.get(Calendar.DAY_OF_MONTH)
    )
    datePicker.show()
}


