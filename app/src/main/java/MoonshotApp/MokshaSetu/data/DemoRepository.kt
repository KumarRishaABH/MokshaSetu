package MoonshotApp.MokshaSetu.data

import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.crypto.Shamir
import MoonshotApp.MokshaSetu.crypto.VaultCipher
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import MoonshotApp.MokshaSetu.crypto.Share

object DemoRepository {

    const val TOTAL_SHARES = 5
    const val THRESHOLD = 3

    private const val SEALED_MESSAGE =
        "Rohan — the FD for Aarav's college is in SBI, not our joint account. Promise me he finishes engineering."

    val plannerName = "Anjali"
    val survivorName = "Rohan"

    val assets = mutableStateListOf(
        AssetEntry(1, "🏦", "HDFC Bank ••4021", NoteKey.SEALED, Status.READY, Category.FINANCIAL, "Rohan", "spouse"),
        AssetEntry(2, "📲", "UPI / GPay", NoteKey.SEALED, Status.READY, Category.FINANCIAL),
        AssetEntry(3, "📈", "Zerodha Demat", NoteKey.PENDING, Status.TODO, Category.FINANCIAL),
        AssetEntry(4, "📱", "Airtel ••88932", NoteKey.BLOCK_RECYCLE, Status.ACTION, Category.DIGITAL),
        AssetEntry(5, "📸", "Instagram · WhatsApp", NoteKey.MEMORIALISE, Status.READY, Category.DIGITAL),
        AssetEntry(6, "📺", "Netflix · Prime", NoteKey.AUTO_CANCEL, Status.READY, Category.DIGITAL)
    )

    val nominees = mutableStateListOf(
        Nominee(1, "R", "Rohan Mehta", "Spouse · Finances + SIM", true),
        Nominee(2, "M", "Meera (Mother)", "Social & messages", true),
        Nominee(3, "A", "Adv. Kapoor", "Executor · legal escalation", false)
    )

    val wishes = mutableStateListOf(
        Wish(1, R.string.wish_rohan_text, null, MetaKey.ROHAN, null),
        Wish(2, R.string.wish_temple_text, null, MetaKey.TEMPLE, null),
        Wish(3, R.string.wish_insta_text, null, MetaKey.INSTA, null),
        Wish(4, null, "Video message for Aarav's 21st birthday 🎥", MetaKey.VIDEO, null)
    )

    val tier1Actions = mutableStateListOf(
        Tier1Action(1, R.string.tier1_sim, true),
        Tier1Action(2, R.string.tier1_subs, true),
        Tier1Action(3, R.string.tier1_fraud, false),
        Tier1Action(4, R.string.tier1_social, false)
    )

    val tier2Actions = mutableStateListOf(
        Tier2Action(1, R.string.tier2_release_title),
        Tier2Action(2, R.string.tier2_fd_title)
    )

    val chat = mutableStateListOf<ChatMessage>()

    val nomineeUnlocked = mutableStateOf(false)

    var decryptedMessage by mutableStateOf<String?>(null)
        private set

    private val masterKeyBytes = VaultCipher.newKeyBytes()
    private val masterShares: List<Share> = Shamir.split(masterKeyBytes, TOTAL_SHARES, THRESHOLD)
    private val sealedBlob = VaultCipher.encrypt(VaultCipher.keyFromBytes(masterKeyBytes), SEALED_MESSAGE)

    init {
        seedChat()
    }

    private fun seedChat() {
        chat.addAll(
            listOf(
                ChatMessage(1, R.string.saarthi_seed_1, null, fromAi = true),
                ChatMessage(2, R.string.saarthi_seed_user_1, null, fromAi = false),
                ChatMessage(3, R.string.saarthi_seed_2, null, fromAi = true),
                ChatMessage(4, R.string.saarthi_seed_user_2, null, fromAi = false),
                ChatMessage(5, R.string.saarthi_seed_3, null, fromAi = true)
            )
        )
    }

    fun legacyScore(): Int {
        val assigned = assets.count { it.nominee != null }
        val assetScore = assigned * 40 / assets.size.coerceAtLeast(1)
        val wishScore = (wishes.size * 30 / 4).coerceAtMost(30)
        val tier1Score = tier1Actions.count { it.armed } * 15 / tier1Actions.size
        val tier2Score = if (tier2Actions.any { it.armed }) 15 else 0
        return (assetScore + wishScore + tier1Score + tier2Score).coerceIn(0, 100)
    }

    fun assignNominee(assetId: Int, nomineeName: String) {
        val idx = assets.indexOfFirst { it.id == assetId }
        if (idx >= 0) {
            assets[idx] = assets[idx].copy(nominee = nomineeName, status = Status.READY, noteRes = NoteKey.SEALED)
        }
    }

    fun addAsset(emoji: String, title: String, secret: String) {
        val id = (assets.maxOfOrNull { it.id } ?: 0) + 1
        if (secret.isNotBlank()) {
            sealedExtra[id] = VaultCipher.encrypt(VaultCipher.keyFromBytes(masterKeyBytes), secret)
        }
        assets.add(AssetEntry(id, emoji.ifBlank { "🗂️" }, title, NoteKey.SEALED, Status.READY, Category.FINANCIAL))
    }

    private val sealedExtra = mutableMapOf<Int, String>()

    fun addNominee(name: String, relation: String, scope: String) {
        val initial = name.trim().take(1).uppercase().ifBlank { "?" }
        nominees.add(Nominee((nominees.maxOfOrNull { it.id } ?: 0) + 1, initial, relation, scope, verified = false))
    }

    fun addWish(text: String, recipient: String) {
        wishes.add(Wish((wishes.maxOfOrNull { it.id } ?: 0) + 1, null, text, MetaKey.ROHAN, recipient))
    }

    fun appendUserMessage(text: String) {
        chat.add(ChatMessage(nextChatId(), null, text, fromAi = false))
    }

    fun appendAiMessage(resId: Int) {
        chat.add(ChatMessage(nextChatId(), resId, null, fromAi = true))
    }

    private fun nextChatId(): Int = (chat.maxOfOrNull { it.id } ?: 0) + 1

    fun toggleTier1(id: Int) {
        val idx = tier1Actions.indexOfFirst { it.id == id }
        if (idx >= 0) tier1Actions[idx] = tier1Actions[idx].copy(armed = !tier1Actions[idx].armed)
    }

    fun requestCoAuth(id: Int) {
        updateTier2(id) { it.copy(coAuthDone = true) }
    }

    fun advanceWaiting(id: Int) {
        updateTier2(id) { it.copy(waitingDays = (it.waitingDays + 7).coerceAtMost(21)) }
    }

    fun armTier2(id: Int): Boolean {
        val action = tier2Actions.firstOrNull { it.id == id } ?: return false
        val ready = action.certVerified && action.aadhaarVerified && action.coAuthDone && action.waitingDays >= 21
        if (ready) updateTier2(id) { it.copy(armed = true) }
        return ready
    }

    private fun updateTier2(id: Int, transform: (Tier2Action) -> Tier2Action) {
        val idx = tier2Actions.indexOfFirst { it.id == id }
        if (idx >= 0) tier2Actions[idx] = transform(tier2Actions[idx])
    }

    fun attemptUnlock(): Boolean {
        val combined = try {
            Shamir.combine(masterShares.drop(1).take(THRESHOLD), masterKeyBytes.size)
        } catch (_: IllegalArgumentException) {
            return false
        }
        return try {
            decryptedMessage = VaultCipher.decrypt(VaultCipher.keyFromBytes(combined), sealedBlob)
            nomineeUnlocked.value = true
            true
        } catch (_: Exception) {
            false
        }
    }
}
