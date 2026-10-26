package com.example.cristata

import android.app.Activity
import android.content.Intent
import androidx.annotation.IdRes
import com.google.android.material.bottomnavigation.BottomNavigationView

// id -> tela.
private val telasDaBarra = mapOf(
    R.id.nav_home to MainActivity::class.java,
    R.id.nav_eventos to EventosActivity::class.java,
    R.id.nav_calendario to AgendaActivity::class.java,
    R.id.nav_colecao to ListaPassarosActivity::class.java,
    R.id.nav_config to ConfigsActivity::class.java
)

fun Activity.configurarBarraNavegacao(@IdRes itemAtual: Int) {
    val barra = findViewById<BottomNavigationView>(R.id.barraNavegacao)

    // deixa o icon da tela selecionada branco
    barra.selectedItemId = itemAtual

    // troca de tela conforme o icone selecionado
    barra.setOnItemSelectedListener { item ->
        val destino = telasDaBarra[item.itemId]
        if (item.itemId != itemAtual && destino != null) {
            val intent = Intent(this, destino)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
        false
    }
}