package com.utc

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.testkit.TestKit
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

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
            TestKit.shutdownActorSystem(system, 2.minutes(), false)
        }
    }


    @Test
    fun TestInteraction() {

        

        object : TestKit(system) {
            init {
                val actorRef = TestActorRef.create<SimpleReceiveActorKT>(system, SimpleReceiveActorKT.props(), "simple")
                actorRef.tell(SimpleReceiveActorMessages.StringMessage("abcde"), testActor())
                expectMsg(5.seconds(), "Invalid Message")
                actorRef.tell(SimpleReceiveActorKT.Companion.SimpleMessage("abcde"), testActor())
                expectMsg(5.seconds(), "edcba")
                actorRef.tell(SimpleReceiveActorKT.Companion.ChangeBehavior, testActor())
                expectMsg(5.seconds(), "Behavior Changed")
                actorRef.tell(SimpleReceiveActorKT.Companion.SimpleMessage("abcde"), testActor())
                expectMsg(5.seconds(), "EDCBA")
                actorRef.tell("string", testActor())
                expectMsg(5.seconds(), "string")
                actorRef.tell(SimpleReceiveActorKT.Companion.ChangeBehavior, testActor())
                expectMsg(5.seconds(), "Behavior Changed")
                actorRef.tell("string", testActor())
                expectMsg(5.seconds(), "Invalid Message")
            }
        }
    }



}