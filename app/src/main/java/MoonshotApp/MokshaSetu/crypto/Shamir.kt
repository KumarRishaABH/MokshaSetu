package MoonshotApp.MokshaSetu.crypto

import java.math.BigInteger
import java.security.SecureRandom

data class Share(val index: Int, val value: BigInteger)

object Shamir {

    private val PRIME: BigInteger = BigInteger.valueOf(2).pow(521).minus(BigInteger.ONE)
    private const val SHARE_BYTES = 66
    private val RANDOM = SecureRandom()

    fun split(secret: ByteArray, totalShares: Int, threshold: Int): List<Share> {
        require(totalShares >= 2) { "Need at least two shares" }
        require(threshold in 2..totalShares) { "Threshold must be within 2..totalShares" }
        require(secret.isNotEmpty()) { "Secret must not be empty" }
        val secretInt = BigInteger(1, secret)
        require(secretInt < PRIME) { "Secret too large for prime field" }

        val degree = threshold - 1
        val coefficients = Array(degree) { BigInteger(SHARE_BYTES * 8, RANDOM).mod(PRIME) }
        return (1..totalShares).map { x ->
            var y = BigInteger.ZERO
            for (k in degree downTo 0) {
                y = y.multiply(BigInteger.valueOf(x.toLong())).mod(PRIME)
                y = y.plus(if (k == 0) secretInt else coefficients[k - 1]).mod(PRIME)
            }
            Share(x, y)
        }
    }

    fun combine(shares: List<Share>, outputLength: Int): ByteArray {
        require(shares.size >= 2) { "Need at least two shares" }
        require(outputLength > 0) { "outputLength must be positive" }
        val distinctX = shares.map { it.index }.toSet()
        require(distinctX.size == shares.size) { "Duplicate share indices" }

        var result = BigInteger.ZERO
        shares.forEach { share ->
            var numerator = BigInteger.ONE
            var denominator = BigInteger.ONE
            shares.forEach { other ->
                if (other.index != share.index) {
                    numerator = numerator.multiply(BigInteger.valueOf(other.index.toLong())).mod(PRIME)
                    denominator = denominator
                        .multiply(BigInteger.valueOf((other.index - share.index).toLong()))
                        .mod(PRIME)
                }
            }
            val lagrange = numerator.multiply(denominator.modInverse(PRIME)).mod(PRIME)
            result = result.plus(share.value.multiply(lagrange)).mod(PRIME)
        }
        val magnitudeBytes = result.toByteArray()
        val stripped = if (magnitudeBytes.size > 1 && magnitudeBytes[0] == ZERO_BYTE) {
            magnitudeBytes.copyOfRange(1, magnitudeBytes.size)
        } else {
            magnitudeBytes
        }
        require(stripped.size <= outputLength) { "Reconstructed secret longer than outputLength" }
        return if (stripped.size == outputLength) {
            stripped
        } else {
            ByteArray(outputLength - stripped.size) + stripped
        }
    }

    private val ZERO_BYTE: Byte = 0
}
