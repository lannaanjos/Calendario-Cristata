package com.example.cristata

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.ComposeView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    // única janela
    // cada andoid:id="@+id/nome" num XML de layout faz o android gerar uma const R.id>nome q o Kotlin pd usar
    // ao abrir, a MainActivity desenha a moldura, pega a barra, pega a área do pager e coloca as páginas nela
    // essa atv espera q seu XML tenha a moldura, q é o ComposeView com id composePaginas e a barra nav.

    private lateinit var passaroViewModel: PassaroDoDiaViewModel

    private val paginas = listOf(
        Pagina(R.id.nav_home, R.layout.activity_main),
        // Pagina(R.id.nav_eventos, R.layout.pagina_eventos),
        Pagina(R.id.nav_calendario, R.layout.activity_calendario),
        Pagina(R.id.nav_colecao, R.layout.pagina_colecao),
        Pagina(R.id.nav_config, R.layout.activity_configs)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        passaroViewModel = ViewModelProvider(this)[PassaroDoDiaViewModel::class.java]
        val barra = findViewById<BottomNavigationView>(R.id.barraNavegacao)
        findViewById<ComposeView>(R.id.composePaginas).setContent {
            PaginasDeslizaveis(paginas, barra, passaroViewModel)
        }
    }

    override fun onStart() {
        super.onStart()
        passaroViewModel.carregarPassaroDoDia()
    }
}

/* Um activity é uma tela gerenciada pelo próprio Android.
* ela deve estar registrada no manifest, aberta com um intent, ter ciclo de vida próprio e entrar na fila de navegação.
*
* O layout XML é um desenho. A Activity é a classe Kotlin correspondente.
* */