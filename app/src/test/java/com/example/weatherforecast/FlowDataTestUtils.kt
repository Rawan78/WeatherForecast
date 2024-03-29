package com.example.weatherforecast

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> Flow<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val collector = object : FlowCollector<T> {
        override suspend fun emit(value: T) {
            data = value
            latch.countDown()
        }
    }
    this.onEach { collector.emit(it) }
        .launchIn(GlobalScope)

    try {
        afterObserve.invoke()

        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("StateFlow value was never set.")
        }

    } finally {
        // Cancel the flow to prevent leaks
        this.onEach { }.launchIn(GlobalScope)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}