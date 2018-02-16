package de.swirtz.kotlin.sonotification.data

import com.google.gson.annotations.SerializedName

data class StackExchangeApiResponse(val items: List<Question>?)
data class User(@SerializedName("user_id") val id: String, @SerializedName("display_name") val name: String)
data class Question(
    val tags: List<String>,
    val owner: User,
    @SerializedName("is_answered") val answered: Boolean,
    val score: Int,
    val title: String,
    val link: String,
    @SerializedName("creation_date") val created: Long,
    @SerializedName("question_id") val id: Long
)