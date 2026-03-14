package me.theos.pipedream

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.stream.Stream

internal class SourceTest {
  @Test
  fun testFromStream() {
    val result = mutableListOf<String>()
    val sink = SinkCollection(result)
    Source(Stream.of("one", "two", "three").iterator()).pipe(sink)
    
    assertThat(result).containsExactly("one", "two", "three")
  }

  @Test
  fun testFromIterator() {
    val result = mutableListOf<String>()
    val sink = SinkCollection(result)
    val iterator = listOf("a", "b", "c").iterator()
    Source(iterator).pipe(sink)
    
    assertThat(result).containsExactly("a", "b", "c")
  }

  @Test
  fun testFromEmptyIterator() {
    val result = mutableListOf<String>()
    val sink = SinkCollection(result)
    val iterator = emptyList<String>().iterator()
    Source(iterator).pipe(sink)
    
    assertThat(result).isEmpty()
  }

  @Test
  fun testConsumedError() {
    val sink = SinkCollection(mutableListOf<String>())
    val source = Source(listOf("a", "b", "c").iterator())
    source.pipe(sink)
    
    assertThatThrownBy { source.pipe(sink) }
      .isInstanceOf(IllegalStateException::class.java)
      .hasMessage("Source has already been consumed")
  }

  @Test
  fun testCompleteCalled() {
    var completeCalled = false
    val sink = object : Sinkable<String> {
      override fun accept(elem: String) {}
      override fun complete() { completeCalled = true }
    }
    
    Source(listOf("a", "b").iterator()).pipe(sink)
    
    assertThat(completeCalled).isTrue()
  }
}
