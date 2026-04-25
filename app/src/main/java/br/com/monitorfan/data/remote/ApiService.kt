package br.com.monitorfan.data.remote

import br.com.monitorfan.data.remote.dto.DuvidaDto
import br.com.monitorfan.data.remote.dto.MonitoriaDto
import br.com.monitorfan.data.remote.dto.RespostaDto
import br.com.monitorfan.data.remote.dto.UsuarioDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Preparado para backend real. Troque BASE_URL em RetrofitClient.kt pelo endereço do servidor.
interface ApiService {

    // Usuários
    @GET("usuarios")
    suspend fun buscarUsuarios(): Response<List<UsuarioDto>>

    @POST("usuarios")
    suspend fun enviarUsuario(@Body usuario: UsuarioDto): Response<UsuarioDto>

    // Monitorias
    @GET("monitorias")
    suspend fun buscarMonitorias(): Response<List<MonitoriaDto>>

    @POST("monitorias")
    suspend fun enviarMonitoria(@Body monitoria: MonitoriaDto): Response<MonitoriaDto>

    // Dúvidas
    @GET("duvidas")
    suspend fun buscarDuvidas(): Response<List<DuvidaDto>>

    @GET("duvidas/{curso}")
    suspend fun buscarDuvidasDoCurso(@Path("curso") curso: String): Response<List<DuvidaDto>>

    @POST("duvidas")
    suspend fun enviarDuvida(@Body duvida: DuvidaDto): Response<DuvidaDto>

    // Respostas
    @GET("duvidas/{duvidaId}/respostas")
    suspend fun buscarRespostas(@Path("duvidaId") duvidaId: Long): Response<List<RespostaDto>>

    @POST("duvidas/{duvidaId}/respostas")
    suspend fun enviarResposta(
        @Path("duvidaId") duvidaId: Long,
        @Body resposta: RespostaDto
    ): Response<RespostaDto>
}
