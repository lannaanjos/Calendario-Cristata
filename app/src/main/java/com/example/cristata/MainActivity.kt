package com.example.cristata

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val paginas = listOf(
        Pagina(R.id.nav_home, R.layout.pagina_inicial),
        Pagina(R.id.nav_eventos, R.layout.pagina_eventos),
        Pagina(R.id.nav_calendario, R.layout.pagina_calendario),
        Pagina(R.id.nav_colecao, R.layout.pagina_colecao),
        Pagina(R.id.nav_config, R.layout.pagina_config)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val barra = findViewById<BottomNavigationView>(R.id.barraNavegacao)
        findViewById<ComposeView>(R.id.composePaginas).setContent {
            PaginasDeslizaveis(paginas, barra)
        }
    }
}