package com.example.cristata

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.google.android.material.imageview.ShapeableImageView

class PassaroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passaro)

        val nome = intent.getStringExtra(EXTRA_NOME).orEmpty()
        val nomeCientifico = intent.getStringExtra(EXTRA_NOME_CIENTIFICO).orEmpty()
        val urlImagem = intent.getStringExtra(EXTRA_URL_IMAGEM).orEmpty()

        findViewById<TextView>(R.id.txtNomeDetalhe).text = nome
        findViewById<TextView>(R.id.txtNomeCientificoDetalhe).text = nomeCientifico
        findViewById<ShapeableImageView>(R.id.imgPassaroDetalhe).load(urlImagem) {
            crossfade(true)
            placeholder(ColorDrawable(getColor(R.color.cor_painel)))
            error(ColorDrawable(getColor(R.color.cor_painel)))
        }
    }

    companion object {
        private const val EXTRA_NOME = "nome"
        private const val EXTRA_NOME_CIENTIFICO = "nome_cientifico"
        private const val EXTRA_URL_IMAGEM = "url_imagem"

        fun criarIntent(contexto: Context, passaro: PassaroColecao): Intent =
            Intent(contexto, PassaroActivity::class.java)
                .putExtra(EXTRA_NOME, passaro.nome)
                .putExtra(EXTRA_NOME_CIENTIFICO, passaro.nomeCientifico)
                .putExtra(EXTRA_URL_IMAGEM, passaro.urlImagem)
    }
}