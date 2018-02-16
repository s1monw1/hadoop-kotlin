package de.swirtz.kotlin.sonotification.seadapter

import de.swirtz.kotlin.sonotification.data.Question
import de.swirtz.kotlin.sonotification.data.User
import de.swirtz.kotlin.sonotification.getLogger
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class MockAPIAdapter : APIAdapter {

    override fun getLastQuestions() = questions.also { logger.debug("current questions: ${it.size}") }

    companion object {
        private val logger = getLogger<MockAPIAdapter>()
    }

    private val questions = mutableListOf<Question>()

    init {
        launch {
            (0..100).forEach {
                questions.add(
                    Question(
                        listOf("kotlin, android$it"),
                        User("M", "id$it"),
                        it % 2 == 0,
                        it * 2,
                        "question$it",
                        "https://$it",
                        it * 3L, it.toLong()
                    )
                )
                delay(5000)
            }

        }
    }
}
