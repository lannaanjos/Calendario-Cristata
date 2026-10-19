package com.example.cristata

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnComecar = findViewById<Button>(R.id.btnComecar)
        val btnSobre = findViewById<Button>(R.id.btnSobre)

        btnComecar.setOnClickListener {
            Toast.makeText(this, "Rodando calendario...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AgendaActivity::class.java)
            startActivity(intent)
        }

        btnSobre?.setOnClickListener {
            Toast.makeText(this, "Desenvolvimento em progresso - equipe Marcos Lanna e Bruna", Toast.LENGTH_SHORT).show()
        }
    }
}