package me.theos.pipedream

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SinkConsumerTest {
  companion object {
    private lateinit var sinkList: MutableList<String>
  }

  @BeforeEach
  fun setUp() {
    sinkList = mutableListOf()
  }

  @Test
  fun testSinkCallsConsumer() {
    val produced = setOf("One", "Two", "Three")
    val sink = SinkConsumer { it: String -> (sinkList).add(it) }
    produced.forEach { sink.accept(it, false) }
    assertEquals(produced.size, sinkList.size)
    produced.forEach { assertTrue(sinkList.contains(it))}
  }
}
