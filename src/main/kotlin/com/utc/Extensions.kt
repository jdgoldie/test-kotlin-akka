package com.utc

import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit

fun Number.seconds(): FiniteDuration = Duration.create(this.toLong(), TimeUnit.SECONDS)
fun Number.minutes(): FiniteDuration = Duration.create(this.toLong(), TimeUnit.MINUTES)

fun FiniteDuration.sleep(): Unit  { Thread.sleep(this.toMillis()) }