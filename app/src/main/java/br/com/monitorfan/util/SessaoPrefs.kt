package br.com.monitorfan.util

import android.content.Context

object SessaoPrefs {
    private const val NOME_ARQUIVO = "monitorfan_sessao"
    private const val CHAVE_ID = "usuario_id"
    private const val SEM_SESSAO = -1L

    fun salvar(context: Context, usuarioId: Long) {
        context.getSharedPreferences(NOME_ARQUIVO, Context.MODE_PRIVATE)
            .edit().putLong(CHAVE_ID, usuarioId).apply()
    }

    fun recuperar(context: Context): Long? {
        val id = context.getSharedPreferences(NOME_ARQUIVO, Context.MODE_PRIVATE)
            .getLong(CHAVE_ID, SEM_SESSAO)
        return if (id == SEM_SESSAO) null else id
    }

    fun limpar(context: Context) {
        context.getSharedPreferences(NOME_ARQUIVO, Context.MODE_PRIVATE)
            .edit().clear().apply()
    }
}
