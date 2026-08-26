package MoonshotApp.MokshaSetu.crypto

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

object VaultCipher {

    private const val KEY_BITS = 256
    private const val IV_BYTES = 12
    private const val TAG_BITS = 128
    private val RANDOM = SecureRandom()

    fun newKeyBytes(): ByteArray =
        KeyGenerator.getInstance("AES").apply { init(KEY_BITS) }.generateKey().encoded

    fun keyFromBytes(bytes: ByteArray): SecretKey = SecretKeySpec(bytes, "AES")

    fun encrypt(key: SecretKey, plain: String): String {
        val iv = ByteArray(IV_BYTES).also { RANDOM.nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_BITS, iv))
        return Base64.encode(iv + cipher.doFinal(plain.encodeToByteArray()))
    }

    fun decrypt(key: SecretKey, blob: String): String {
        val raw = Base64.decode(blob)
        val iv = raw.copyOfRange(0, IV_BYTES)
        val ct = raw.copyOfRange(IV_BYTES, raw.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_BITS, iv))
        return cipher.doFinal(ct).decodeToString()
    }
}
