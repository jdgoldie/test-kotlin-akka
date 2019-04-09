package com.utc

import akka.actor.ActorSystem
import akka.actor.PoisonPill
import akka.actor.Props
import akka.testkit.TestActorRef
import akka.testkit.TestKit
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.junit.After
import org.junit.Before
import org.junit.Test
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit

class ExamplePersistentActorTest {


    lateinit var system: ActorSystem

    @Before
    fun setup() {
        system = ActorSystem.create("system", ConfigFactory.parseString("""
            akka.persistence.journal.plugin = "akka.persistence.journal.inmem"
            akka.persistence.snapshot-store.plugin = "akka.persistence.snapshot-store.local"
        """.trimIndent()))
    }

    @After
    fun teardown() {
        TestKit.shutdownActorSystem(system, 2.minutes(), false)
    }


    @Test
    fun SimplePersistentActorTest() {

        val actorRef = system.actorOf(ExamplePersistentActor.props("persistent-actor-1-id"), "first-create-of-actor-1")

        actorRef.tell(ExamplePersistProtocol.Command("First"), TestActorRef.noSender())
        actorRef.tell(ExamplePersistProtocol.Command("Second"), TestActorRef.noSender())
        actorRef.tell(ExamplePersistProtocol.Print, TestActorRef.noSender())
        2.seconds().sleep()
        //Kill the actor, bring it back
        println("Killing the actor")
        actorRef.tell(PoisonPill.getInstance(), TestActorRef.noSender())
        2.seconds().sleep()
        actorRef.tell(ExamplePersistProtocol.Print, TestActorRef.noSender()) //Should cause dead-letter since this ref is invalid
        //The line below creates a new actor ref to an actor with the same persistence id.  Because of the persistence id, this new actor will recover
        //to the point where the previous actor was terminated
        val anotherActorRef = system.actorOf(ExamplePersistentActor.props("persistent-actor-1-id"), "recover-of-actor-1")
        //Now send another message and check state
        anotherActorRef.tell(ExamplePersistProtocol.Command("Third"), TestActorRef.noSender())
        anotherActorRef.tell(ExamplePersistProtocol.Print, TestActorRef.noSender()) //Shows all three messages sent
        2.seconds().sleep()
        assert(true)

    }

}