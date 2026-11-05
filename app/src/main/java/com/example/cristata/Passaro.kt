package com.example.cristata

data class Passaro(
    val name: String? = null,
    val sciName: String? = null,
    val images: List<String>? = null
)

data class RespostaPassaros(
    val page: Int? = null,
    val pageSize: Int? = null,
    val entities: List<Passaro>? = null
)
