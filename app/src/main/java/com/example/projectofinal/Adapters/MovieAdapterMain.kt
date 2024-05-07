package com.example.projectofinal.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectofinal.Responses.MoviesResponse
import com.example.projectofinal.R
import com.example.projectofinal.Responses.GenresResponse
import com.example.projectofinal.databinding.ItemMovieMainBinding
import java.util.Locale

class MovieAdapterMain(private val listener: OnClickListener) : RecyclerView.Adapter<MovieAdapterMain.ViewHolder>() {

    private var movies: MutableList<MoviesResponse.Movie> = mutableListOf()
    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMovieMainBinding.bind(view)

        fun setListener(movie: MoviesResponse.Movie){
            binding.root.setOnClickListener() {
                listener.onClick(movie)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_movie_main, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position] // Usar la lista filtrada para obtener los elementos
        with(holder) {
            setListener(movie)

            Glide.with(itemView)
                .load("https://image.tmdb.org/t/p/w500/${movie.poster_path}")
                .override(300, 500)
                .into(binding.imageView)
        }
    }

    override fun getItemCount(): Int {
        return movies.size // Usar el tamaño de la lista filtrada
    }

    fun addData(newMovies: List<MoviesResponse.Movie>) {
        val uniqueMovies = newMovies.filter { movie ->
            !movies.any { it.title == movie.title }
        }
        movies.addAll(uniqueMovies)
        notifyDataSetChanged()
    }

    fun addMovie(movie: MoviesResponse.Movie) {
        if (!movies.contains(movie)) {
            movies.add(movie)
            notifyDataSetChanged()
        }
    }

}
