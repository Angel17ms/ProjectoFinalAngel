package com.example.projectofinal.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.projectofinal.R
import com.example.projectofinal.databinding.ActivityMainBinding
import com.example.projectofinal.fragments.FragmentFavorites
import com.example.projectofinal.fragments.FragmentGenders
import com.example.projectofinal.fragments.FragmentHome
import com.example.projectofinal.fragments.FragmentProfile
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigation = findViewById<BottomNavigationView>(
            R.id.bottomNavigation
        )
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)



        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        switchFragment(FragmentHome())

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUser = mAuth.currentUser
        currentUser?.let {
            firestore.collection("users").document(it.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("nombre")
                        binding.bienvenida.text = "Bienvenido $userName"
                    }
                }
                .addOnFailureListener { exception ->
                    // Manejar cualquier error al obtener el nombre del usuario
                }
        }

    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                switchFragment(FragmentHome())
            }


            R.id.generos -> {
                switchFragment(FragmentGenders())

            }

            R.id.favoritos -> {
                switchFragment(FragmentFavorites())

            }


            R.id.perfil -> {
                switchFragment(FragmentProfile())

            }

            R.id.nav_logout ->{

            }

        }
        // Cierra el drawer después de hacer la selección
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bot_home -> {
                    switchFragment(FragmentHome())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.generos -> {
                    switchFragment(FragmentGenders())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.favoritos -> {
                    switchFragment(FragmentFavorites())
                    return@OnNavigationItemSelectedListener true
                }

            }
            false
        }


}
