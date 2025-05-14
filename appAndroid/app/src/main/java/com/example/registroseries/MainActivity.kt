package com.example.registroseries

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Insets.add
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.registroseries.databinding.ActivityMainBinding
import com.example.registroseries.modelo.LoginVM
import com.example.registroseries.modelo.Serie
import com.example.registroseries.modelo.SignUpVM
import java.util.Calendar
import kotlin.random.Random
import androidx.navigation.findNavController
import androidx.navigation.NavController

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    var series: MutableList<Serie> = mutableListOf()

    val loginVM:LoginVM by viewModels()
    val signUpVM: SignUpVM by viewModels()


    //usuario con sharedPreferences
    lateinit var usuario:SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //usuario = this.getSharedPreferences("usuario",Context.MODE_PRIVATE)

        generarSeries()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        /*binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }*/
    }

    fun generarSeries(): List<Serie> {

        val titulos = listOf("Breaking Bad", "Stranger Things", "Game of Thrones", "The Witcher", "The Office")
        val generos = listOf("Drama", "Comedia", "Ciencia Ficción", "Acción", "Terror")
        val estadosUsuario = listOf("Viendo", "Completada", "Pendiente", "Abandonada")
        val imagenesUrl = listOf(
            "https://link_imagen_1.jpg",
            "https://link_imagen_2.jpg",
            "https://link_imagen_3.jpg",
            "https://link_imagen_4.jpg",
            "https://link_imagen_5.jpg"
        )

        // Generar 5 series como ejemplo
        for (i in 1..5) {
            val titulo = titulos.random()
            val genero = generos.random()
            val temporadaActual = Random.nextInt(1, 10)  // Entre 1 y 9 temporadas
            val captituloActual = Random.nextInt(1, 12) // Entre 1 y 11 capítulos
            val puntuacion = Random.nextDouble(1.0, 10.0) // Entre 1.0 y 10.0
            val estadoUsuario = estadosUsuario.random()
            val serieFinalizada = estadoUsuario == "Completada"
            val notas = "Notas sobre la serie $titulo"
            val imagenUrl = imagenesUrl.random()

            val fechaProximoEstreno = Calendar.getInstance().apply {
                add(Calendar.MONTH, Random.nextInt(1, 6)) // Proximo estreno entre 1 y 6 meses
            }.time

            val serie = Serie(
                titulo = titulo,
                genero = genero,
                temporadaActual = temporadaActual,
                captituloActual = captituloActual,
                puntuacion = puntuacion,
                fecha_proximo_estreno = fechaProximoEstreno,
                estado_usuario = estadoUsuario,
                serie_finalizada = serieFinalizada,
                notas = notas,
                imagen_url = imagenUrl
            )

            series.add(serie)
        }

        return series
    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        return true
    }

   /* override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
            R.id.
        }

    }*/


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                Toast.makeText(this, "Configuraciones", Toast.LENGTH_LONG).show()
                true
            }
            /*R.id.action_crear_serie -> {
                NavController.navigate(R.id.action_global_crearSerieFragment)

            }*/
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}