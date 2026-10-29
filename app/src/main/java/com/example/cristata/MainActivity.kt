package com.example.cristata

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val paginas = listOf(
        Pagina(R.id.nav_home, R.layout.pagina_inicial),
        Pagina(R.id.nav_eventos, R.layout.pagina_eventos),
        Pagina(R.id.nav_calendario, R.layout.pagina_calendario),
        Pagina(R.id.nav_colecao, R.layout.pagina_colecao),
        Pagina(R.id.nav_config, R.layout.pagina_config)
    )

    // url da foto do pássaro; quando muda, a tela inicial recarrega a imagem
    private var urlPassaro by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val barra = findViewById<BottomNavigationView>(R.id.barraNavegacao)
        findViewById<ComposeView>(R.id.composePaginas).setContent {
            PaginasDeslizaveis(paginas, barra, urlPassaro)
        }

        buscarPassaro()
    }

    private fun buscarPassaro() {
        lifecycleScope.launch {
            try {
                val resp = ClienteApi.servico.getBirds()
                urlPassaro = resp.entities
                    ?.firstOrNull { !it.images.isNullOrEmpty() }
                    ?.images?.first()
            } catch (e: Exception) {
                Log.e("Aves", "Erro na API", e)
            }
        }
    }
}