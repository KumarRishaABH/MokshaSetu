package MoonshotApp.MokshaSetu

import MoonshotApp.MokshaSetu.crypto.VaultCipher
import org.junit.Assert.assertThrows
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import javax.crypto.AEADBadTagException

class VaultCipherTest {

    @Test
    fun `encrypt decrypt roundtrip`() {
        val key = VaultCipher.keyFromBytes(VaultCipher.newKeyBytes())
        val secret = "Rohan — the FD for Aarav's college is in SBI. 🎓"
        assertEquals(secret, VaultCipher.decrypt(key, VaultCipher.encrypt(key, secret)))
    }

    @Test
    fun `ciphertexts differ across encryptions due to random iv`() {
        val key = VaultCipher.keyFromBytes(VaultCipher.newKeyBytes())
        assertNotSameText(VaultCipher.encrypt(key, "same input"), VaultCipher.encrypt(key, "same input"))
    }

    private fun assertNotSameText(a: String, b: String) {
        assertTrue(a != b)
    }

    @Test(expected = AEADBadTagException::class)
    fun `wrong key fails authentication`() {
        val blob = VaultCipher.encrypt(VaultCipher.keyFromBytes(VaultCipher.newKeyBytes()), "sealed")
        VaultCipher.decrypt(VaultCipher.keyFromBytes(VaultCipher.newKeyBytes()), blob)
    }

    @Test
    fun `tampered ciphertext throws`() {
        val key = VaultCipher.keyFromBytes(VaultCipher.newKeyBytes())
        val blob = VaultCipher.encrypt(key, "sealed")
        val raw = kotlin.io.encoding.Base64.decode(blob)
        raw[raw.size - 1] = (raw[raw.size - 1].toInt() xor 0x01).toByte()
        assertThrows(Exception::class.java) {
            VaultCipher.decrypt(key, kotlin.io.encoding.Base64.encode(raw))
        }
    }
}
