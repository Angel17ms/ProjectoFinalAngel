package com.example.projectofinal.Responses

import java.io.Serializable

data class MoviesResponse(val results: List<Movie>, val total_pages: Int){

    data class Movie(val id:Int,
                     val title: String,
                     val overview: String,
                     val backdrop_path: String): Serializable
}


