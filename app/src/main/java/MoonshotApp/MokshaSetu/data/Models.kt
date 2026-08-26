package MoonshotApp.MokshaSetu.data

data class AssetEntry(
    val id: Int,
    val emoji: String,
    val title: String,
    val noteRes: NoteKey,
    val status: Status,
    val category: Category,
    val nominee: String? = null,
    val nomineeRelation: String? = null
)

enum class Category { FINANCIAL, DIGITAL }
enum class Status { READY, TODO, ACTION }
enum class NoteKey { SEALED, PENDING, BLOCK_RECYCLE, MEMORIALISE, AUTO_CANCEL }

data class Nominee(
    val id: Int,
    val name: String,
    val relation: String,
    val scope: String,
    val verified: Boolean
)

enum class WishKind { MESSAGE, UNFULFILLED, ACTION, SCHEDULED }

data class Wish(
    val id: Int,
    val textRes: Int?,
    val customText: String?,
    val metaRes: MetaKey,
    val customMeta: String?
)

enum class MetaKey { ROHAN, TEMPLE, INSTA, VIDEO }

data class Tier1Action(val id: Int, val labelRes: Int, val armed: Boolean)

data class Tier2Action(
    val id: Int,
    val titleRes: Int,
    val certVerified: Boolean = true,
    val aadhaarVerified: Boolean = true,
    val coAuthDone: Boolean = false,
    val waitingDays: Int = 0,
    val armed: Boolean = false
)

data class ChatMessage(
    val id: Int,
    val textRes: Int?,
    val text: String?,
    val fromAi: Boolean
)

enum class ClaimDotState { DONE, NOW, TODO }

data class ClaimStep(
    val dotState: ClaimDotState,
    val stepNo: Int?,
    val titleRes: Int,
    val chipRes: Int?,
    val detailTitleRes: Int,
    val detailBodyRes: Int
)
