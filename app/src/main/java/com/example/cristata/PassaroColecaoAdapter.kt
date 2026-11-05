package com.example.cristata

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.imageview.ShapeableImageView

class PassaroColecaoAdapter(
    private val aoClicar: (PassaroColecao) -> Unit
) : ListAdapter<PassaroColecao, PassaroColecaoAdapter.PassaroViewHolder>(Comparador) {

    class PassaroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.txtNomeColecao)
        val nomeCientifico: TextView = view.findViewById(R.id.txtNomeCientificoColecao)
        val imagem: ShapeableImageView = view.findViewById(R.id.imgPassaroColecao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassaroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_passaro_colecao, parent, false)
        return PassaroViewHolder(view)
    }

    override fun onBindViewHolder(holder: PassaroViewHolder, position: Int) {
        val passaro = getItem(position)
        val contexto = holder.itemView.context

        holder.nome.text = contexto.getString(R.string.item_colecao_nome, passaro.nome)
        holder.nomeCientifico.text = passaro.nomeCientifico
        holder.imagem.load(passaro.urlImagem) {
            crossfade(true)
            placeholder(ColorDrawable(contexto.getColor(R.color.cor_painel_secundario)))
            error(ColorDrawable(contexto.getColor(R.color.cor_painel_secundario)))
        }
        holder.itemView.setOnClickListener { aoClicar(passaro) }
    }

    private object Comparador : DiffUtil.ItemCallback<PassaroColecao>() {
        override fun areItemsTheSame(a: PassaroColecao, b: PassaroColecao) = a.id == b.id
        override fun areContentsTheSame(a: PassaroColecao, b: PassaroColecao) = a == b
    }
}