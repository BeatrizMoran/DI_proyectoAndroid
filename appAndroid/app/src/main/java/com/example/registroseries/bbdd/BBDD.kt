package com.example.registroseries.bbdd

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.registroseries.modelo.Serie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

@Database(entities = [Serie::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BBDD : RoomDatabase() {
    abstract fun miDAO(): SerieDAO

    companion object {
        @Volatile
        private var INSTANCE: BBDD? = null

        fun getDatabase(context: Context): BBDD {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BBDD::class.java,
                    "serie_dataBase"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d("RoomCallback", "¡Base de datos creada por primera vez!")
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = INSTANCE?.miDAO() ?: return@launch
                                val series = generarSeriesDeEjemplo()
                                series.forEach { dao.insertarSerie(it) }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Función para generar series de ejemplo
        private fun generarSeriesDeEjemplo(): List<Serie> {
            val titulos = listOf(
                "Breaking Bad",
                "Stranger Things",
                "Game of Thrones",
                "The Witcher",
                "The Office"
            )
            val generos = listOf("Drama", "Comedia", "Ciencia Ficción", "Acción", "Terror")
            val estadosUsuario = listOf("Viendo", "Completada", "Pendiente", "Abandonada", "Pendiente")

            return titulos.indices.map { i ->
                Serie(
                    titulo = titulos[i],
                    genero = generos.getOrElse(i) { "Desconocido" },
                    temporadaActual = 1,
                    captituloActual = 1,
                    puntuacion = null,
                    fechaProximoEstreno = null,
                    estadoVisualizacion = estadosUsuario.getOrElse(i) { "Pendiente" },
                    emisionFinalizada = false,
                    notas = null,
                    imagenUrl = null,
                    fechaCreacion = Date()
                )
            }
        }
    }
}
