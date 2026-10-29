package com.example.cristata

import retrofit2.http.GET
import retrofit2.http.Query
interface NuthatchApi {
    @GET("birds")
    suspend fun getBirds(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 25
    ): BirdsResponse
}