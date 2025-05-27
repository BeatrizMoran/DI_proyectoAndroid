package com.example.registroseries

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.registroseries.databinding.ActivityMainBinding
import com.example.registroseries.modelo.LoginVM
import com.example.registroseries.modelo.SignUpVM

import com.example.registroseries.bbdd.BBDD
import com.example.registroseries.bbdd.Repositorio
import com.example.registroseries.modelo.SerieVM
import com.example.registroseries.modelo.SerieViewModelFactory
import com.example.registroseries.modelo.SignUpViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    val miDataBase by lazy {BBDD.getDatabase(this)}
    val miRepositorio by lazy {Repositorio(miDataBase.miDAO())}
    val serieViewModel: SerieVM by viewModels{SerieViewModelFactory(miRepositorio)}

    //var series: MutableList<Serie> = mutableListOf()

    val loginVM:LoginVM by viewModels()
    val signUpVM: SignUpVM by viewModels{SignUpViewModelFactory(application, miRepositorio)}


    //usuario con sharedPreferences
    lateinit var usuario:SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Referencia al BottomNavigationView
        val bottomNav = binding.bottomNavigation

        // Oculta el navbar en login y signup
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.signUpFragment -> bottomNav.visibility = View.GONE
                else -> bottomNav.visibility = View.VISIBLE
            }
        }

        //menu de abajo
       accionesBottomNavMenu()

        //Si esta logueado redirigir al dashboard, si no al login
        if (loginVM.isUserLoggedIn()){
            findNavController(R.id.nav_host_fragment_content_main)
                .navigate(R.id.dashboardFragment)
        }else{
            findNavController(R.id.nav_host_fragment_content_main)
                .navigate(R.id.loginFragment)
        }
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
        menuInflater.inflate(R.menu.menu_main, menu)
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
            R.id.action_logout -> {
                loginVM.logout()
                findNavController(R.id.nav_host_fragment_content_main)
                    .navigate(R.id.loginFragment)
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