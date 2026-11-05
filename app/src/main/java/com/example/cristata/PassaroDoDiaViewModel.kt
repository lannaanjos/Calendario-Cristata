package com.example.cristata

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class PassaroDoDia(
    val nome: String,
    val nomeCientifico: String,
    val urlImagem: String
)

sealed interface EstadoPassaroDoDia {
    data object Carregando : EstadoPassaroDoDia
    data class Sucesso(val passaro: PassaroDoDia) : EstadoPassaroDoDia
    data class Erro(val mensagem: String) : EstadoPassaroDoDia
}

class PassaroDoDiaViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencias = application.getSharedPreferences(
        "passaro_do_dia",
        Context.MODE_PRIVATE
    )
    private val _estado = MutableStateFlow<EstadoPassaroDoDia>(EstadoPassaroDoDia.Carregando)
    val estado: StateFlow<EstadoPassaroDoDia> = _estado.asStateFlow()
    private var buscaEmAndamento: Job? = null

    fun carregarPassaroDoDia() {
        val dataHoje = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date())
        lerPassaroSalvo(dataHoje)?.let {
            _estado.value = EstadoPassaroDoDia.Sucesso(it)
            return
        }

        if (buscaEmAndamento?.isActive == true) return

        if (BuildConfig.NUTHATCH_API_KEY.isBlank()) {
            val mensagem = getApplication<Application>().getString(R.string.erro_chave_api)
            Log.e(TAG, "NUTHATCH_API_KEY não está configurada.")
            _estado.value = EstadoPassaroDoDia.Erro(mensagem)
            return
        }

        buscaEmAndamento = viewModelScope.launch {
            _estado.value = EstadoPassaroDoDia.Carregando
            try {
                val resposta = ClienteApi.servico.listarPassaros()
                val passarosComImagem = resposta.entities.orEmpty().filter { passaro ->
                    passaro.images.orEmpty().any(String::isNotBlank)
                }

                if (passarosComImagem.isEmpty()) {
                    val mensagem = getApplication<Application>().getString(R.string.erro_sem_imagens)
                    Log.e(TAG, "A API não retornou aves com imagens.")
                    _estado.value = EstadoPassaroDoDia.Erro(mensagem)
                    return@launch
                }

                val dataDaEscolha = Date()
                val dataEscolhida = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(dataDaEscolha)
                val diaDoAno = Calendar.getInstance().apply { time = dataDaEscolha }
                    .get(Calendar.DAY_OF_YEAR)
                val indiceDoDia = (diaDoAno - 1) % passarosComImagem.size
                val passaro = passarosComImagem[indiceDoDia]
                val dadosDoDia = PassaroDoDia(
                    nome = passaro.name.orEmpty().ifBlank {
                        getApplication<Application>().getString(R.string.nome_desconhecido)
                    },
                    nomeCientifico = passaro.sciName.orEmpty(),
                    urlImagem = passaro.images.orEmpty().first(String::isNotBlank)
                )

                salvarPassaro(dataEscolhida, dadosDoDia)
                _estado.value = EstadoPassaroDoDia.Sucesso(dadosDoDia)
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: HttpException) {
                Log.e(TAG, "Falha HTTP ao buscar o pássaro do dia: ${erro.code()}.", erro)
                val recurso = if (erro.code() == 401) {
                    R.string.erro_chave_api
                } else {
                    R.string.erro_api
                }
                _estado.value = EstadoPassaroDoDia.Erro(
                    getApplication<Application>().getString(recurso)
                )
            } catch (erro: IOException) {
                Log.e(TAG, "Falha de conexão ao buscar o pássaro do dia.", erro)
                _estado.value = EstadoPassaroDoDia.Erro(
                    getApplication<Application>().getString(R.string.erro_sem_internet)
                )
            } catch (erro: Exception) {
                Log.e(TAG, "Falha ao carregar o pássaro do dia.", erro)
                _estado.value = EstadoPassaroDoDia.Erro(
                    getApplication<Application>().getString(R.string.erro_api)
                )
            }
        }
    }

    private fun lerPassaroSalvo(dataHoje: String): PassaroDoDia? {
        if (preferencias.getString(CHAVE_DATA, null) != dataHoje) return null

        val nome = preferencias.getString(CHAVE_NOME, null) ?: return null
        val nomeCientifico = preferencias.getString(CHAVE_NOME_CIENTIFICO, null) ?: return null
        val urlImagem = preferencias.getString(CHAVE_URL_IMAGEM, null) ?: return null
        if (urlImagem.isBlank()) return null

        return PassaroDoDia(nome, nomeCientifico, urlImagem)
    }

    private fun salvarPassaro(data: String, passaro: PassaroDoDia) {
        preferencias.edit()
            .putString(CHAVE_DATA, data)
            .putString(CHAVE_NOME, passaro.nome)
            .putString(CHAVE_NOME_CIENTIFICO, passaro.nomeCientifico)
            .putString(CHAVE_URL_IMAGEM, passaro.urlImagem)
            .apply()
    }

    private companion object {
        const val TAG = "PassaroDoDia"
        const val CHAVE_DATA = "data"
        const val CHAVE_NOME = "nome"
        const val CHAVE_NOME_CIENTIFICO = "nome_cientifico"
        const val CHAVE_URL_IMAGEM = "url_imagem"
    }
}
