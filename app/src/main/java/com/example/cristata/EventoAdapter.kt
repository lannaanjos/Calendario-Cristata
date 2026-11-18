package com.example.cristata

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

// a lista mistura dois tipos de linha: o título do dia e o cartão do evento
sealed interface ItemEvento {
    data class Cabecalho(val data: String) : ItemEvento
    data class Cartao(val evento: Evento) : ItemEvento
}

class EventoAdapter(
    private val aoClicar: (Evento) -> Unit
) : ListAdapter<ItemEvento, RecyclerView.ViewHolder>(Comparador) {

    class CabecalhoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.txtCabecalhoData)
    }

    class CartaoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bolinha: View = view.findViewById(R.id.viewCorEvento)
        val texto: TextView = view.findViewById(R.id.txtEvento)
    }

    // recebe os eventos (já ordenados por data) e coloca um cabeçalho antes de cada dia novo
    fun submitEventos(eventos: List<Evento>) {
        val itens = mutableListOf<ItemEvento>()
        var dataAtual: String? = null
        for (evento in eventos) {
            if (evento.data != dataAtual) {
                itens.add(ItemEvento.Cabecalho(evento.data))
                dataAtual = evento.data
            }
            itens.add(ItemEvento.Cartao(evento))
        }
        submitList(itens)
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is ItemEvento.Cabecalho -> TIPO_CABECALHO
        is ItemEvento.Cartao -> TIPO_CARTAO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TIPO_CABECALHO) {
            CabecalhoViewHolder(inflater.inflate(R.layout.item_evento_cabecalho, parent, false))
        } else {
            CartaoViewHolder(inflater.inflate(R.layout.item_evento, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ItemEvento.Cabecalho -> {
                (holder as CabecalhoViewHolder).titulo.text = formatarCabecalho(item.data)
            }
            is ItemEvento.Cartao -> {
                val cartao = holder as CartaoViewHolder
                val evento = item.evento
                val contexto = holder.itemView.context

                val cor = if (evento.feriado) R.color.cor_evento_feriado else R.color.cor_evento_usuario
                cartao.bolinha.backgroundTintList = ColorStateList.valueOf(contexto.getColor(cor))

                val quando = evento.hora.ifBlank { contexto.getString(R.string.evento_dia_todo) }
                cartao.texto.text = if (evento.local.isBlank()) {
                    "${evento.nome} - $quando"
                } else {
                    "${evento.nome} - $quando (${evento.local})"
                }
                holder.itemView.setOnClickListener { aoClicar(evento) }
            }
        }
    }

    // "2026-05-05" -> "Mai 05"
    private fun formatarCabecalho(data: String): String {
        val partes = data.split("-")
        val mes = partes.getOrNull(1)?.toIntOrNull()
        val dia = partes.getOrNull(2)?.toIntOrNull()
        if (partes.size != 3 || mes == null || dia == null || mes !in 1..12) {
            return data.ifBlank { "Sem data" }
        }
        return "${MESES[mes - 1]} ${dia.toString().padStart(2, '0')}"
    }

    private object Comparador : DiffUtil.ItemCallback<ItemEvento>() {
        override fun areItemsTheSame(a: ItemEvento, b: ItemEvento) = when {
            a is ItemEvento.Cabecalho && b is ItemEvento.Cabecalho -> a.data == b.data
            a is ItemEvento.Cartao && b is ItemEvento.Cartao -> a.evento.id == b.evento.id
            else -> false
        }

        override fun areContentsTheSame(a: ItemEvento, b: ItemEvento) = a == b
    }

    private companion object {
        const val TIPO_CABECALHO = 0
        const val TIPO_CARTAO = 1
        val MESES = listOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
            "Jul", "Ago", "Set", "Out", "Nov", "Dez")
    }
}