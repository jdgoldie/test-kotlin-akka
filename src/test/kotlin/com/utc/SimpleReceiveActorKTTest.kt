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

        @BeforeClass
        @JvmStatic
        fun setup() {
            system = ActorSystem.create()
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            TestKit.shutdownActorSystem(system, Duration.create(10, TimeUnit.MINUTES), false)
        }
    }


    @Test
    fun TestInteraction() {

        val fiveSeconds = Duration.create(5, TimeUnit.SECONDS)

        object : TestKit(system) {
            init {
                val actorRef = TestActorRef.create<SimpleReceiveActorKT>(system, SimpleReceiveActorKT.props(), "simple")
                actorRef.tell(SimpleReceiveActorMessages.StringMessage("abcde"), testActor())
                expectMsg(fiveSeconds, "Invalid Message")
                actorRef.tell(SimpleReceiveActorKT.Companion.SimpleMessage("abcde"), testActor())
                expectMsg(fiveSeconds, "edcba")
                actorRef.tell(SimpleReceiveActorKT.Companion.ChangeBehavior, testActor())
                expectMsg(fiveSeconds, "Behavior Changed")
                actorRef.tell(SimpleReceiveActorKT.Companion.SimpleMessage("abcde"), testActor())
                expectMsg(fiveSeconds, "EDCBA")
                actorRef.tell("string", testActor())
                expectMsg(fiveSeconds, "string")
                actorRef.tell(SimpleReceiveActorKT.Companion.ChangeBehavior, testActor())
                expectMsg(fiveSeconds, "Behavior Changed")
                actorRef.tell("string", testActor())
                expectMsg(fiveSeconds, "Invalid Message")
            }
        }
    }



}