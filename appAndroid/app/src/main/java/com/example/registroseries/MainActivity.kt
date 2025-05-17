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
import com.example.registroseries.bbdd.BBDD
import com.example.registroseries.bbdd.Repositorio
import com.example.registroseries.modelo.SerieVM
import com.example.registroseries.modelo.SerieViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    val miDataBase by lazy {BBDD.getDatabase(this)}
    val miRepositorio by lazy {Repositorio(miDataBase.miDAO())}
    val serieViewModel: SerieVM by viewModels{SerieViewModelFactory(miRepositorio)}

    //var series: MutableList<Serie> = mutableListOf()

    val loginVM:LoginVM by viewModels()
    val signUpVM: SignUpVM by viewModels()


    //usuario con sharedPreferences
    lateinit var usuario:SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //usuario = this.getSharedPreferences("usuario",Context.MODE_PRIVATE)

        //generarSeries()
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

        //menu de abajo
       accionesBottomNavMenu()
    }

    fun accionesBottomNavMenu(){
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.action_settings -> {
                    Toast.makeText(this, "Configuraciones", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_crear_serie -> {
                    findNavController(R.id.nav_host_fragment_content_main)
                        .navigate(R.id.action_global_crearSerieFragment)
                    true
                }
                R.id.action_dashboard -> {
                    findNavController(R.id.nav_host_fragment_content_main)
                        .navigate(R.id.action_global_dashboardFragment)
                    true
                }
                else -> false
            }
        }
    }



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
            R.id.action_crear_serie -> {
                Toast.makeText(this, "crear", Toast.LENGTH_LONG).show()

                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.action_global_crearSerieFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}