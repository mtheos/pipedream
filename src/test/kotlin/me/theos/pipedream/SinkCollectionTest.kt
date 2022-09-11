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
    assertEquals(sink.get(), sinkList)
  }

  @Test
  fun testSinkReturnsIterator() {
    val produced = setOf("One", "Two", "Three")
    val sink = SinkCollection(sinkList)
    produced.forEach { sink.accept(it, false) }
    sink.iterator().let { it1 ->
      sinkList.iterator().let { it2 ->
        assertEquals(it1.hasNext(), it2.hasNext())
        assertEquals(it1.next(), it2.next())
      }
    }
  }
}
