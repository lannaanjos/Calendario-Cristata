package com.example.cristata

data class Passaro(
    val id: Int,
    val name: String?,
    val sciName: String?,
    val images: List<String>?
)

data class BirdsResponse(
    val entities: List<Passaro>?
)