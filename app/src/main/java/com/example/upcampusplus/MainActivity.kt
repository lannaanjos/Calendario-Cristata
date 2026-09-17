package com.example.upcampusplus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.upcampusplus.ui.theme.UPCampusPlusTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val btnComecar = findViewById<Button>(R.id.btnComecar)
        val btnVoltar = findViewById<Button>(R.id.btnVoltar)
        val btnSobre = findViewById<Button>(R.id.btnSobre)
        val btnSalvar = findViewById<Button>(R.id.btnSalvar)
        btnComecar.setOnClickListener{
            val intent = Intent(
                this, MainActivity::class.java
            )
            startActivity(intent)
        }

        btnVoltar.setOnClickListener{
            finish()
        }



        btnSalvar.setOnClickListener {
            val evento = Evento(
                nome = etNome.text.toString().trim(),
                data = etData.text.toString().trim(),
                local = etLocal.text.toString().trim(),
                descricao = etDescricao.text.toString().trim()
            )
            if (evento.nome.isEmpty()) {
                etNome.error = "Informe o nome"
                return@setOnClickListener
            }
            val banco = BancoHelper(this)
            banco.inserir(evento)
            Toast.makeText(this, "Evento salvo!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

