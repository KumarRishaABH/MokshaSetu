package MoonshotApp.MokshaSetu.data

import MoonshotApp.MokshaSetu.R

object Fixtures {

    const val PLANNER_AADHAAR = "901234567890"
    const val NOMINEE_AADHAAR = "784512903366"
    const val DEMO_OTP = "123456"

    const val VIA_ACCOUNT_AGGREGATOR = "Account Aggregator"
    const val VIA_UDGAM = "UDGAM · DEA Fund"
    const val VIA_SELF = "Registered by you"

    val plannerProfile = AadhaarProfile(
        maskedAadhaar = maskAadhaar(PLANNER_AADHAAR),
        name = "Anjali Sharma",
        dob = "14 Aug 1984",
        address = "Kothrud, Pune, Maharashtra 411038"
    )

    val nomineeProfile = AadhaarProfile(
        maskedAadhaar = maskAadhaar(NOMINEE_AADHAAR),
        name = "Rohan Sharma",
        dob = "02 Nov 1991",
        address = "Kothrud, Pune, Maharashtra 411038"
    )

    val deathCertificate = DeathCertificate(
        registrationNo = "MH/PUN/2026/0148923",
        state = "Maharashtra",
        issuedOn = "18 Feb 2026",
        deceasedName = "Anjali Sharma",
        fileName = null
    )

    val states = listOf(
        "Maharashtra",
        "Karnataka",
        "Delhi",
        "Tamil Nadu",
        "Uttar Pradesh",
        "West Bengal",
        "Gujarat",
        "Kerala"
    )

    const val PROPERTY_ASSET_ID = 7

    fun discoverableAssets(): List<FinancialAsset> = listOf(
        FinancialAsset(
            id = 1,
            kind = AssetKind.BANK,
            institution = "HDFC Bank",
            maskedId = "Savings ••4021",
            valueRupees = 482_650,
            nomineeId = 1,
            discoveredVia = VIA_ACCOUNT_AGGREGATOR
        ),
        FinancialAsset(
            id = 2,
            kind = AssetKind.BANK,
            institution = "State Bank of India",
            maskedId = "Savings ••7788",
            valueRupees = 214_300,
            nomineeId = null,
            discoveredVia = VIA_ACCOUNT_AGGREGATOR
        ),
        FinancialAsset(
            id = 3,
            kind = AssetKind.BANK,
            institution = "Punjab National Bank",
            maskedId = "Dormant ••1902",
            valueRupees = 420_000,
            nomineeId = null,
            discoveredVia = VIA_UDGAM
        ),
        FinancialAsset(
            id = 4,
            kind = AssetKind.DEMAT,
            institution = "Zerodha",
            maskedId = "Demat ••ZR41",
            valueRupees = 635_400,
            nomineeId = 3,
            discoveredVia = VIA_ACCOUNT_AGGREGATOR
        ),
        FinancialAsset(
            id = 5,
            kind = AssetKind.INSURANCE,
            institution = "LIC of India",
            maskedId = "Policy ••309",
            valueRupees = 1_800_000,
            nomineeId = 2,
            discoveredVia = VIA_ACCOUNT_AGGREGATOR
        ),
        FinancialAsset(
            id = 6,
            kind = AssetKind.INSURANCE,
            institution = "HDFC Life Sanchay",
            maskedId = "Policy ••7712",
            valueRupees = 950_000,
            nomineeId = 1,
            sharePercent = 50,
            discoveredVia = VIA_ACCOUNT_AGGREGATOR
        )
    )

    fun propertyAsset(): FinancialAsset = FinancialAsset(
        id = PROPERTY_ASSET_ID,
        kind = AssetKind.PROPERTY,
        institution = "Flat 402, Nirvana Towers, Pune",
        maskedId = "Sale deed MH/PUN/2016/44120",
        valueRupees = 8_500_000,
        nomineeId = 1,
        discoveredVia = VIA_SELF
    )

    fun assets(): List<FinancialAsset> = discoverableAssets() + propertyAsset()

    fun propertyDocs(): List<PropertyDoc> = listOf(
        PropertyDoc(
            id = PROPERTY_ASSET_ID,
            title = "Flat 402, Nirvana Towers, Pune",
            fileName = "sale-deed-flat-402.pdf",
            nomineeId = 1
        )
    )

    fun nominees(): List<Nominee> = listOf(
        Nominee(1, "Rohan Sharma", "Spouse", maskAadhaar(NOMINEE_AADHAAR), verified = true),
        Nominee(2, "Meera Sharma", "Mother", maskAadhaar("562291445521"), verified = true),
        Nominee(3, "Aarav Sharma", "Son", maskAadhaar("330871268814"), verified = false)
    )

    fun digitalIdentities(): List<DigitalIdentity> = listOf(
        DigitalIdentity(
            id = 1,
            platform = "Instagram",
            emoji = "📸",
            username = "@anjali.teaches",
            password = "Diya@2019!",
            nomineeId = 2,
            afterDeathAction = DigitalAction.MEMORIALISE
        ),
        DigitalIdentity(
            id = 2,
            platform = "Google",
            emoji = "🔑",
            username = "anjali.sharma@example.com",
            password = "SetuG#4482",
            nomineeId = 1,
            afterDeathAction = DigitalAction.TRANSFER_ACCESS
        ),
        DigitalIdentity(
            id = 3,
            platform = "Netflix",
            emoji = "📺",
            username = "anjali.sharma@example.com",
            password = "NflxDemo#77",
            nomineeId = 3,
            afterDeathAction = DigitalAction.DELETE
        )
    )

    fun wishes(): List<Wish> = listOf(
        Wish(1, R.string.wish_fd_text, null, MetaKey.ROHAN, null),
        Wish(2, R.string.wish_temple_text, null, MetaKey.TEMPLE, null),
        Wish(3, R.string.wish_insta_text, null, MetaKey.INSTA, null),
        Wish(4, R.string.wish_video_text, null, MetaKey.VIDEO, null)
    )

    fun profileFor(aadhaar: String): AadhaarProfile {
        val digits = aadhaar.filter { it.isDigit() }
        return when (digits) {
            PLANNER_AADHAAR -> plannerProfile
            NOMINEE_AADHAAR -> nomineeProfile
            else -> AadhaarProfile(
                maskedAadhaar = maskAadhaar(digits),
                name = "Demo Resident",
                dob = "01 Jan 1990",
                address = "Demo address, India"
            )
        }
    }
}
