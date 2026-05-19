package com.example.speechkids_ai

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.speechkids_ai.model.UserRole
import com.example.speechkids_ai.databinding.ActivityMainBinding
import com.example.speechkids_ai.utils.RoleManager
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.visibility = View.GONE

        // --- ИСПРАВЛЕНИЕ ЗДЕСЬ ---
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        // -------------------------

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        configureBottomNav(UserRole.PARENT)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideOnAuthScreens = destination.id == R.id.authFragment || destination.id == R.id.roleSelectionFragment
            bottomNav.visibility = if (hideOnAuthScreens) View.GONE else View.VISIBLE

            if (!hideOnAuthScreens) {
                configureBottomNav(RoleManager.getRole())
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun configureBottomNav(role: UserRole) {
        val menuRes = when (role) {
            UserRole.PARENT, UserRole.CHILD -> R.menu.menu_bottom_parent
            UserRole.THERAPIST -> R.menu.menu_bottom_therapist
            UserRole.ADMIN -> R.menu.menu_bottom_admin
        }

        bottomNav.menu.clear()
        bottomNav.inflateMenu(menuRes)
        bottomNav.setOnItemSelectedListener { item ->
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            if (navController.currentDestination?.id != item.itemId) {
                navController.navigate(item.itemId)
            }
            true
        }
    }
}