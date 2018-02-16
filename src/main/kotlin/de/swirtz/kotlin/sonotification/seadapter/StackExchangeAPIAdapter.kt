package de.swirtz.kotlin.sonotification.seadapter

import com.google.gson.Gson
import de.swirtz.kotlin.sonotification.data.Question
import de.swirtz.kotlin.sonotification.data.StackExchangeApiResponse
import de.swirtz.kotlin.sonotification.getLogger
import okhttp3.OkHttpClient
import okhttp3.Request

class StackExchangeAPIAdapter : APIAdapter {

    companion object {
        private val logger = getLogger<StackExchangeAPIAdapter>()
    }

    private val key = "3PbggULhOu0xoRBUHUXxjg(("

    private val kotlinApiCall =
        "https://api.stackexchange.com/2.2/questions?pagesize=20&order=desc&sort=activity&tagged=kotlin&site=stackoverflow&key=$key"

    private val client: OkHttpClient = OkHttpClient.Builder().build()
    override fun getLastQuestions() = with(client) {
        val request = Request.Builder().url(kotlinApiCall).build()
        val response = request.let {
            newCall(it).execute().use {
                it.code().let {
                    if (it != 200) throw IllegalStateException("API responded with $it")
                }
                it.body()?.source()?.readByteArray()?.let { String(it) }
                        ?: throw IllegalStateException("No response from server!")
            }
        }
        Gson().fromJson(response, StackExchangeApiResponse::class.java).items
                ?: throw IllegalStateException("No response from API!")
    }
}

interface APIAdapter {
    fun getLastQuestions(): List<Question>
}


