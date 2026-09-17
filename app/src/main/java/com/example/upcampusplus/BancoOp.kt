package com.example.upcampusplus

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
fun inserir(evento: Evento): Long {
    val valores = ContentValues().apply {
        put("nome", evento.nome)
        put("data", evento.data)
        put("local", evento.local)
        put("descricao", evento.descricao)
        put("favorito", if (evento.favorito) 1 else 0)
    }
    return writableDatabase.insert(
        "eventos",
        null,
        valores
    )
}
class BancoOp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banco_op)


    }
}

