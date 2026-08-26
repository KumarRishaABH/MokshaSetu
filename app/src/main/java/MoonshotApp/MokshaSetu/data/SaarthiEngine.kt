package MoonshotApp.MokshaSetu.data

import MoonshotApp.MokshaSetu.R

enum class SaarthiIntent { CERTIFICATE, CLAIMS, SIM, MEMORIAL, GRIEF, FALLBACK }

object SaarthiEngine {

    private val certificateWords = listOf("certificate", "digilocker", "praman", "प्रमाण", "death proof")
    private val claimWords = listOf("claim", "bank", "start", "money", "शुरू", "दावा", "process")
    private val simWords = listOf("sim", "airtel", "number", "jio", "नंबर")
    private val memorialWords = listOf("instagram", "memorial", "social", "facebook", "whatsapp", "account")
    private val griefWords = listOf("sorry", "sad", "miss", "alone", "cry", "help me", "बहुत", "दुख")

    fun classify(input: String): SaarthiIntent {
        val text = input.lowercase()
        return when {
            certificateWords.any(text::contains) -> SaarthiIntent.CERTIFICATE
            claimWords.any(text::contains) -> SaarthiIntent.CLAIMS
            simWords.any(text::contains) -> SaarthiIntent.SIM
            memorialWords.any(text::contains) -> SaarthiIntent.MEMORIAL
            griefWords.any(text::contains) -> SaarthiIntent.GRIEF
            else -> SaarthiIntent.FALLBACK
        }
    }

    fun replyRes(intent: SaarthiIntent): Int = when (intent) {
        SaarthiIntent.CERTIFICATE -> R.string.saarthi_reply_certificate
        SaarthiIntent.CLAIMS -> R.string.saarthi_reply_claims
        SaarthiIntent.SIM -> R.string.saarthi_reply_sim
        SaarthiIntent.MEMORIAL -> R.string.saarthi_reply_memorial
        SaarthiIntent.GRIEF -> R.string.saarthi_reply_grief
        SaarthiIntent.FALLBACK -> R.string.saarthi_reply_fallback
    }
}
