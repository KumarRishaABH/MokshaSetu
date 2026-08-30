package MoonshotApp.MokshaSetu

import MoonshotApp.MokshaSetu.data.Fixtures
import MoonshotApp.MokshaSetu.data.entitlementsFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EntitlementsTest {

    private val assets = Fixtures.assets()
    private val identities = Fixtures.digitalIdentities()
    private val docs = Fixtures.propertyDocs()

    @Test
    fun returnsOnlyTheItemsBelongingToThatNominee() {
        val result = entitlementsFor(2, assets, identities, docs)

        assertTrue(result.assets.all { it.splitFor(2) != null })
        assertTrue(result.digitalIdentities.all { it.nomineeId == 2 })
        assertTrue(result.propertyDocs.all { doc -> doc.splits.any { it.nomineeId == 2 } })
    }

    @Test
    fun excludesAssetsThatHaveNoNominee() {
        val everyEntitledId = Fixtures.nominees()
            .flatMap { entitlementsFor(it.id, assets, identities, docs).assets }
            .map { it.id }
            .toSet()
        val unassignedIds = assets.filter { !it.isAssigned }.map { it.id }

        assertTrue(unassignedIds.isNotEmpty())
        assertTrue(unassignedIds.none { it in everyEntitledId })
    }

    @Test
    fun totalUsesTheRegisteredSharePercentage() {
        val result = entitlementsFor(1, assets, identities, docs)
        val expected = assets
            .filter { it.splitFor(1) != null }
            .sumOf { it.shareRupeesFor(1) }

        assertEquals(expected, result.totalRupees)
        assertTrue(result.totalRupees < assets.filter { it.splitFor(1) != null }.sumOf { it.valueRupees })
    }

    @Test
    fun anUnknownNomineeGetsNothing() {
        val result = entitlementsFor(9_999, assets, identities, docs)

        assertTrue(result.isEmpty)
        assertEquals(0L, result.totalRupees)
    }
}
