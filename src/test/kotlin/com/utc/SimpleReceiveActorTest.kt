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

class SimpleReceiveActorTest {

    companion object {
        lateinit var system: ActorSystem
        lateinit var actorRef: ActorRef

        @BeforeClass
        @JvmStatic
        fun setup() {
            system = ActorSystem.create()
            actorRef = TestActorRef.create<SimpleReceiveActor>(system, SimpleReceiveActor.props())
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            TestKit.shutdownActorSystem(system, Duration.create(10, TimeUnit.MINUTES), false)
        }
    }


    //@Test
    fun SendKnownMessage() {
        actorRef.tell(SimpleReceiveActorMessages.StringMessage("Test"), TestActorRef.noSender())
        assert(true)
    }

    //@Test
    fun SendUnknownMessage() {
        actorRef.tell("This is a bad message", TestActorRef.noSender())
        assert(true)
    }

}