package br.com.monitorfan.ui.viewmodel

sealed class UiState<out T> {
    object Carregando : UiState<Nothing>()
    data class Sucesso<T>(val dados: T) : UiState<T>()
    data class Erro(val mensagem: String) : UiState<Nothing>()
    object Vazio : UiState<Nothing>()
}
