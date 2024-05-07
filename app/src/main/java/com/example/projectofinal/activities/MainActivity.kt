package com.example.projectofinal.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.projectofinal.Adapters.OnClickListener
import com.example.projectofinal.R
import com.example.projectofinal.Responses.GenresResponse
import com.example.projectofinal.Responses.MoviesResponse
import com.example.projectofinal.databinding.ActivityMainBinding
import com.example.projectofinal.fragments.DetailsFragment
import com.example.projectofinal.fragments.FragmentFavorites
import com.example.projectofinal.fragments.FragmentGenders
import com.example.projectofinal.fragments.FragmentHome
import com.example.projectofinal.fragments.FragmentProfile
import com.example.projectofinal.fragments.GenreListener
import com.example.projectofinal.fragments.MainFragment
import com.example.projectofinal.fragments.MovieListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(),
    GenreListener, MovieListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var bottomNavigation: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowTitleEnabled(false)

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

        drawerLayout = binding.drawerLayout
        bottomNavigation = binding.bottomNavigation


        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


        val frg = MainFragment()
        frg.setMovieListener(this)
        switchFragment(frg)

        binding.btnToggleDrawer.setOnClickListener {
            val frg = FragmentHome()
            frg.setMovieListener(this)
            switchFragment(frg)
        }

    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }



    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bot_home -> {
                    val frg = MainFragment()
                    frg.setMovieListener(this)
                    switchFragment(frg)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.generos2 -> {
                    val frg = FragmentGenders()
                    frg.setGenereListener(this)
                    switchFragment(frg)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.favoritos2 -> {
                    val frg = FragmentFavorites()
                    frg.setMovieListener(this)
                    switchFragment(frg)
                    return@OnNavigationItemSelectedListener true
                }

            }
            false
        }

    override fun onGeneroSeleccionado(genero: GenresResponse.MovieGenre) {
        val frg = FragmentHome.newInstance(genero)
        frg.setMovieListener(this)
        switchFragment(frg)
    }

    override fun onPeliculaSeleccionado(pelicula: MoviesResponse.Movie) {
        switchFragment(DetailsFragment.newInstance(pelicula))
    }


}
