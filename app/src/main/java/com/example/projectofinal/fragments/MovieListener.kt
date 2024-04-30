package com.example.projectofinal.fragments

import com.example.projectofinal.Responses.GenresResponse
import com.example.projectofinal.Responses.MoviesResponse

interface MovieListener {

    fun onPeliculaSeleccionado(pelicula: MoviesResponse.Movie)

}