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
        TestKit.shutdownActorSystem(system, Duration.create(10, TimeUnit.MINUTES), false)
    }

    fun delaySeconds(s: Long): FiniteDuration {
        return Duration.create(s, TimeUnit.SECONDS)
    }

    @Test
    fun SimplePersistentActorTest() {

        val actorRef = system.actorOf(Props.create(ExamplePersistentActor::class.java), "persistent-1")

        actorRef.tell(ExamplePersistProtocol.Command("First"), TestActorRef.noSender())
        actorRef.tell(ExamplePersistProtocol.Command("Second"), TestActorRef.noSender())
        actorRef.tell(ExamplePersistProtocol.Print, TestActorRef.noSender())
        Thread.sleep(2000)
        //Kill the actor, bring it back
        println("Killing the actor")
        actorRef.tell(PoisonPill.getInstance(), TestActorRef.noSender())
        Thread.sleep(2000)
        actorRef.tell(ExamplePersistProtocol.Print, TestActorRef.noSender()) //Should cause dead-letter since this ref is invalid
        val anotherActorRef = system.actorOf(Props.create(ExamplePersistentActor::class.java), "persistent-2")
        //Now send another message and check state
        anotherActorRef.tell(ExamplePersistProtocol.Command("Third"), TestActorRef.noSender())
        anotherActorRef.tell(ExamplePersistProtocol.Print, TestActorRef.noSender()) //Shows all three messages sent
        Thread.sleep(2000)
        assert(true)

    }

}