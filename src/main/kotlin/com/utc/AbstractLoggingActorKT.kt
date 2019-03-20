package com.utc

import akka.actor.AbstractLoggingActor

abstract class AbstractLoggingActorKT : AbstractLoggingActor() {

    open lateinit var onReceive: (msg:Any) -> Unit

    //Hides the receiveBuilder() stuff so the kotlin code can just use kotlin matching and looks nicer
    override fun createReceive(): Receive {
        return receiveBuilder().matchAny { m -> onReceive(m) }.build()
    }

    //Possibly not exact match for how it works in scala
    fun become( behavior: (msg:Any) -> Unit ) {
        onReceive = behavior
    }

}