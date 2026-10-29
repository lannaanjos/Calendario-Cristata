package com.example.cristata

import android.view.LayoutInflater
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

@Composable
fun PaginasDeslizaveis(paginas: List<Pagina>, barra: BottomNavigationView) {
    val estado = rememberPagerState { paginas.size }
    val escopo = rememberCoroutineScope()

    // se deslizou marca o ícone da página atual
    LaunchedEffect(estado.currentPage) {
        barra.menu.findItem(paginas[estado.currentPage].idMenu).isChecked = true
    }

    // se tocou no ícone desliza até a página
    DisposableEffect(barra) {
        barra.setOnItemSelectedListener { item ->
            val indice = paginas.indexOfFirst { it.idMenu == item.itemId }
            escopo.launch { estado.scrollToPage(indice) }
            true
        }
        onDispose { barra.setOnItemSelectedListener(null) }
    }

    // beyondViewportPageCount mantém todas as páginas instanciadas
    HorizontalPager(state = estado, beyondViewportPageCount = paginas.size - 1) { indice ->
        AndroidView(
            factory = { contexto ->
                LayoutInflater.from(contexto).inflate(paginas[indice].layout, null)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}