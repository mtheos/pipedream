package me.theos.pipedream

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SinkConsumerTest {

  companion object {
    private var sinkList: List<String>? = null
    private var sink: SinkConsumer<String>? = null
  }

  @BeforeEach
  fun setUp() {
    sinkList = mutableListOf()
    sink = SinkConsumer { (sinkList as MutableList<String>).add(it) }
  }

  @Test
  fun testSinkCallsConsumer() {
    val produced = setOf("One", "Two", "Three")
    produced.forEach { sink!!.accept(it, false) }
    assertEquals(produced.size, sinkList!!.size)
    produced.forEach { assertTrue(sinkList!!.contains(it))}
  }
}
