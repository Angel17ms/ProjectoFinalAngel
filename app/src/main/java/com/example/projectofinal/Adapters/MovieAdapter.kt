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
import com.example.projectofinal.databinding.ItemMovieBinding
import java.util.Locale

class MovieAdapter(private val listener: OnClickListener) : RecyclerView.Adapter<MovieAdapter.ViewHolder>(), Filterable {

    private var movies: MutableList<MoviesResponse.Movie> = mutableListOf()
    private var filteredMovies: MutableList<MoviesResponse.Movie> = mutableListOf()
    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMovieBinding.bind(view)

        fun setListener(movie: MoviesResponse.Movie){
            binding.root.setOnClickListener() {
                listener.onClick(movie)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = filteredMovies[position] // Usar la lista filtrada para obtener los elementos
        with(holder) {
            setListener(movie)
            binding.textView2.text = movie.title

            Glide.with(itemView)
                .load("https://image.tmdb.org/t/p/w500/${movie.backdrop_path}")
                .override(400, 300)
                .into(binding.imageView)
        }
    }

    override fun getItemCount(): Int {
        return filteredMovies.size // Usar el tamaño de la lista filtrada
    }

    fun addData(newMovies: List<MoviesResponse.Movie>) {
        val uniqueMovies = newMovies.filter { movie ->
            !movies.any { it.title == movie.title }
        }
        movies.addAll(uniqueMovies)
        filteredMovies.addAll(uniqueMovies) // Agregar los nuevos elementos a la lista filtrada también
        notifyDataSetChanged()
    }

    fun addMovie(movie: MoviesResponse.Movie) {
        if (!movies.contains(movie)) {
            movies.add(movie)
            filteredMovies.add(movie)
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<MoviesResponse.Movie>()
                if (constraint.isNullOrBlank()) {
                    filteredList.addAll(movies)
                } else {
                    val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()
                    filteredList.addAll(
                        movies.filter { it.title.toLowerCase(Locale.ROOT).contains(filterPattern) }
                    )
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                val filteredList = results?.values as? MutableList<MoviesResponse.Movie> ?: mutableListOf()
                filteredMovies.clear()
                filteredMovies.addAll(filteredList)
                notifyDataSetChanged()
            }
        }
    }
}
