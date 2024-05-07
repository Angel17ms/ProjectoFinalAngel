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
import com.example.projectofinal.Adapters.MovieAdapterMain
import com.example.projectofinal.Adapters.OnClickListener
import com.example.projectofinal.Responses.GenresResponse
import com.example.projectofinal.Responses.MoviesResponse
import com.example.projectofinal.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainFragment : Fragment(), OnClickListener {
    private lateinit var binding: FragmentMainBinding
    private lateinit var adapterSugeridos: MovieAdapterMain
    private lateinit var adapterEstrenos: MovieAdapterMain
    private lateinit var adapterPopulares: MovieAdapterMain
    private lateinit var adapterMiedo: MovieAdapterMain

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
        binding = FragmentMainBinding.inflate(inflater, container, false)
        val idioma = Locale.getDefault().language


        binding.reciclerSugeridos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapterSugeridos = MovieAdapterMain(this)
        binding.reciclerSugeridos.adapter = adapterSugeridos

        binding.reciclerEstrenos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapterEstrenos = MovieAdapterMain(this)
        binding.reciclerEstrenos.adapter = adapterEstrenos

        binding.reciclerPopulares.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapterPopulares = MovieAdapterMain(this)
        binding.reciclerPopulares.adapter = adapterPopulares

        binding.reciclerMiedo.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapterMiedo = MovieAdapterMain(this)
        binding.reciclerMiedo.adapter = adapterMiedo

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(InterfaceAPI::class.java)


        obtenerPeliculasFavoritas { ids ->
            CoroutineScope(Dispatchers.Main).launch {

                getPeliculasSugeridas(service, idioma, ids)

                getPeliculasMiedo(service, idioma)

                getPeliculasEstrenos(service, idioma)

                getPeliculasPopulares(service, idioma)

            }
        }




        return binding.root
    }

    private suspend fun getPeliculasSugeridas(service: InterfaceAPI, idioma: String, ids: List<Int>, page: Int = 1) {
        try {
            var response: MoviesResponse? = null

            if (ids.isNotEmpty()) {
                for (id in ids) {
                    response = service.getRecommendations(id, API_KEY, idioma, page)
                    val movies = response.results

                    adapterSugeridos.addData(movies)
                }

            } else {
                response = service.getTopRatedMovies(API_KEY, idioma, page)
            }

            val movies = response?.results

            // Añadir datos al adaptador y notificar cambios
            if (movies != null) {
                adapterSugeridos.addData(movies)
            }

            // Recorrer la siguiente página si hay más resultados
            val nextPage = page + 1
            if (nextPage <= 4) {
                getPeliculasSugeridas(service, idioma, ids, nextPage)
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

    private suspend fun getPeliculasMiedo(service: InterfaceAPI, idioma: String, page: Int = 1) {
        try {
            val response = service.getHorrorMovies(API_KEY, idioma,27, page)
            val movies = response.results

            adapterMiedo.addData(movies)

            // Recorrer la siguiente página si hay más resultados
            val nextPage = page + 1
            if (nextPage <= 4) {
                getPeliculasMiedo(service, idioma, nextPage)
            }
        } catch (e: Exception) {
            // Manejar errores
            println("Error: ${e.message}")
        }
    }

    private suspend fun getPeliculasPopulares(service: InterfaceAPI, idioma: String, page: Int = 1) {
        try {
            val response = service.getPopularMovies(API_KEY, idioma, page)
            val movies = response.results


            adapterPopulares.addData(movies)

            // Recorrer la siguiente página si hay más resultados
            val nextPage = page + 1
            if (nextPage <= 4) {
                getPeliculasPopulares(service, idioma, nextPage)
            }
        } catch (e: Exception) {
            // Manejar errores
            println("Error: ${e.message}")
        }
    }

    private suspend fun getPeliculasEstrenos(service: InterfaceAPI, idioma: String, page: Int = 1) {
        try {
            val response = service.getNowPlayingMovies(API_KEY, idioma, page)
            val movies = response.results


            adapterEstrenos.addData(movies)

            // Recorrer la siguiente página si hay más resultados
            val nextPage = page + 1
            if (nextPage <= 4) {
                getPeliculasEstrenos(service, idioma, nextPage)
            }
        } catch (e: Exception) {
            // Manejar errores
            println("Error: ${e.message}")
        }
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



