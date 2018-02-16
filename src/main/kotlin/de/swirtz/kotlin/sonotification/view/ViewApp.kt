package de.swirtz.kotlin.sonotification.view

import de.swirtz.kotlin.sonotification.controller.LastQuestionUpdated
import de.swirtz.kotlin.sonotification.controller.UpdatedQuestionsController
import de.swirtz.kotlin.sonotification.data.Question
import de.swirtz.kotlin.sonotification.getLogger
import javafx.geometry.Pos
import javafx.scene.layout.GridPane
import javafx.util.Duration
import tornadofx.*
import tornadofx.controlsfx.infoNotification
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class QuestionsView : View() {
    private val logger = getLogger<QuestionsView>()

    private val controller: UpdatedQuestionsController by di()
    override val root = GridPane()

    init {
        with(root) {
            row {
                vbox {
                    label("StackOverflow Questions [kotlin]") {
                        gridpaneConstraints {
                            marginBottom = 10.0
                        }
                    }
                    tableview(controller.questions) {
                        minWidth = 800.0
                        column("Title", Question::title).remainingWidth()
                        column("Score", Question::score)
                        column("Tags", Question::tags).cellFormat {
                            text = it.joinToString(", ")
                        }
                        column("Owner", Question::owner).cellFormat { (id, name) ->
                            text = "$name ($id)"
                        }
                        column("Answered", Question::answered).cellFormat {
                            text = if (it) "yes" else "no"
                            style {
                                alignment = Pos.CENTER
                                tableRow?.style {
                                    backgroundColor += if (!it) {
                                        c("rgb(255, 255, 224)")
                                    } else {
                                        c("rgb(239, 240, 241)")
                                    }
                                }
                            }
                        }
                        column("Creation", Question::created).cellFormat {
                            text = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
                                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        }
                        columnResizePolicy = SmartResize.POLICY
                        requestResize()
                        onUserSelect(2) { hostServices.showDocument(it.link) }
                        subscribe<LastQuestionUpdated> {
                            infoNotification(
                                title= "Notification", text="New Question: ${it.q.title}",
                                position = Pos.BOTTOM_RIGHT,
                                hideAfter = Duration.seconds(5.0)
                            )
                        }
                    }
                }
            }
        }
    }
}
