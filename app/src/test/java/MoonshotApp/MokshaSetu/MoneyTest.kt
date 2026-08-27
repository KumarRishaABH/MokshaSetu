package MoonshotApp.MokshaSetu

import MoonshotApp.MokshaSetu.data.formatRupees
import MoonshotApp.MokshaSetu.data.maskAadhaar
import org.junit.Assert.assertEquals
import org.junit.Test

class MoneyTest {

    @Test
    fun groupsRupeesTheIndianWay() {
        assertEquals("₹0", formatRupees(0))
        assertEquals("₹950", formatRupees(950))
        assertEquals("₹4,021", formatRupees(4_021))
        assertEquals("₹4,82,650", formatRupees(482_650))
        assertEquals("₹18,00,000", formatRupees(1_800_000))
        assertEquals("₹85,00,000", formatRupees(8_500_000))
        assertEquals("₹1,23,45,67,890", formatRupees(1_234_567_890))
    }

    @Test
    fun keepsTheSignForNegativeAmounts() {
        assertEquals("-₹4,82,650", formatRupees(-482_650))
    }

    @Test
    fun masksAllButTheLastFourAadhaarDigits() {
        assertEquals("XXXX XXXX 7890", maskAadhaar("901234567890"))
        assertEquals("XXXX XXXX 7890", maskAadhaar("9012 3456 7890"))
        assertEquals("XXXX XXXX XXXX", maskAadhaar("12"))
    }
}
