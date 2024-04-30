package com.example.projectofinal.Adapters

import com.example.projectofinal.Responses.GenresResponse
import com.example.projectofinal.Responses.MoviesResponse
import com.example.projectofinal.fragments.GenreListener
import com.example.projectofinal.fragments.MovieListener

interface OnClickListener {

    fun onClick(movie: MoviesResponse.Movie)

    fun onClick(gender: GenresResponse.MovieGenre)

    fun setGenereListener(listener: GenreListener)

    fun setMovieListener(listener: MovieListener)


}