package com.example.projectofinal.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.projectofinal.API.InterfaceAPI
import com.example.projectofinal.R
import com.example.projectofinal.Responses.CastResponse
import com.example.projectofinal.Responses.GenresResponse
import com.example.projectofinal.Responses.MoviesResponse
import com.example.projectofinal.databinding.ActivityDetailsBinding
import com.example.projectofinal.fragments.FragmentFavorites
import com.example.projectofinal.fragments.FragmentHome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val API_KEY = "51ef9ea30d062ebf77af05ce4a8eebed"
    private lateinit var credits : List<CastResponse.ActorResponse>
    private lateinit var movieDetails : MoviesResponse.Movie


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

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(InterfaceAPI::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            getcreditos(movie.id, service, Locale.getDefault().language)

            getPeliculasPorId(service, Locale.getDefault().language, movie.id)

            var duracion: String = (movieDetails.runtime / 60).toString()
            duracion = duracion + "h y " + movieDetails.runtime % 60 + "min"
            binding.duracion.text = binding.duracion.text.toString() + duracion

            for (cred in credits){
                if (cred.known_for_department == "Acting"){
                    binding.elenco.text = binding.elenco.text.toString() + cred.name + ", "
                }
            }
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

     suspend fun getcreditos(idMovie: Int, service: InterfaceAPI, idioma: String, page: Int = 1) {
        try {
            val response = service.getMovieCredits(idMovie, API_KEY, idioma)
            credits = response.cast

        } catch (e: Exception) {
            // Manejar errores
            println("Error: ${e.message}")
        }
    }

    suspend fun getPeliculasPorId(service: InterfaceAPI, idioma: String, id: Int) {
        try {

            val response = service.getMovieDetails(id, API_KEY, idioma)
            movieDetails = response.body()!!


        } catch (e: Exception) {
            // Manejar errores
            println("Error: ${e.message}")
        }
    }
}