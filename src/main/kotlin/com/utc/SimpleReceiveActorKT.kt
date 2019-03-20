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
                //logger.info("[DEFAULT] Got message: ${msg.msg}")
                context.sender.tell(msg.msg.reversed(), context.self)
            }
            is ChangeBehavior -> {
                //logger.info("Switching to new behavior")
                context.sender.tell("Behavior Changed", context.self)
                become(newBehavior)
            }
            else -> {
                //logger.info("Received unknown message of $msg")
                context.sender.tell("Invalid Message", context.self)
            }
        }

    }

    val newBehavior: (msg:Any) -> Unit = { msg: Any ->

        when (msg) {
            is SimpleMessage -> {
                //logger.info("[NEW] Got message: ${msg.msg}")
                context.sender.tell(msg.msg.toUpperCase().reversed(), context.self)
            }
            is String -> {
                //logger.info("[NEW] Got a plain string $msg")
                context.sender.tell(msg, context.self)
            }
            is ChangeBehavior -> {
                //logger.info("Switching to default behavior")
                context.sender.tell("Behavior Changed", context.self)
                become(defaultReceive)
            }
            else -> {
                //logger.info("Received unknown message of $msg")
                context.sender.tell("Invalid Message", context.self)
            }
        }

    }

    //Uses kotlin matching
    override var onReceive = defaultReceive


}