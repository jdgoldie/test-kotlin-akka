package com.utc

import akka.event.LogSource
import akka.persistence.AbstractPersistentActor
import akka.event.Logging
import akka.event.LoggingAdapter



abstract class AbstractPersistentLoggingActorKT : AbstractPersistentActor() {

    fun getLog(logSource: Any) = Logging.getLogger(context.system, logSource)

    open lateinit var onReceive: (msg:Any) -> Unit

    open lateinit var onReceiveRecover: (msg:Any) -> Unit

    override fun createReceiveRecover(): Receive {
        return receiveBuilder().matchAny { m -> onReceiveRecover(m) }.build()
    }

    override fun createReceive(): Receive {
        return receiveBuilder().matchAny { m -> onReceive(m) }.build()
    }

    fun become( newReceive: (msg:Any) -> Unit ): Unit {
        this.context.become(
            receiveBuilder().matchAny { m -> newReceive(m) }.build())
    }

    fun unbecome(): Unit {
        this.context.unbecome()
    }

}