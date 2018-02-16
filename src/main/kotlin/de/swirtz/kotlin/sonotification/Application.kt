package de.swirtz.kotlin.sonotification

import com.google.inject.Guice
import com.google.inject.Module
import de.swirtz.kotlin.sonotification.seadapter.APIAdapter
import de.swirtz.kotlin.sonotification.seadapter.MockAPIAdapter
import de.swirtz.kotlin.sonotification.seadapter.StackExchangeAPIAdapter
import de.swirtz.kotlin.sonotification.view.QuestionsView
import javafx.scene.text.FontWeight
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import kotlin.reflect.KClass

class SONotificationApp : App(QuestionsView::class, Styles::class) {

    init {
        Guice.createInjector(Module {
            it.bind(APIAdapter::class.java).to(StackExchangeAPIAdapter::class.java)
        }).let { guice ->
            FX.dicontainer = object : DIContainer {
                override fun <T : Any> getInstance(type: KClass<T>): T = guice.getInstance(type.java)
            }
        }
    }
}

fun main(args: Array<String>) {
    launch<SONotificationApp>(args)
}

class Styles : Stylesheet() {
    init {
        label {
            fontSize = 14.px
            fontWeight = FontWeight.BOLD
        }


    }
}

inline fun <reified T> getLogger(): Logger = LoggerFactory.getLogger(T::class.java)

const val DEFAULT_POLL_INTERVAL = 15_000