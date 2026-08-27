package MoonshotApp.MokshaSetu.data

data class Entitlements(
    val nomineeId: Int,
    val assets: List<FinancialAsset>,
    val digitalIdentities: List<DigitalIdentity>,
    val propertyDocs: List<PropertyDoc>
) {
    val totalRupees: Long get() = assets.sumOf { it.nomineeShareRupees }

    val isEmpty: Boolean get() = assets.isEmpty() && digitalIdentities.isEmpty() && propertyDocs.isEmpty()
}

fun entitlementsFor(
    nomineeId: Int,
    assets: List<FinancialAsset>,
    digitalIdentities: List<DigitalIdentity>,
    propertyDocs: List<PropertyDoc>
): Entitlements = Entitlements(
    nomineeId = nomineeId,
    assets = assets.filter { it.nomineeId == nomineeId },
    digitalIdentities = digitalIdentities.filter { it.nomineeId == nomineeId },
    propertyDocs = propertyDocs.filter { it.nomineeId == nomineeId }
)
