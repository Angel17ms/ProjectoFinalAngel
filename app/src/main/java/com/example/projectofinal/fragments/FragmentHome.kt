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



class FragmentHome : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val recicler = binding.recicler
        val idioma = Locale.getDefault().language
        val search = binding.searchView

        recicler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = MovieAdapter()
        recicler.adapter = adapter

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(InterfaceAPI::class.java)

        // Utiliza CoroutineScope y launch para manejar llamadas suspendidas
        CoroutineScope(Dispatchers.Main).launch {
            getPeliculas(service, idioma)
        }

        // Agregar un TextWatcher al SearchView
        search.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Filtrar los elementos del RecyclerView al cambiar el texto en el SearchView
                adapter.filter.filter(newText)
                return false
            }
        })





        return binding.root
    }

    private suspend fun getPeliculas(service: InterfaceAPI, idioma: String, page: Int = 1) {
        try {
            val response = service.getPopularMovies(API_KEY, idioma, page)
            val movies = response.results

            // Añadir datos al adaptador y notificar cambios
            adapter.addData(movies)

            // Recorrer la siguiente página si hay más resultados
            val nextPage = page + 1
            if (nextPage <= response.total_pages) {
                getPeliculas(service, idioma, nextPage)
            }
        } catch (e: Exception) {
            // Manejar errores
            println("Error: ${e.message}")
        }
    }

    companion object {
        private const val API_KEY = "51ef9ea30d062ebf77af05ce4a8eebed"
    }
}



