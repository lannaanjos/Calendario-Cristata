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

@Composable
fun PaginasDeslizaveis(
    paginas: List<Pagina>,
    barra: BottomNavigationView,
    passaroViewModel: PassaroDoDiaViewModel
) {
    val estado = rememberPagerState { paginas.size }
    val escopo = rememberCoroutineScope()
    val estadoPassaro by passaroViewModel.estado.collectAsState()
    val colecao by passaroViewModel.colecao.collectAsState()

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
                LayoutInflater.from(contexto).inflate(paginas[indice].layout, null)
            },
            update = { view ->
                if (paginas[indice].layout == R.layout.pagina_colecao) {
                    atualizarColecao(view, colecao)
                }
                if (paginas[indice].layout == R.layout.pagina_inicial) {
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
