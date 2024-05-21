package com.example.projectofinal.Responses

import java.io.Serializable

data class CastResponse(
    val cast: List<ActorResponse>
){
    data class ActorResponse(
        val id: Int,
        val name: String,
        val known_for_department:String
    ) : Serializable
}
