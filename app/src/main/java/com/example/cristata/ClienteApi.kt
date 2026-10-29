package com.example.cristata

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ClienteApi {
    // url da api
    private val URL_BASE = "https://nuthatch.lastelm.software/v2/"

    private val clienteHttp = OkHttpClient.Builder()
        .addInterceptor { cadeia ->
            val pedidoComChave = cadeia.request().newBuilder()
                .addHeader("api-key", BuildConfig.NUTHATCH_API_KEY)
                .build()
            cadeia.proceed(pedidoComChave) // segue com a requisição já com o cabeçalho
        }
        .build()

    val servico: NuthatchApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)                                   // endereço base
            .client(clienteHttp)                                 // cliente com a chave da API
            .addConverterFactory(GsonConverterFactory.create())  // JSON -> data classes (Passaro)
            .build()
            .create(NuthatchApi::class.java)                     // gera a implementação da interface
    }
}