package MoonshotApp.MokshaSetu

import MoonshotApp.MokshaSetu.crypto.Shamir
import MoonshotApp.MokshaSetu.crypto.Share
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.security.SecureRandom

class ShamirTest {

    private val random = SecureRandom()

    private fun randomSecret(bytes: Int = 32): ByteArray = ByteArray(bytes).also { random.nextBytes(it) }

    @Test
    fun `split and combine roundtrips with threshold subset`() {
        repeat(25) {
            val secret = randomSecret()
            val shares = Shamir.split(secret, totalShares = 5, threshold = 3)
            assertEquals(5, shares.size)
            val reconstructed = Shamir.combine(shares.take(3), 32)
            assertArrayEquals(secret, reconstructed)
        }
    }

    @Test
    fun `any combination of threshold shares reconstructs`() {
        val secret = randomSecret()
        val shares = Shamir.split(secret, 5, 3)
        listOf(
            listOf(shares[0], shares[2], shares[4]),
            listOf(shares[1], shares[3], shares[4]),
            listOf(shares[0], shares[1], shares[2]),
            listOf(shares[2], shares[3], shares[0])
        ).forEach { subset ->
            assertArrayEquals(secret, Shamir.combine(subset, 32))
        }
    }

    @Test
    fun `insufficient shares fail to reconstruct`() {
        val secret = randomSecret()
        val shares = Shamir.split(secret, 5, 3)
        try {
            val wrong = Shamir.combine(shares.take(2), 32)
            assertNotEquals(secret.toList(), wrong.toList())
        } catch (_: IllegalArgumentException) {
        }
    }

    @Test
    fun `duplicate share indices are rejected`() {
        val secret = randomSecret()
        val shares = Shamir.split(secret, 5, 3)
        assertThrows(IllegalArgumentException::class.java) {
            Shamir.combine(listOf(shares[0], shares[0], shares[1]), 32)
        }
    }

    @Test
    fun `tampered share produces different secret`() {
        val secret = randomSecret()
        val shares = Shamir.split(secret, 5, 3)
        val tampered = Share(shares[1].index, shares[1].value.plus(java.math.BigInteger.ONE))
        assertNotEquals(secret.toList(), Shamir.combine(listOf(shares[0], tampered, shares[2]), 32).toList())
    }

    @Test
    fun `invalid parameters rejected`() {
        val secret = randomSecret(16)
        assertThrows(IllegalArgumentException::class.java) { Shamir.split(secret, 1, 2) }
        assertThrows(IllegalArgumentException::class.java) { Shamir.split(secret, 5, 6) }
        assertThrows(IllegalArgumentException::class.java) { Shamir.split(secret, 5, 1) }
        assertThrows(IllegalArgumentException::class.java) { Shamir.split(ByteArray(0), 5, 3) }
    }

    @Test
    fun `leading zero secrets are preserved`() {
        val secret = ByteArray(32)
        secret[0] = 0
        secret[31] = 42
        val shares = Shamir.split(secret, 5, 2)
        assertArrayEquals(secret, Shamir.combine(shares.take(2), 32))
    }

    @Test
    fun `share type carries index and value`() {
        val shares: List<Share> = Shamir.split(randomSecret(), 5, 3)
        assertEquals(listOf(1, 2, 3, 4, 5), shares.map { it.index })
    }
}
