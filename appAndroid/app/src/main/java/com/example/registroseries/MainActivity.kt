package com.example.registroseries

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import com.example.registroseries.bbdd.BBDD
import com.example.registroseries.bbdd.Repositorio
import com.example.registroseries.databinding.ActivityMainBinding
import com.example.registroseries.modelo.LoginVM
import com.example.registroseries.modelo.SerieVM
import com.example.registroseries.modelo.SerieViewModelFactory
import com.example.registroseries.modelo.SignUpVM
import com.example.registroseries.modelo.SignUpViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment
    private val miDataBase by lazy { BBDD.getDatabase(this) }
    private val miRepositorio by lazy { Repositorio(miDataBase.miDAO()) }
    val serieViewModel: SerieVM by viewModels { SerieViewModelFactory(miRepositorio) }

    val loginVM: LoginVM by viewModels()
    val signUpVM: SignUpVM by viewModels { SignUpViewModelFactory(application, miRepositorio) }
    var isKeyboardVisible = false
    var currentDestinationId = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //forzar modo claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)




        // Listener para saber qué pantalla estamos
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestinationId = destination.id
            updateBottomNavVisibility()
        }

// Listener para detectar teclado
        val decorView = window.decorView
        ViewCompat.setOnApplyWindowInsetsListener(decorView) { _, insetsCompat ->
            isKeyboardVisible = insetsCompat.isVisible(WindowInsetsCompat.Type.ime())
            updateBottomNavVisibility()
            insetsCompat
        }
        ViewCompat.requestApplyInsets(decorView)

        //Si esta logueado redirigir al dashboard, si no al login
        if (loginVM.isUserLoggedIn()){
            findNavController(R.id.nav_host_fragment_content_main)
                .navigate(R.id.dashboardFragment)
            binding.bottomNavigation.selectedItemId = R.id.action_dashboard
        }else{
            findNavController(R.id.nav_host_fragment_content_main)
                .navigate(R.id.loginFragment)
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.action_dashboard -> {
                    navController.navigate(R.id.dashboardFragment)
                    true
                }
                R.id.action_crear_serie -> {
                    navController.navigate(R.id.serieCreateFragment)
                    true
                }
                R.id.action_lista_series -> {
                    navController.navigate(R.id.seriesListFragment)
                    true
                }
                else -> false
            }
        }
    }
    // Función que decide visibilidad combinada
    fun updateBottomNavVisibility() {
        binding.bottomNavigation.visibility =
            if (isKeyboardVisible || currentDestinationId == R.id.loginFragment || currentDestinationId == R.id.signUpFragment) {
                View.GONE
            } else {
                View.VISIBLE
            }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                loginVM.logout()
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.loginFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }




    override fun onSupportNavigateUp(): Boolean {
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
