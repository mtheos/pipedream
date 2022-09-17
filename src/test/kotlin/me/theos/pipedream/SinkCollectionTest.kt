package me.theos.pipedream

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SinkCollectionTest {
  companion object {
    private lateinit var sinkList: MutableList<String>
  }

  @BeforeEach
  fun setUp() {
    sinkList = mutableListOf()
  }

  @Test
  fun testSinkRemembersItems() {
    val produced = setOf("One", "Two", "Three")
    val sink = SinkCollection(sinkList)
    produced.forEach { sink.accept(it, false) }
    assertEquals(produced.size, sinkList.size)
    produced.forEach { assertTrue(sinkList.contains(it))}
  }

  @Test
  fun testSinkReturnsList() {
    val produced = setOf("One", "Two", "Three")
    val sink = SinkCollection(sinkList)
    produced.forEach { sink.accept(it, false) }
    assertEquals(sink.toList(), sinkList)
  }
}
