package me.theos.pipedream

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class PipeTest {
  companion object {
    private lateinit var pipe: Pipe<String>
  }

  @BeforeEach
  fun setUp() {
    pipe = Pipe()
  }

  @Test
  fun testInto() {
    val elements = listOf("one", "two", "three", "four")
    val pipe2 = Pipe<String>().filter { it == "one" }
    val sink = pipe.into(pipe2).sink()
    elements.source().pipe(pipe)
    assertTrue(sink.result().size == 1)
    assertEquals(sink.result().first(), elements.first())
  }

  @Test
  fun testIntoTransform() {
    val elements = listOf("one", "two", "three", "four")
    val pipe2 = object : TransformPipe<String, Int>() {
      override fun transform(elem: String, last: Boolean): Int = elem.length
    }
    val sink = pipe.into(pipe2).sink()
    elements.source().pipe(pipe)
    assertEquals(sink.result(), elements.map { it.length })
  }

  @Test
  fun testMap() {
    val elements = listOf("one", "two", "three", "four")
    val sink = pipe.map { it.length } .sink()
    elements.source().pipe(pipe)
    assertEquals(sink.result(), elements.map { it.length })
  }

  @Test
  fun testBiMap() {
    val elements = listOf("one", "two", "three", "four")
    val sink = pipe.biMap { it, last -> if (last) 0 else it.length }.sink()
    elements.source().pipe(pipe)
    assertEquals(sink.result(), elements.map { if (it == "four") 0 else it.length })
  }

  @Test
  fun testFilter() {
    val elements = listOf("one", "two", "three", "four")
    val sink = pipe.filter { it.length % 2 == 1 }.sink()
    elements.source().pipe(pipe)
    assertFalse(sink.result().contains("four"))
    sink.result().forEach { assertTrue(elements.contains(it)) }
  }

  @Test
  fun testReduce() {
    val elements = listOf("one", "two", "three", "four")
    val sink = pipe.reduce { a, b -> "$a$b" }.sink()
    elements.source().pipe(pipe)
    assertEquals(sink.result().first(), elements.reduce { acc, s -> acc + s })
  }

  @Test
  fun testReduceFold() {
    val elements = listOf("one", "two", "three", "four")
    val sink = pipe.reduce(0) { a, b -> a + b.length }.sink()
    elements.source().pipe(pipe)
    assertEquals(sink.result().first(), elements.fold(0) { acc, s -> acc + s.length })
  }

  @Test
  fun testFold() {
    val elements = listOf("one", "two", "three", "four")
    val sink = pipe.fold(0) { a, b -> a + b.length }.sink()
    elements.source().pipe(pipe)
    assertEquals(sink.result().first(), elements.fold(0) { acc, s -> acc + s.length })
  }

  @Test
  fun testPrimarySinkCollection() {
    val elements = listOf("one", "two", "three", "four")
    val sink = pipe.sink()
    elements.source().pipe(pipe)
    assertEquals(sink.result(), elements)
  }

  @Test
  fun testListSinkCollection() {
    val elements = listOf("one", "two", "three", "four")
    val lis = mutableListOf<String>()
    pipe.sink(lis)
    elements.source().pipe(pipe)
    assertEquals(elements, lis)
  }

  @Test
  fun testSinkConsumer() {
    val elements = listOf("one", "two", "three", "four")
    var value = elements[0]
    pipe.sink { value = it }
    elements.forEach { pipe.accept(it, false).run { assertEquals(it, value) } }
  }

  @Test
  fun testSinkableConsumer() {
    val elements = listOf("one", "two", "three", "four")
    var value = elements[0]
    pipe.sink { s, _ -> value = s }
    elements.forEach { pipe.accept(it, false).run { assertEquals(it, value) } }
  }

  @Test
  fun testRequiresSink() {
    assertThatThrownBy { pipe.accept("Throw", false) }.isExactlyInstanceOf(IllegalStateException::class.java)
  }
}
