package com.example.cristata

import retrofit2.http.GET
import retrofit2.http.Query

interface NuthatchApi {
    @GET("birds")
    suspend fun listarPassaros(
        @Query("page") pagina: Int = 1,
        @Query("pageSize") tamanhoPagina: Int = 100,
        @Query("hasImg") somenteComImagem: Boolean = true
    ): RespostaPassaros
}