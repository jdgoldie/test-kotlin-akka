package com.utc

import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.TestActorRef
import akka.testkit.TestKit
import org.junit.After
import org.junit.Before
import org.junit.Test

class TrivialWithStateTest {

    lateinit var system: ActorSystem

    @Before
    fun setup() {
        system = ActorSystem.create()
    }

    @After
    fun teardown() {
        TestKit.shutdownActorSystem(system, 3.minutes(), false)
    }


    @Test
    fun TestTrivialWithState() {
        object : TestKit(system) {
            init {

                val trivialActor = TestActorRef.create<TrivialWithState>(system,
                        Props.create(com.utc.TrivialWithState::class.java),  "trivial")
                trivialActor.tell(TrivialActorProtocol.AddValue(10), testActor())
                trivialActor.tell(TrivialActorProtocol.AddValue(20), testActor())
                trivialActor.tell(TrivialActorProtocol.GetTotal, testActor())
                val total = expectMsgClass(10.seconds(), TrivialActorProtocol.Total::class.java)
                assert(total.i == 30)
                trivialActor.tell(TrivialActorProtocol.Reset, testActor())
                trivialActor.tell(TrivialActorProtocol.GetTotal, testActor())
                val resetTotal = expectMsgClass(10.seconds(), TrivialActorProtocol.Total::class.java)
                assert(resetTotal.i == 0)
            }
        }
    }

}