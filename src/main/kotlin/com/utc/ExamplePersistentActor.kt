package com.utc

import akka.actor.Props
import akka.persistence.SnapshotOffer

//Translated from example in Akka docs to see how a simple persistent actor looks/works in in Kotlin
//Check against Scala code here: https://doc.akka.io/docs/akka/2.5.4/scala/persistence.html

object ExamplePersistProtocol {
    data class Event(val data: String)
    data class Command(val data: String)
    object Print

}

data class ExampleState(val state: List<String> = emptyList()) {
    fun updated(event: ExamplePersistProtocol.Event): ExampleState { return ExampleState(listOf(event.data) + state) }
    fun size() : Int { return state.size }
    override fun toString(): String { return state.reversed().joinToString() }
}

class ExamplePersistentActor(private val thePersistenceId: String): AbstractPersistentLoggingActorKT() {

    companion object {
        @JvmStatic
        fun props(thePersistenceId: String) : Props {
            return Props.create(com.utc.ExamplePersistentActor::class.java, { com.utc.ExamplePersistentActor(thePersistenceId) } )
        }
    }

    val logger = getLog(this)

    override fun persistenceId(): String { return thePersistenceId }

    var state = ExampleState()

    fun updateState(evt: ExamplePersistProtocol.Event) { state = state.updated(evt) }

    fun numEvents(): Int { return state.size() }

    override var onReceiveRecover = { msg:Any ->
        when (msg) {
            is ExamplePersistProtocol.Event -> { updateState(msg) }
            is SnapshotOffer -> { state = msg.snapshot() as ExampleState }
        }
    }

    override var onReceive = { msg:Any ->
        when (msg) {
            is ExamplePersistProtocol.Command -> {
                persist(ExamplePersistProtocol.Event("${msg.data} - ${numEvents() + 1}")) { evt->
                    updateState(evt)
                    context.system.eventStream.publish(evt)
                    if (lastSequenceNr() % 10 == 0L && lastSequenceNr() > 0) { saveSnapshot(state) }
                }
            }
            is ExamplePersistProtocol.Print -> {
                logger.info(state.toString())
            }
            else -> {
                logger.warning("Received invalid message $msg")
            }
        }
    }

}