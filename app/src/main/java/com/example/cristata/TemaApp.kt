package com.example.cristata

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

// guarda e aplica a escolha de modo claro/escuro do usuário
object TemaApp {
    private const val ARQUIVO = "configuracoes"
    private const val CHAVE_MODO_ESCURO = "modo_escuro"

    // claro por padrão
    fun estaEscuro(contexto: Context): Boolean =
        contexto.getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE)
            .getBoolean(CHAVE_MODO_ESCURO, false)

    // inverte a escolha, salva e aplica na hora
    fun alternar(contexto: Context) {
        val escuro = !estaEscuro(contexto)
        contexto.getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE).edit {
            putBoolean(CHAVE_MODO_ESCURO, escuro)
        }
        aplicar(escuro)
    }

    // abre o app no modo salvo
    fun aplicarSalvo(contexto: Context) = aplicar(estaEscuro(contexto))

    private fun aplicar(escuro: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (escuro) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}