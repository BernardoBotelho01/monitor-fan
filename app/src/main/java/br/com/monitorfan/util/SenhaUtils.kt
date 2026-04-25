package br.com.monitorfan.util

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object SenhaUtils {
    private const val ALGORITMO = "PBKDF2WithHmacSHA256"
    private const val ITERACOES = 65_536
    private const val COMPRIMENTO_BITS = 256
    private const val PREFIXO = "h1"

    fun hashear(senha: String): String {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hash = pbkdf2(senha, salt)
        val saltB64 = Base64.encodeToString(salt, Base64.NO_WRAP)
        val hashB64 = Base64.encodeToString(hash, Base64.NO_WRAP)
        return "$PREFIXO:$saltB64:$hashB64"
    }

    fun verificar(senha: String, armazenado: String): Boolean {
        if (!armazenado.startsWith("$PREFIXO:")) return senha == armazenado // migração: senha legada em texto puro
        return try {
            val partes = armazenado.split(":")
            if (partes.size != 3) return false
            val salt = Base64.decode(partes[1], Base64.NO_WRAP)
            val hashArmazenado = Base64.decode(partes[2], Base64.NO_WRAP)
            MessageDigest.isEqual(pbkdf2(senha, salt), hashArmazenado)
        } catch (_: Exception) { false }
    }

    fun estaHasheada(armazenado: String) = armazenado.startsWith("$PREFIXO:")

    private fun pbkdf2(senha: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(senha.toCharArray(), salt, ITERACOES, COMPRIMENTO_BITS)
        return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).encoded
    }
}
