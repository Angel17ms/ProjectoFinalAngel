package com.example.projectofinal.Responses

import java.io.Serializable

data class GenresResponse(
    val genres: List<MovieGenre>
){
    data class MovieGenre(
        val id: Int,
        val name: String
    ) : Serializable
}
