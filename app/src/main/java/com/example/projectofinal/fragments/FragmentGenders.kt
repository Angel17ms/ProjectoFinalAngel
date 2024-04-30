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
import com.example.projectofinal.Adapters.GenderAdapter
import com.example.projectofinal.Adapters.OnClickListener
import com.example.projectofinal.Responses.GenresResponse
import com.example.projectofinal.Responses.MoviesResponse
import com.example.projectofinal.databinding.FragmentGendersBinding


class FragmentGenders : Fragment(), OnClickListener {
    private lateinit var binding: FragmentGendersBinding
    private lateinit var adapter: GenderAdapter
    private lateinit var listener: GenreListener


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGendersBinding.inflate(inflater, container, false)
        val recicler = binding.reciclerGenders
        val idioma = Locale.getDefault().language

        recicler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = GenderAdapter(this)
        recicler.adapter = adapter

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(InterfaceAPI::class.java)

        // Utiliza CoroutineScope y launch para manejar llamadas suspendidas
        CoroutineScope(Dispatchers.Main).launch {
            getGenders(service, idioma)
        }

        return binding.root
    }

    private suspend fun getGenders(service: InterfaceAPI, idioma: String) {
        try {
            val response = service.getMovieGenres(API_KEY, idioma)
            val genres = response.genres

            // Actualizar el adaptador con la lista de géneros
            adapter.addData(genres)
        } catch (e: Exception) {
            // Manejar errores
            println("Error: ${e.message}")
        }
    }

    companion object {
        private const val API_KEY = "51ef9ea30d062ebf77af05ce4a8eebed"
    }

    override fun onClick(movie: MoviesResponse.Movie) {
        TODO("Not yet implemented")
    }

    override fun setGenereListener(listener: GenreListener) {
        this.listener = listener

    }

    override fun onClick(gender: GenresResponse.MovieGenre) {
        if (listener != null)
            listener.onGeneroSeleccionado(gender)
    }



    override fun setMovieListener(listener: MovieListener) {
        TODO("Not yet implemented")
    }
}



