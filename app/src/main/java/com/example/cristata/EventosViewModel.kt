package com.example.cristata

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventosViewModel(application: Application) : AndroidViewModel(application) {
    private val banco = BancoHelper(application)
    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos.asStateFlow()

    // recarrega do banco (chamado sempre que a MainActivity volta a aparecer)
    fun carregar() {
        viewModelScope.launch {
            _eventos.value = withContext(Dispatchers.IO) { banco.listar() }
        }
    }

    override fun onCleared() {
        banco.close()
        super.onCleared()
    }
}