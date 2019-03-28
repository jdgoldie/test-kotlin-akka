package com.utc

import akka.actor.AbstractLoggingActor

abstract class AbstractLoggingActorKT : AbstractLoggingActor() {

    open lateinit var onReceive: (msg:Any) -> Unit

    //Hides the receiveBuilder() stuff so the kotlin code can just use kotlin matching and looks nicer
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