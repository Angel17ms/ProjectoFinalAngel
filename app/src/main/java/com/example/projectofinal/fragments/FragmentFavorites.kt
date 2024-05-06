package com.example.projectofinal.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectofinal.API.InterfaceAPI
import com.example.projectofinal.Adapters.MovieAdapter
import com.example.projectofinal.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import android.widget.SearchView.OnQueryTextListener
import com.example.projectofinal.Adapters.OnClickListener
import com.example.projectofinal.Responses.GenresResponse
import com.example.projectofinal.Responses.MoviesResponse
import com.example.projectofinal.databinding.FragmentFavoritesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FragmentFavorites : Fragment(), OnClickListener {
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var adapter: MovieAdapter
    private lateinit var listener: MovieListener
    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid




    companion object {

        private const val API_KEY = "51ef9ea30d062ebf77af05ce4a8eebed"

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val recicler = binding.recicler
        val idioma = Locale.getDefault().language

        recicler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = MovieAdapter(this)
        recicler.adapter = adapter

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(InterfaceAPI::class.java)



        obtenerPeliculasFavoritas { ids ->
            CoroutineScope(Dispatchers.Main).launch {

                getPeliculasPorId(service, idioma, ids)
            }
        }





        return binding.root
    }

    suspend fun getPeliculasPorId(service: InterfaceAPI, idioma: String, ids: List<Int>) {
        try {
            // Recorrer cada ID y obtener la película correspondiente
            for (id in ids) {
                val response = service.getMovieDetails(id, API_KEY, idioma)
                val movie = response.body()

                // Verificar si se obtuvo la película correctamente
                if (response.isSuccessful && movie != null) {
                    adapter.addMovie(movie)
                } else {
                    println("Error al obtener película con ID $id: ${response.message()}")
                }
            }
        } catch (e: Exception) {
            // Manejar errores
            println("Error: ${e.message}")
        }
    }

    fun obtenerPeliculasFavoritas(callback: (List<Int>) -> Unit) {
        userId?.let { userId ->
            val usuarioRef = firestore.collection("users").document(userId)

            // Obtener la lista de películas favoritas del usuario
            usuarioRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val peliculasFavoritas = document.get("peliculasFavoritas") as? List<Int> ?: emptyList()
                        callback(peliculasFavoritas)
                    } else {
                        callback(emptyList())
                    }
                }
                .addOnFailureListener { e ->
                    // Manejar errores
                    println("Error al obtener películas favoritas: ${e.message}")
                    callback(emptyList())
                }
        } ?: callback(emptyList())
    }

    override fun onClick(movie: MoviesResponse.Movie) {
        if (listener != null)
            listener.onPeliculaSeleccionado(movie)
    }

    override fun onClick(gender: GenresResponse.MovieGenre) {
        TODO("Not yet implemented")
    }

    override fun setGenereListener(listener: GenreListener) {
        TODO("Not yet implemented")
    }

    override fun setMovieListener(listener: MovieListener) {
        this.listener = listener
    }

}



