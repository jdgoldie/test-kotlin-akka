package com.utc

import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.TestActorRef
import akka.testkit.TestKit
import org.junit.After
import org.junit.Before
import org.junit.Test
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit

class TrivialWithStateTest {

    lateinit var system: ActorSystem

    @Before
    fun setup() {
        system = ActorSystem.create()
    }

    @After
    fun teardown() {
        TestKit.shutdownActorSystem(system, Duration.create(10, TimeUnit.MINUTES), false)
    }

    fun delaySeconds(s: Long): FiniteDuration {
        return Duration.create(s, TimeUnit.SECONDS)
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
                val total = expectMsgClass(Duration.create(10, TimeUnit.SECONDS), TrivialActorProtocol.Total::class.java)
                assert(total.i == 30)
                trivialActor.tell(TrivialActorProtocol.Reset, testActor())
                trivialActor.tell(TrivialActorProtocol.GetTotal, testActor())
                val resetTotal = expectMsgClass(Duration.create(10, TimeUnit.SECONDS), TrivialActorProtocol.Total::class.java)
                assert(resetTotal.i == 0)
            }
        }
    }

}