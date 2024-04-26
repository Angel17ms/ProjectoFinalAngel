package com.example.projectofinal.Responses

data class MoviesResponse(val results: List<Movie>, val total_pages: Int){

    data class Movie(val title: String,
                     val overview: String,
                     val backdrop_path: String)
}


