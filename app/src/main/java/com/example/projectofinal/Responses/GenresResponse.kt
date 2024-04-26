package com.example.projectofinal.Responses

data class GenresResponse(
    val genres: List<MovieGenre>
){
    data class MovieGenre(
        val id: Int,
        val name: String
    )
}
