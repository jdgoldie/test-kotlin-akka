package com.utc

import akka.actor.Props
import akka.event.LoggingAdapter

class SimpleReceiveActorKT: AbstractLoggingActorKT() {

    private val logger : LoggingAdapter = log()


    companion object {
        data class SimpleMessage(val msg: String)
        object ChangeBehavior

        @JvmStatic
        fun props() : Props {
            return Props.create(com.utc.SimpleReceiveActorKT::class.java)
        }
    }

    val defaultReceive: (msg:Any) -> Unit = { msg:Any ->

        when (msg) {
            is SimpleMessage -> {
                logger.info("[DEFAULT] Got message: ${msg.msg}")
            }
            is ChangeBehavior -> {
                logger.info("Switching to new behavior")
                become(newBehavior)
            }
            else -> logger.info("Received unknown message of $msg")
        }

    }

    val newBehavior: (msg:Any) -> Unit = { msg: Any ->

        when (msg) {
            is SimpleMessage -> {
                logger.info("[NEW] Got message: ${msg.msg}")
            }
            is String -> {
                logger.info("[NEW] Got a plain string $msg")
            }
            is ChangeBehavior -> {
                logger.info("Switching to default behavior")
                become(defaultReceive)
            }
            else -> logger.info("Received unknown message of $msg")
        }

    }

    //Uses kotlin matching
    override var onReceive = defaultReceive


}