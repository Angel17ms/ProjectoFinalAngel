package com.example.projectofinal.fragments

import com.example.projectofinal.Responses.GenresResponse

interface GenreListener {

    fun onGeneroSeleccionado(genero: GenresResponse.MovieGenre)

}