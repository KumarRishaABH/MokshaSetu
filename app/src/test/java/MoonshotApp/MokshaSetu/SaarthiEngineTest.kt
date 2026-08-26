package MoonshotApp.MokshaSetu

import MoonshotApp.MokshaSetu.data.SaarthiEngine
import MoonshotApp.MokshaSetu.data.SaarthiIntent
import org.junit.Assert.assertEquals
import org.junit.Test

class SaarthiEngineTest {

    @Test
    fun `certificate intent detected`() {
        assertEquals(SaarthiIntent.CERTIFICATE, SaarthiEngine.classify("The certificate is on DigiLocker"))
        assertEquals(SaarthiIntent.CERTIFICATE, SaarthiEngine.classify("मृत्यु प्रमाण है मेरे पास"))
    }

    @Test
    fun `claims intent detected`() {
        assertEquals(SaarthiIntent.CLAIMS, SaarthiEngine.classify("Please start the claims"))
        assertEquals(SaarthiIntent.CLAIMS, SaarthiEngine.classify("Which bank has her money?"))
    }

    @Test
    fun `sim intent detected`() {
        assertEquals(SaarthiIntent.SIM, SaarthiEngine.classify("What about the SIM?"))
        assertEquals(SaarthiIntent.SIM, SaarthiEngine.classify("Her Airtel number"))
    }

    @Test
    fun `memorial intent detected`() {
        assertEquals(SaarthiIntent.MEMORIAL, SaarthiEngine.classify("Memorialise her Instagram account"))
    }

    @Test
    fun `grief intent detected`() {
        assertEquals(SaarthiIntent.GRIEF, SaarthiEngine.classify("I miss her so much, I feel alone"))
    }

    @Test
    fun `fallback for unknown input`() {
        assertEquals(SaarthiIntent.FALLBACK, SaarthiEngine.classify("Hello there"))
    }
}
