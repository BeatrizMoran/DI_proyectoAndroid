package com.example.registroseries.modelo

import java.util.Date

class Serie (
    val titulo: String,
    val genero: String,
    val temporadaActual: Int,
    val captituloActual: Int,
    val puntuacion: Double,
    val fecha_proximo_estreno: Date,
    val estado_usuario: String,
    val serie_finalizada: Boolean,
    val notas: String,
    val imagen_url: String

)