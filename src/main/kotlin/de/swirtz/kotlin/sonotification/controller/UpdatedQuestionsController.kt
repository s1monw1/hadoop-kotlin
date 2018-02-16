package de.swirtz.kotlin.sonotification.controller

import com.google.inject.Inject
import de.swirtz.kotlin.sonotification.DEFAULT_POLL_INTERVAL
import de.swirtz.kotlin.sonotification.data.Question
import de.swirtz.kotlin.sonotification.data.User
import de.swirtz.kotlin.sonotification.getLogger
import de.swirtz.kotlin.sonotification.seadapter.APIAdapter
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import tornadofx.*
import kotlin.properties.Delegates

class UpdatedQuestionsController @Inject constructor(private val adapter: APIAdapter) : Controller() {

    private val logger = getLogger<UpdatedQuestionsController>()

    val questions = ArrayList(adapter.getLastQuestions()).observable()

    private val QUESTION_INIT = Question(listOf(), User("", ""), false, 0, "", "", 0L, 0)

    private var lastQuestion: Question by Delegates.observable(
        questions.firstOrNull() ?: QUESTION_INIT
    ) { _, oldValue, newValue ->
        if (oldValue != QUESTION_INIT && oldValue != newValue) {
            logger.debug("Last question changed from $oldValue to $newValue")
            fire(LastQuestionUpdated(lastQuestion))
        }
    }

    init {
        launch {
            while (true) {
                delay(DEFAULT_POLL_INTERVAL)
                with(questions) {
                    adapter.getLastQuestions().let { response ->
                        clear()
                        response.forEach { questions.add(it) }
                    }

                    lastQuestion = maxBy(Question::created).also {
                        logger.debug("Last question is: $it")
                    } ?: lastQuestion
                }
            }
        }
    }
}

class LastQuestionUpdated(val q: Question) : FXEvent()

