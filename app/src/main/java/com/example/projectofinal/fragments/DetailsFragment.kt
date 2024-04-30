package com.example.projectofinal.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.projectofinal.API.InterfaceAPI
import com.example.projectofinal.Adapters.MovieAdapter
import com.example.projectofinal.R
import com.example.projectofinal.Responses.GenresResponse
import com.example.projectofinal.Responses.MoviesResponse
import com.example.projectofinal.databinding.FragmentDetailsBinding
import com.example.projectofinal.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class DetailsFragment : Fragment() {
    private lateinit var movie: MoviesResponse.Movie
    private lateinit var binding: FragmentDetailsBinding

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)

        Glide.with(requireContext())
            .load("https://image.tmdb.org/t/p/w500/${movie.backdrop_path}")
            .into(binding.imageView2)

        binding.textView4.text = movie.title

        binding.textView5.text = movie.overview

        binding.imageButton.setOnClickListener {
            binding.imageButton.visibility = View.INVISIBLE
            binding.imageButton2.visibility = View.VISIBLE
        }

        binding.imageButton2.setOnClickListener {
            binding.imageButton2.visibility = View.INVISIBLE
            binding.imageButton.visibility = View.VISIBLE
        }



        return binding.root
    }


}