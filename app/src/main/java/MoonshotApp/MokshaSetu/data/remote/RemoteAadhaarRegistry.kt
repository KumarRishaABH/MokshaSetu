package MoonshotApp.MokshaSetu.data.remote

import MoonshotApp.MokshaSetu.data.ServerConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class RegistryUnavailableException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)

class RemoteAadhaarRegistry(
    private val baseUrl: String = ServerConfig.BASE_URL,
    callTimeoutSeconds: Long = 15
) : AadhaarRegistry {

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient.Builder()
        .callTimeout(callTimeoutSeconds, TimeUnit.SECONDS)
        .build()

    override suspend fun findByNumber(digits: String): AadhaarRecord? = withContext(Dispatchers.IO) {
        retryOnce {
            execute(Request.Builder().url("$baseUrl/aadhaar/$digits").build()) { response ->
                when {
                    response.code == HTTP_NOT_FOUND -> null
                    response.isSuccessful -> parseRecord(response.body?.string().orEmpty())
                    else -> throw RegistryUnavailableException("Registry responded HTTP ${response.code}")
                }
            }
        }
    }

    override suspend fun upsert(record: AadhaarRecord): Unit = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("aadhaarNumber", record.aadhaarNumber)
            .put("holderName", record.holderName)
            .put("dob", record.dob)
            .put("address", record.address)
            .put("mobileLast4", record.mobileLast4)
            .put("active", record.active)
        val request = Request.Builder()
            .url("$baseUrl/aadhaar")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()
        execute(request) { response ->
            if (!response.isSuccessful) {
                throw RegistryUnavailableException("Registry responded HTTP ${response.code}")
            }
        }
    }

    override suspend fun isReachable(): Boolean = withContext(Dispatchers.IO) {
        try {
            retryOnce {
                val request = Request.Builder().url("$baseUrl/health").build()
                client.newCall(request).execute().use { response ->
                    response.isSuccessful &&
                        JSONObject(response.body?.string().orEmpty()).optBoolean("ok", false)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun <T> retryOnce(block: () -> T): T {
        return try {
            block()
        } catch (e: RegistryUnavailableException) {
            delay(RETRY_DELAY_MS)
            block()
        }
    }

    private inline fun <T> execute(request: Request, handle: (Response) -> T): T {
        return try {
            client.newCall(request).execute().use(handle)
        } catch (e: IOException) {
            throw RegistryUnavailableException("Registry unreachable", e)
        }
    }

    private fun parseRecord(raw: String): AadhaarRecord? {
        if (raw.isBlank()) return null
        return try {
            val json = JSONObject(raw)
            AadhaarRecord(
                aadhaarNumber = json.getString("aadhaarNumber"),
                holderName = json.optString("holderName"),
                dob = json.optString("dob"),
                address = json.optString("address"),
                mobileLast4 = json.optString("mobileLast4", "0000"),
                active = json.optBoolean("active", true)
            )
        } catch (e: JSONException) {
            throw RegistryUnavailableException("Registry returned a non-JSON response", e)
        }
    }

    companion object {
        private const val HTTP_NOT_FOUND = 404
        private const val RETRY_DELAY_MS = 300L
    }
}