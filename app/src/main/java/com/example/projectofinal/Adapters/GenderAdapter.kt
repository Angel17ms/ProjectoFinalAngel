package com.example.projectofinal.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectofinal.Responses.MoviesResponse
import com.example.projectofinal.R
import com.example.projectofinal.Responses.GenresResponse
import com.example.projectofinal.databinding.ItemGendersBinding
import com.example.projectofinal.databinding.ItemMovieBinding
import java.util.Locale

class GenderAdapter : RecyclerView.Adapter<GenderAdapter.ViewHolder>() {

    private var genders: MutableList<GenresResponse.MovieGenre> = mutableListOf()
    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemGendersBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_genders, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genre = genders[position] // Usar la lista filtrada para obtener los elementos
        with(holder) {
            binding.textView2.text = genre.name

        }
    }

    override fun getItemCount(): Int {
        return genders.size // Usar el tamaño de la lista filtrada
    }

    fun addData(newGenders: List<GenresResponse.MovieGenre>) {
        genders.addAll(newGenders)
        notifyDataSetChanged()
    }

}
