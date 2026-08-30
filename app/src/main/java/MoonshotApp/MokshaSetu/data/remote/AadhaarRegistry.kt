package MoonshotApp.MokshaSetu.data.remote

data class AadhaarRecord(
    val aadhaarNumber: String,
    val holderName: String,
    val dob: String,
    val address: String,
    val mobileLast4: String,
    val active: Boolean
)

interface AadhaarRegistry {
    suspend fun findByNumber(digits: String): AadhaarRecord?

    suspend fun upsert(record: AadhaarRecord)

    suspend fun isReachable(): Boolean
}

class FakeAadhaarRegistry(initial: List<AadhaarRecord> = emptyList()) : AadhaarRegistry {

    private val records = initial.associateBy { it.aadhaarNumber }.toMutableMap()

    override suspend fun findByNumber(digits: String): AadhaarRecord? = records[digits]

    override suspend fun upsert(record: AadhaarRecord) {
        records[record.aadhaarNumber] = record
    }

    override suspend fun isReachable(): Boolean = true
}