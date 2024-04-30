package com.example.projectofinal.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectofinal.R
import com.example.projectofinal.Responses.GenresResponse
import com.example.projectofinal.databinding.ItemGendersBinding

class GenderAdapter( private val listener: OnClickListener) : RecyclerView.Adapter<GenderAdapter.ViewHolder>() {

    private var genders: MutableList<GenresResponse.MovieGenre> = mutableListOf()
    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemGendersBinding.bind(view)

        fun setListener(gender: GenresResponse.MovieGenre){
            binding.root.setOnClickListener() {
                listener.onClick(gender)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_genders, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genre = genders[position] // Usar la lista filtrada para obtener los elementos
        with(holder) {
            setListener(genre)
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
