package MoonshotApp.MokshaSetu

import MoonshotApp.MokshaSetu.data.remote.AadhaarRecord
import MoonshotApp.MokshaSetu.data.remote.FakeAadhaarRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AadhaarRegistryTest {

    private val planner = AadhaarRecord(
        aadhaarNumber = "901234567890",
        holderName = "Anjali Sharma",
        dob = "14 Aug 1984",
        address = "Kothrud, Pune, Maharashtra 411038",
        mobileLast4 = "4021",
        active = true
    )

    @Test
    fun findsASeededRecord() = runBlocking {
        val registry = FakeAadhaarRegistry(listOf(planner))

        assertEquals(planner, registry.findByNumber("901234567890"))
    }

    @Test
    fun unknownNumberIsNull() = runBlocking {
        val registry = FakeAadhaarRegistry(listOf(planner))

        assertNull(registry.findByNumber("111111111111"))
    }

    @Test
    fun upsertInsertsThenOverwrites() = runBlocking {
        val registry = FakeAadhaarRegistry()

        assertNull(registry.findByNumber("784512903366"))
        registry.upsert(AadhaarRecord("784512903366", "Rohan Sharma", "02 Nov 1991", "Kothrud, Pune", "3366", active = true))
        assertEquals("Rohan Sharma", registry.findByNumber("784512903366")?.holderName)
        registry.upsert(AadhaarRecord("784512903366", "Rohan S.", "02 Nov 1991", "Kothrud, Pune", "3366", active = false))
        assertEquals("Rohan S.", registry.findByNumber("784512903366")?.holderName)
        assertFalse(registry.findByNumber("784512903366")!!.active)
    }

    @Test
    fun fakeIsAlwaysReachable() = runBlocking {
        assertTrue(FakeAadhaarRegistry().isReachable())
    }
}