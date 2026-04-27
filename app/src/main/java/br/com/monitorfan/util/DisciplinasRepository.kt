package br.com.monitorfan.util

import android.content.Context
import org.json.JSONObject

object DisciplinasRepository {

    private var cache: Map<String, List<String>>? = null

    fun listarPorCurso(context: Context, curso: String): List<String> {
        val disciplinas = cache ?: carregarDisciplinas(context).also {
            cache = it
        }

        return disciplinas[curso].orEmpty()
    }

    private fun carregarDisciplinas(context: Context): Map<String, List<String>> {
        return try {
            val json = context.assets
                .open("disciplinas.json")
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }

            val objetoJson = JSONObject(json)
            val mapa = mutableMapOf<String, List<String>>()
            val cursos = objetoJson.keys()

            while (cursos.hasNext()) {
                val curso = cursos.next()
                val arrayDisciplinas = objetoJson.getJSONArray(curso)

                val lista = List(arrayDisciplinas.length()) { index ->
                    arrayDisciplinas.getString(index)
                }

                mapa[curso] = lista
            }

            mapa
        } catch (e: Exception) {
            emptyMap()
        }
    }
}