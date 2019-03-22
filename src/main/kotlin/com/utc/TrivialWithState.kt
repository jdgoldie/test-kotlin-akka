package com.utc

import akka.event.LoggingAdapter


object TrivialActorProtocol {
    data class AddValue(val i: Int)
    data class Total(val i: Int)
    object Reset
    object GetTotal
}

class TrivialWithState : AbstractLoggingActorKT() {

    private val logger : LoggingAdapter = log()

    fun receiveWithState(total: Int): (msg: Any) -> Unit {

        return { msg:Any ->

            val currentTotal:Int = total

            when (msg) {
                is TrivialActorProtocol.AddValue -> {
                    logger.info("Adding value ${msg.i}")
                    become(receiveWithState(currentTotal + msg.i))
                }
                is TrivialActorProtocol.GetTotal -> {
                    logger.info("Returning total $currentTotal")
                    context.sender.tell(TrivialActorProtocol.Total(currentTotal), context.self)
                }
                is TrivialActorProtocol.Reset -> {
                    logger.info("Resetting accumulator")
                    become(receiveWithState(0))
                }
            }

        }

    }

    //Initialize with 0
    override var onReceive = receiveWithState(0)


}