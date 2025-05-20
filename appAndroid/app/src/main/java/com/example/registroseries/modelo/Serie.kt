package com.example.registroseries.modelo

import java.util.Date
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Series")
data class Serie(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var titulo: String,
    var genero: String,
    var temporadaActual: Int?,
    var captituloActual: Int?,
    var puntuacion: Double?,
    var fechaProximoEstreno: Date?,
    var estadoVisualizacion: String,
    var serieEnEmision: Boolean?,
    var notas: String?,
    var imagenUrl: ByteArray?,
    var fechaCreacion: Date
)
