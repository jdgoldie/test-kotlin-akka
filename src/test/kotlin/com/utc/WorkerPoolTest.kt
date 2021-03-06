package com.utc

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.testkit.TestKit
import org.junit.After
import org.junit.Before
import org.junit.Test
import scala.collection.JavaConverters


class WorkerPoolTest {


    lateinit var system: ActorSystem

    @Before
    fun setup() {
        system = ActorSystem.create()
    }

    @After
    fun teardown() {
        TestKit.shutdownActorSystem(system, 2.minutes(), false)
    }


    @Test
    fun TestSingleWorkRequest() {

        object : TestKit(system) {
            init {

                val leaderRef = TestActorRef.create<Leader>(system, Leader.props(), "leader")
                leaderRef.tell(Follower.Companion.DoWork("test", 5000), testActor())
                expectMsg(10.seconds(), "tset")


            }
        }
    }

    @Test
    fun TestManyWorkRequests() {

        object : TestKit(system) {
            init {

                val leaderRef = TestActorRef.create<Leader>(system, Leader.props(), "leader")

                val testInput = listOf("second test", "able was i ere i saw elba", "the third", "i am number 4", "yep", "test value 6 should wait for next worker", "test value 7 should wait for next worker")
                val testResults = testInput.map { s -> s.reversed() }

                testInput.forEach { s -> leaderRef.tell(Follower.Companion.DoWork(s, 5000), testActor()) }

                //Technically could pass even if results are not 1:1, but this is just a demo
                testResults.forEach {
                    expectMsgAnyOf(10.seconds(), JavaConverters.asScalaBufferConverter(testResults).asScala())
                }

            }
        }


    }


}
