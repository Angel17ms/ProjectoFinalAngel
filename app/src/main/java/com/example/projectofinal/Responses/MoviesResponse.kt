package com.example.projectofinal.Responses

import java.io.Serializable

data class MoviesResponse(val results: List<Movie>, val total_pages: Int){

    data class Movie(val id:Int,
                     val title: String,
                     val overview: String,
                     val backdrop_path: String,
                     val poster_path: String,
                     val original_language: String,
                     val release_date:String,
                     val vote_average: Double): Serializable
}
