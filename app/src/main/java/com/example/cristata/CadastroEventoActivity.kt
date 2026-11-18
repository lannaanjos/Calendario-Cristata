package com.example.cristata

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CadastroEventoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bubble_cadastro_evento)

        val etNome = findViewById<EditText>(R.id.etNome)
        val etData = findViewById<EditText>(R.id.etData)
        val etHora = findViewById<EditText>(R.id.etHora)
        val etLocal = findViewById<EditText>(R.id.etLocal)
        val etDescricao = findViewById<EditText>(R.id.etDescricao)
        val btnSalvar = findViewById<Button>(R.id.btnSalvar)

        // id != 0 significa que veio da lista para editar
        val idEdicao = intent.getIntExtra(EXTRA_ID, 0)
        val editando = idEdicao != 0
        val favorito = intent.getBooleanExtra(EXTRA_FAVORITO, false)
        val feriado = intent.getBooleanExtra(EXTRA_FERIADO, false)

        if (editando) {
            etNome.setText(intent.getStringExtra(EXTRA_NOME))
            etData.setText(intent.getStringExtra(EXTRA_DATA))
            etHora.setText(intent.getStringExtra(EXTRA_HORA))
            etLocal.setText(intent.getStringExtra(EXTRA_LOCAL))
            etDescricao.setText(intent.getStringExtra(EXTRA_DESCRICAO))
            btnSalvar.text = "SALVAR ALTERAÇÕES"
        }

        btnSalvar.setOnClickListener {
            val evento = Evento(
                id = idEdicao,
                nome = etNome.text.toString().trim(),
                data = etData.text.toString().trim(),
                hora = etHora.text.toString().trim(),
                local = etLocal.text.toString().trim(),
                descricao = etDescricao.text.toString().trim(),
                favorito = favorito,
                feriado = feriado
            )

            if (evento.nome.isEmpty()) {
                etNome.error = "Informe o nome"
                return@setOnClickListener
            }
            // a lista agrupa e ordena pela data, então ela precisa estar neste formato
            if (!Regex("""\d{4}-\d{2}-\d{2}""").matches(evento.data)) {
                etData.error = "Use o formato aaaa-mm-dd"
                return@setOnClickListener
            }

            val banco = BancoHelper(this)
            if (editando) banco.atualizar(evento) else banco.inserir(evento)
            banco.close()
            Toast.makeText(
                this,
                if (editando) "Evento atualizado!" else "Evento salvo!",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    companion object {
        private const val EXTRA_ID = "id"
        private const val EXTRA_NOME = "nome"
        private const val EXTRA_DATA = "data"
        private const val EXTRA_HORA = "hora"
        private const val EXTRA_LOCAL = "local"
        private const val EXTRA_DESCRICAO = "descricao"
        private const val EXTRA_FAVORITO = "favorito"
        private const val EXTRA_FERIADO = "feriado"

        fun criarIntentEdicao(contexto: Context, evento: Evento): Intent =
            Intent(contexto, CadastroEventoActivity::class.java)
                .putExtra(EXTRA_ID, evento.id)
                .putExtra(EXTRA_NOME, evento.nome)
                .putExtra(EXTRA_DATA, evento.data)
                .putExtra(EXTRA_HORA, evento.hora)
                .putExtra(EXTRA_LOCAL, evento.local)
                .putExtra(EXTRA_DESCRICAO, evento.descricao)
                .putExtra(EXTRA_FAVORITO, evento.favorito)
                .putExtra(EXTRA_FERIADO, evento.feriado)
    }
}