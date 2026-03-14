package me.theos.pipedream

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class SinkTest {
  @Test
  fun testSinkCollection() {
    val collection = mutableListOf<String>()
    val sink = SinkCollection(collection)
    
    sink.accept("a")
    sink.accept("b")
    sink.complete()
    
    assertThat(collection).containsExactly("a", "b")
  }

  @Test
  fun testSinkValueSingle() {
    val sink = SinkValue<String>()
    
    sink.accept("value")
    
    assertThat(sink.get()).isEqualTo("value")
  }

  @Test
  fun testSinkValueMultipleThrows() {
    val sink = SinkValue<String>()
    sink.accept("first")
    
    assertThatThrownBy { sink.accept("second") }
      .isInstanceOf(IllegalStateException::class.java)
      .hasMessage("SinkValue can only hold 1 value")
  }

  @Test
  fun testSinkValueEmptyThrows() {
    val sink = SinkValue<String>()
    
    assertThatThrownBy { sink.get() }
      .isInstanceOf(NullPointerException::class.java)
  }

  @Test
  fun testSinkConsumer() {
    var lastValue: String? = null
    val sink = SinkConsumer<String> { lastValue = it }
    
    sink.accept("a")
    sink.accept("b")
    sink.complete()
    
    assertThat(lastValue).isEqualTo("b")
  }

  @Test
  fun testSinkReduction() {
    val sink = SinkReduction { acc: Int, i: Int -> acc + i }
    
    sink.accept(1)
    sink.accept(2)
    sink.accept(3)
    sink.complete()
    
    assertThat(sink.get()).isEqualTo(6)
  }

  @Test
  fun testSinkReductionSingle() {
    val sink = SinkReduction { acc: String, s: String -> acc + s }
    
    sink.accept("a")
    
    assertThat(sink.get()).isEqualTo("a")
  }

  @Test
  fun testSinkReductionEmptyThrows() {
    val sink = SinkReduction { acc: Int, i: Int -> acc + i }
    
    assertThatThrownBy { sink.get() }
      .isInstanceOf(IllegalStateException::class.java)
      .hasMessage("No elements to reduce")
  }

  @Test
  fun testSinkCollectionToList() {
    val collection = mutableListOf("initial")
    val sink = SinkCollection(collection)
    
    sink.accept("a")
    sink.accept("b")
    
    assertThat(sink.toList()).containsExactly("initial", "a", "b")
  }
}
