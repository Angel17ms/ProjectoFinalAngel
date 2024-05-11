package com.example.projectofinal.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.projectofinal.R
import com.example.projectofinal.Responses.MoviesResponse
import com.example.projectofinal.databinding.ActivityDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movie = intent.getSerializableExtra("pelicula") as MoviesResponse.Movie

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500/${movie.backdrop_path}")
            .into(binding.imageView2)

        binding.textView4.text = movie.title

        binding.textView5.text = binding.textView5.text.toString() + movie.overview

        binding.textView6.text = binding.textView6.text.toString() + movie.release_date


        val flagMap = mapOf(
            "en" to R.drawable.en,
            "es" to R.drawable.es,
            "fr" to R.drawable.fr,
            "de" to R.drawable.de,
            "hi" to R.drawable.hi,
            "it" to R.drawable.it,
            "ja" to R.drawable.ja,
            "ko" to R.drawable.ko,
            "pt" to R.drawable.pt

        )
        val flagResource = flagMap[movie.original_language]

        if (flagResource != null) {
            Glide.with(this)
                .load(flagResource)
                .into(binding.imageView3)
        } else {
            // Si no hay una bandera disponible para el idioma, puedes ocultar la ImageView o mostrar una imagen predeterminada
            binding.imageView3.visibility = View.GONE
            binding.textView7.text = binding.textView7.text.toString() + movie.original_language
        }

        val valoracion = (movie.vote_average * 5) / 10

        binding.ratingBar.rating = valoracion.toFloat()

        binding.favOff.setOnClickListener {
            binding.favOff.visibility = View.INVISIBLE
            binding.favOn.visibility = View.VISIBLE
            agregarPeliculaFavorita(movie.id)
        }

        binding.favOn.setOnClickListener {
            binding.favOn.visibility = View.INVISIBLE
            binding.favOff.visibility = View.VISIBLE
            quitarPeliculaFavorita(movie.id)
        }

        userId?.let { userId ->
            val usuarioRef = firestore.collection("users").document(userId)
            usuarioRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val peliculasFavoritas = document["peliculasFavoritas"] as? List<Int>
                        if (peliculasFavoritas != null) {
                            for (id in peliculasFavoritas) {
                                if (id == movie.id) {
                                    binding.favOff.visibility = View.INVISIBLE
                                    binding.favOn.visibility = View.VISIBLE
                                    return@addOnSuccessListener
                                } else {
                                    binding.favOff.visibility = View.VISIBLE
                                    binding.favOn.visibility = View.INVISIBLE
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Manejar errores de lectura de datos
                }
        }

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        toolbar.setNavigationOnClickListener {
            onBackPressed()
            finish()
        }
    }

    fun agregarPeliculaFavorita(peliculaId: Int) {
        userId?.let { userId ->
            val usuarioRef = firestore.collection("users").document(userId)

            usuarioRef.update("peliculasFavoritas", FieldValue.arrayUnion(peliculaId))
                .addOnSuccessListener {
                    Toast.makeText(this,"Pelicula añadida a favoritos", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    println("Error al añadir la pelicula")
                }
        }
    }

    // Quitar una película de la lista de favoritos del usuario
    fun quitarPeliculaFavorita(peliculaId: Int) {
        userId?.let { userId ->
            // Obtener la referencia al documento del usuario
            val usuarioRef = firestore.collection("users").document(userId)

            // Quitar el ID de la película de la lista de favoritos
            usuarioRef.update("peliculasFavoritas", FieldValue.arrayRemove(peliculaId))
                .addOnSuccessListener {
                    Toast.makeText(this,"Pelicula eliminada de favoritos", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    println("Error al quitar la pelicula")
                }
        }
    }
}