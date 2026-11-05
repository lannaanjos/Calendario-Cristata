package com.example.cristata

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BancoHelper(context: Context) :
    SQLiteOpenHelper(context, "campus.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE eventos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                data TEXT,
                local TEXT,
                descricao TEXT,
                favorito INTEGER DEFAULT 0
            )
        """.trimIndent())
        criarTabelaPassaros(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) criarTabelaPassaros(db)
    }

    private fun criarTabelaPassaros(db: SQLiteDatabase) {
        db.execSQL("""
        CREATE TABLE IF NOT EXISTS passaros (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nome TEXT NOT NULL,
            nome_cientifico TEXT NOT NULL,
            url_imagem TEXT NOT NULL,
            data_coleta TEXT,
            UNIQUE (nome, nome_cientifico)
        )
    """.trimIndent())
    }

    fun inserir(evento: Evento): Long {
        val valores = ContentValues().apply {
            put("nome", evento.nome)
            put("data", evento.data)
            put("local", evento.local)
            put("descricao", evento.descricao)
            put("favorito", if (evento.favorito) 1 else 0)
        }
        return writableDatabase.insert("eventos", null, valores)
    }

    fun listar(): MutableList<Evento> {
        val lista = mutableListOf<Evento>()
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM eventos ORDER BY id DESC",
            null
        )
        while (cursor.moveToNext()) {
            lista.add(
                Evento(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nome = cursor.getString(cursor.getColumnIndexOrThrow("nome")),
                    data = cursor.getString(cursor.getColumnIndexOrThrow("data")),
                    local = cursor.getString(cursor.getColumnIndexOrThrow("local")),
                    descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao")),
                    favorito = cursor.getInt(cursor.getColumnIndexOrThrow("favorito")) == 1
                )
            )
        }
        cursor.close()
        return lista
    }

    fun salvarNaColecao(passaro: PassaroDoDia, dataColeta: String): Boolean {
        val valores = ContentValues().apply {
            put("nome", passaro.nome)
            put("nome_cientifico", passaro.nomeCientifico)
            put("url_imagem", passaro.urlImagem)
            put("data_coleta", dataColeta)
        }
        val id = writableDatabase.insertWithOnConflict(
            "passaros", null, valores, SQLiteDatabase.CONFLICT_IGNORE
        )
        return id != -1L
    }

    fun listarColecao(): List<PassaroColecao> {
        val lista = mutableListOf<PassaroColecao>()
        readableDatabase.rawQuery("SELECT * FROM passaros ORDER BY id DESC", null).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    PassaroColecao(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        nome = cursor.getString(cursor.getColumnIndexOrThrow("nome")),
                        nomeCientifico = cursor.getString(cursor.getColumnIndexOrThrow("nome_cientifico")),
                        urlImagem = cursor.getString(cursor.getColumnIndexOrThrow("url_imagem"))
                    )
                )
            }
        }
        return lista
    }

}