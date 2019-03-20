package com.utc

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.testkit.TestKit
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class SimpleReceiveActorKTTest {

    companion object {
        lateinit var system: ActorSystem
        lateinit var actorRef: ActorRef

        @BeforeClass
        @JvmStatic
        fun setup() {
            system = ActorSystem.create()
            actorRef = TestActorRef.create<SimpleReceiveActorKT>(system, SimpleReceiveActorKT.props())
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            TestKit.shutdownActorSystem(system, Duration.create(10, TimeUnit.MINUTES), false)
        }
    }


    @Test
    fun TestDefaultBehavior() {
        actorRef.tell(SimpleReceiveActorKT.Companion.SimpleMessage("A simple message..."), TestActorRef.noSender())
        actorRef.tell("A plain string", TestActorRef.noSender())
        assert(true)
    }


    @Test
    fun TestBahaviorChange() {
        actorRef.tell(SimpleReceiveActorKT.Companion.ChangeBehavior, TestActorRef.noSender())
        actorRef.tell(SimpleReceiveActorKT.Companion.SimpleMessage("Simple message after behavior change"), TestActorRef.noSender())
        actorRef.tell("A plain string", TestActorRef.noSender())
        actorRef.tell(SimpleReceiveActorKT.Companion.ChangeBehavior, TestActorRef.noSender())
        assert(true)
    }

}