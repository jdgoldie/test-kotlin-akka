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
        data class ReportWork(val s: String)

        //Seeing this in some examples -- need to research why but for now just stealing it
        @JvmStatic
        fun props() : Props {
            return Props.create(com.utc.Follower::class.java)
        }
    }

    val defaultReceive: (msg:Any) -> Unit = { msg:Any ->

        when (msg) {
            is DoWork -> {
                //Just some fake blocking work
                Thread.sleep(msg.d)
                context.sender.tell(msg.s.reversed(), context.self)
            }
            else -> logger.warning("${self.path()} got invalid message: $msg")
        }

    }

    override var onReceive = defaultReceive

}


class Leader: AbstractLoggingActorKT() {

    val logger: LoggingAdapter = log()

    var router = {
        var routees = (0..5).map {
            val r = context.actorOf(Follower.props())
            context.watch(r)
            ActorRefRoutee(r)
        }
        Router(RoundRobinRoutingLogic(), routees)
    }.invoke()

    companion object {

        //Seeing this in some examples -- need to research why but for now just stealing it
        @JvmStatic
        fun props(): Props {
            return Props.create(com.utc.Follower::class.java)
        }
    }


    val defaultReceive: (msg:Any) -> Unit = { msg:Any ->

        when (msg) {
            is Follower.Companion.DoWork -> {
                this.router.route(msg, context.sender)
            }
            is Terminated -> {
                //Do something to fix the terminated worker???
                logger.warning("${context.sender.path()} terminated.")
            }
            else -> logger.warning("${self.path()} got invalid message: $msg")
        }

    }

    override var onReceive = defaultReceive

}



