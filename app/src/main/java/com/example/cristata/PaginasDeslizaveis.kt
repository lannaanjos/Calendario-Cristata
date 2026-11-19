package com.example.cristata

import android.view.LayoutInflater
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import coil.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun PaginasDeslizaveis(
    paginas: List<Pagina>,
    barra: BottomNavigationView,
    passaroViewModel: PassaroDoDiaViewModel,
    eventosViewModel: EventosViewModel
) {
    val estado = rememberPagerState { paginas.size }
    val escopo = rememberCoroutineScope()
    val estadoPassaro by passaroViewModel.estado.collectAsState()
    val colecao by passaroViewModel.colecao.collectAsState()
    val eventos by eventosViewModel.eventos.collectAsState()

    // se deslizou marca o ícone da página atual
    LaunchedEffect(estado.currentPage) {
        barra.menu.findItem(paginas[estado.currentPage].idMenu).isChecked = true
    }

    // se tocou no ícone desliza até a página
    DisposableEffect(barra) {
        barra.setOnItemSelectedListener { item ->
            val indice = paginas.indexOfFirst { it.idMenu == item.itemId }
            escopo.launch { estado.scrollToPage(indice) }
            true
        }
        onDispose { barra.setOnItemSelectedListener(null) }
    }

    // beyondViewportPageCount mantém todas as páginas instanciadas
    HorizontalPager(state = estado, beyondViewportPageCount = paginas.size - 1) { indice ->
        AndroidView(
            factory = { contexto ->
                LayoutInflater.from(contexto).inflate(paginas[indice].layout, null).also { view ->
                    if (paginas[indice].layout == R.layout.tela_configs) {
                        configurarConfiguracoes(view, passaroViewModel)
                    }
                    if (paginas[indice].layout == R.layout.tela_calendario) {
                        configurarCalendario(view)
                    }
                }
            },
            update = { view ->
                if (paginas[indice].layout == R.layout.tela_colecao) {
                    atualizarColecao(view, colecao)
                }
                if (paginas[indice].layout == R.layout.tela_lista_eventos) {
                    atualizarEventos(view, eventos)
                }
                if (paginas[indice].layout == R.layout.tela_calendario) {
                    atualizarAgenda(view, eventos)
                }
                if (paginas[indice].layout == R.layout.tela_inicial) {
                    val imagem = view.findViewById<com.google.android.material.imageview.ShapeableImageView>(
                        R.id.imgPassaro
                    )
                    val nome = view.findViewById<android.widget.TextView>(R.id.txtNomePassaro)
                    val nomeCientifico =
                        view.findViewById<android.widget.TextView>(R.id.txtNomeCientificoPassaro)
                    val erro = view.findViewById<android.widget.TextView>(R.id.txtErroPassaro)

                    when (val estadoAtual = estadoPassaro) {
                        EstadoPassaroDoDia.Carregando -> {
                            imagem.setImageResource(R.color.cor_painel_secundario)
                            nome.text = ""
                            nomeCientifico.text = ""
                            erro.visibility = android.view.View.GONE
                        }
                        is EstadoPassaroDoDia.Sucesso -> {
                            nome.text = estadoAtual.passaro.nome
                            nomeCientifico.text = estadoAtual.passaro.nomeCientifico
                            erro.visibility = android.view.View.GONE
                            imagem.load(estadoAtual.passaro.urlImagem) {
                                crossfade(true)
                                placeholder(
                                    ColorDrawable(imagem.context.getColor(R.color.cor_painel_secundario))
                                )
                                error(ColorDrawable(imagem.context.getColor(R.color.cor_painel)))
                                listener(onError = { _, resultado ->
                                    android.util.Log.e(
                                        "PassaroDoDia",
                                        "Falha ao carregar a imagem do pássaro.",
                                        resultado.throwable
                                    )
                                })
                            }
                        }
                        is EstadoPassaroDoDia.Erro -> {
                            imagem.setImageResource(R.color.cor_painel)
                            nome.text = ""
                            nomeCientifico.text = ""
                            erro.text = estadoAtual.mensagem
                            erro.visibility = android.view.View.VISIBLE
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
private fun atualizarColecao(view: View, colecao: List<PassaroColecao>) {
    val lista = view.findViewById<RecyclerView>(R.id.listaColecao)
    val vazio = view.findViewById<TextView>(R.id.txtColecaoVazia)

    val adapter = lista.adapter as? PassaroColecaoAdapter
        ?: PassaroColecaoAdapter { passaro ->
            lista.context.startActivity(PassaroActivity.criarIntent(lista.context, passaro))
        }.also { lista.adapter = it }

    adapter.submitList(colecao)
    vazio.visibility = if (colecao.isEmpty()) View.VISIBLE else View.GONE
}

// preenche a lista de eventos; tocar num cartão abre a tela de edição
private fun atualizarEventos(view: View, eventos: List<Evento>) {
    val lista = view.findViewById<RecyclerView>(R.id.listaEventos)
    val vazio = view.findViewById<TextView>(R.id.txtEventosVazio)

    val adapter = lista.adapter as? EventoAdapter
        ?: EventoAdapter { evento ->
            lista.context.startActivity(
                CadastroEventoActivity.criarIntentEdicao(lista.context, evento)
            )
        }.also { lista.adapter = it }

    adapter.submitEventos(eventos)
    vazio.visibility = if (eventos.isEmpty()) View.VISIBLE else View.GONE
}

// guarda o dia selecionado e os eventos; fica na própria view do calendário (view.tag)
private class EstadoCalendario {
    var selecionado: LocalDate = LocalDate.now()
    var diasComEvento: Set<String> = emptySet()
    var eventos: List<Evento> = emptyList()
}

// célula de cada dia
private class DiaContainer(view: View) : ViewContainer(view) {
    val texto: TextView = view.findViewById(R.id.txtDia)
    lateinit var dia: CalendarDay
}

// cabeçalho de cada mês
private class MesContainer(view: View) : ViewContainer(view) {
    val titulo: TextView = view.findViewById(R.id.txtMes)
}

// liga o botão NOVO EVENTO e monta o calendário
private fun configurarCalendario(tela: View) {
    tela.findViewById<View>(R.id.btnNovoEvento).setOnClickListener {
        tela.context.startActivity(Intent(tela.context, CadastroEventoActivity::class.java))
    }

    val estado = EstadoCalendario()
    tela.tag = estado
    val calendario = tela.findViewById<CalendarView>(R.id.calendarView)

    fun selecionar(data: LocalDate) {
        if (data == estado.selecionado) return
        val anterior = estado.selecionado
        estado.selecionado = data
        calendario.notifyDateChanged(anterior)
        calendario.notifyDateChanged(data)
        mostrarAgenda(tela)
    }

    calendario.dayBinder = object : MonthDayBinder<DiaContainer> {
        override fun create(view: View): DiaContainer {
            val container = DiaContainer(view)
            view.setOnClickListener {
                if (container.dia.position == DayPosition.MonthDate) {
                    selecionar(container.dia.date)
                }
            }
            return container
        }

        override fun bind(container: DiaContainer, data: CalendarDay) {
            container.dia = data
            val texto = container.texto
            texto.text = data.date.dayOfMonth.toString()

            // dias de outros meses ficam escondidos
            if (data.position != DayPosition.MonthDate) {
                texto.visibility = View.INVISIBLE
                return
            }
            texto.visibility = View.VISIBLE

            val temEvento = data.date.toString() in estado.diasComEvento
            val selecionado = data.date == estado.selecionado

            texto.setBackgroundResource(
                when {
                    temEvento && selecionado -> R.drawable.bg_dia_evento_selecionado
                    temEvento -> R.drawable.bg_dia_evento
                    selecionado -> R.drawable.bg_dia_selecionado
                    else -> 0
                }
            )
            // número branco sobre o verde, preto nos outros dias
            texto.setTextColor(
                texto.context.getColor(
                    if (temEvento) R.color.cor_texto_sobre_primaria else R.color.cor_texto
                )
            )
        }
    }

    calendario.monthHeaderBinder = object : MonthHeaderFooterBinder<MesContainer> {
        override fun create(view: View) = MesContainer(view)

        override fun bind(container: MesContainer, data: CalendarMonth) {
            val mes = data.yearMonth.month
                .getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-BR"))
            container.titulo.text =
                "${mes.replaceFirstChar { it.uppercase() }} ${data.yearMonth.year}"
        }
    }

    val mesAtual = YearMonth.now()
    calendario.setup(mesAtual.minusMonths(60), mesAtual.plusMonths(60), DayOfWeek.SUNDAY)
    calendario.scrollToMonth(mesAtual)
}

// mostra no card só os eventos do dia selecionado
private fun mostrarAgenda(tela: View) {
    val estado = tela.tag as? EstadoCalendario ?: return
    val lista = tela.findViewById<RecyclerView>(R.id.rvEventos)

    val adapter = lista.adapter as? EventoAdapter
        ?: EventoAdapter { evento ->
            lista.context.startActivity(
                CadastroEventoActivity.criarIntentEdicao(lista.context, evento)
            )
        }.also { lista.adapter = it }

    // LocalDate.toString() dá "aaaa-mm-dd", o mesmo formato salvo no banco
    val dia = estado.selecionado.toString()
    adapter.submitEventos(estado.eventos.filter { it.data == dia })
}

// chamada sempre que a lista de eventos muda
// chamada sempre que a lista de eventos muda
private fun atualizarAgenda(tela: View, eventos: List<Evento>) {
    val estado = tela.tag as? EstadoCalendario ?: return
    estado.eventos = eventos

    // se mudou quais dias têm evento, redesenha o calendário
    val dias = eventos.map { it.data }.toSet()
    if (dias != estado.diasComEvento) {
        estado.diasComEvento = dias
        tela.findViewById<CalendarView>(R.id.calendarView).notifyCalendarChanged()
    }
    mostrarAgenda(tela)
}
// liga as ações das linhas da tela de configurações
private fun configurarConfiguracoes(view: View, passaroViewModel: PassaroDoDiaViewModel) {
    view.findViewById<TextView>(R.id.txtSaibaMais).setOnClickListener {
        abrirLink(view.context, view.context.getString(R.string.url_repositorio))
    }
    view.findViewById<TextView>(R.id.txtSobreApi).setOnClickListener {
        abrirLink(view.context, view.context.getString(R.string.url_api))
    }
    view.findViewById<TextView>(R.id.txtRedefinir).setOnClickListener {
        passaroViewModel.limparColecao()
    }
    val linhaModo = view.findViewById<TextView>(R.id.txtModoEscuro)
    // mostra a opção contrária ao modo atual
    linhaModo.setText(
        if (TemaApp.estaEscuro(view.context)) R.string.config_modo_claro
        else R.string.config_modo_escuro
    )
    linhaModo.setOnClickListener {
        TemaApp.alternar(view.context)
    }
}

// abre a url no navegador do celular
private fun abrirLink(contexto: Context, url: String) {
    contexto.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
}
