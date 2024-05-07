package com.example.projectofinal.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.projectofinal.API.InterfaceAPI
import com.example.projectofinal.Adapters.MovieAdapter
import com.example.projectofinal.R
import com.example.projectofinal.Responses.GenresResponse
import com.example.projectofinal.Responses.MoviesResponse
import com.example.projectofinal.databinding.FragmentDetailsBinding
import com.example.projectofinal.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import kotlin.math.roundToInt

class DetailsFragment : Fragment() {
    private lateinit var movie: MoviesResponse.Movie
    private lateinit var binding: FragmentDetailsBinding
    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    companion object {

        private const val API_KEY = "51ef9ea30d062ebf77af05ce4a8eebed"

        @JvmStatic
        fun newInstance(movie: MoviesResponse.Movie) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("movie", movie)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movie = it.getSerializable("movie") as MoviesResponse.Movie
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)

        Glide.with(requireContext())
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
            Glide.with(requireContext())
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



        return binding.root
    }

    fun agregarPeliculaFavorita(peliculaId: Int) {
        userId?.let { userId ->
            val usuarioRef = firestore.collection("users").document(userId)

            usuarioRef.update("peliculasFavoritas", FieldValue.arrayUnion(peliculaId))
                .addOnSuccessListener {
                    Toast.makeText(requireContext(),"Pelicula añadida a favoritos", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(),"Pelicula eliminada de favoritos", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    println("Error al quitar la pelicula")
                }
        }
    }


}