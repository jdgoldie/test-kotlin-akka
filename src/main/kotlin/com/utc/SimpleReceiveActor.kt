package com.utc

import akka.actor.AbstractLoggingActor
import akka.actor.Props
import akka.event.Logging
import akka.event.LoggingAdapter

object SimpleReceiveActorMessages {

    //Message Classes
    data class StringMessage(val value: String)

}

class SimpleReceiveActor : AbstractLoggingActor() {

    private val logger: LoggingAdapter = log()

    companion object {
        @JvmStatic
        fun props() : Props {
            return Props.create(com.utc.SimpleReceiveActor::class.java)
        }
    }

    override fun createReceive(): Receive {
        return receiveBuilder().matchAny {

            message ->
                when (message) {
                    is SimpleReceiveActorMessages.StringMessage -> {
                        logger.info("Got message ${message.value}")
                    }
                    else -> logger.info("Received unknown message")
                }

        }.build()
    }

}


