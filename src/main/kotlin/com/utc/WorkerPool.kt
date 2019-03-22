package com.utc

import akka.actor.Props
import akka.event.LoggingAdapter
import akka.routing.RoundRobinRoutingLogic
import akka.routing.Router
import akka.routing.ActorRefRoutee
import akka.actor.Terminated



class Follower: AbstractLoggingActorKT() {

    val logger: LoggingAdapter = log()

    companion object {
        data class DoWork(val s: String, val d: Long)

        //Seeing this in some examples -- need to research why but for now just stealing it
        @JvmStatic
        fun props() : Props {
            return Props.create(com.utc.Follower::class.java)
        }
    }

    val workerReceive: (msg:Any) -> Unit = { msg:Any ->

        when (msg) {
            is DoWork -> {
                //Just some fake blocking work
                logger.info("Follower ${self.path()} got work $msg")
                Thread.sleep(msg.d)
                context.sender.tell(msg.s.reversed(), context.self)
            }
            else -> logger.warning("${self.path()} got invalid message: $msg")
        }

    }

    override var onReceive = workerReceive

}


class Leader: AbstractLoggingActorKT() {

    val logger: LoggingAdapter = log()

    var router = {
        var routees = (0..4).map {
            val r = context.actorOf(Follower.props())
            context.watch(r)
            ActorRefRoutee(r)
        }
        Router(RoundRobinRoutingLogic(), routees)
    }.invoke() //better way to do this than invoke()

    companion object {

        //Seeing this in some examples -- need to research why but for now just stealing it
        @JvmStatic
        fun props(): Props {
            return Props.create(com.utc.Leader::class.java)
        }
    }


    val leaederReceive: (msg:Any) -> Unit = { msg:Any ->

        when (msg) {
            is Follower.Companion.DoWork -> {
                logger.info("Leader ${self.path()} forwarding $msg to Follower.")
                this.router.route(msg, context.sender())
            }
            is Terminated -> {
                //In a real system, do something to fix the terminated worker
                //If the task-processing actors are persistent, we would just restart the task
                //and have it recover from where it stopped
                //Possibly consider failure is unrecoverable so need backoff/quit strategy?
                logger.warning("${context.sender.path()} terminated.")
            }
            else -> logger.warning("${self.path()} got invalid message: $msg")
        }

    }

    override var onReceive = leaederReceive

}



