package me.theos.pipedream

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class PipeTest {
  @Test
  fun testPipeWithoutSink() {
    val pipe = Pipe<String>()
    
    assertThatThrownBy { pipe.accept("test") }
      .isInstanceOf(IllegalStateException::class.java)
      .hasMessage("Pipe doesn't connect to anything")
  }

  @Test
  fun testPipeWithSink() {
    val result = mutableListOf<String>()
    val pipe = Pipe<String>()
    pipe.sink(result)
    
    pipe.accept("a")
    pipe.accept("b")
    pipe.complete()
    
    assertThat(result).containsExactly("a", "b")
  }

  @Test
  fun testPipeChaining() {
    val result = mutableListOf<Int>()
    val pipe = Pipe<String>()
    pipe.map { it.length }.sink(result)
    
    pipe.accept("a")
    pipe.accept("bb")
    pipe.complete()
    
    assertThat(result).containsExactly(1, 2)
  }

  @Test
  fun testPipeFilter() {
    val result = mutableListOf<String>()
    val pipe = Pipe<String>()
    pipe.filter { it.length > 1 }.sink(result)
    
    pipe.accept("a")
    pipe.accept("bb")
    pipe.accept("ccc")
    pipe.complete()
    
    assertThat(result).containsExactly("bb", "ccc")
  }

  @Test
  fun testPipeFold() {
    val result = mutableListOf<Int>()
    val pipe = Pipe<String>()
    pipe.fold(0) { acc, s -> acc + s.length }.sink(result)
    
    pipe.accept("a")
    pipe.accept("bb")
    pipe.complete()
    
    assertThat(result).containsExactly(3)
  }

  @Test
  fun testPipeMultipleSinks() {
    val result1 = mutableListOf<String>()
    val result2 = mutableListOf<Int>()
    val pipe = Pipe<String>()
    pipe.sink(result1)
    pipe.map { it.length }.sink(result2)
    
    pipe.accept("a")
    pipe.accept("bb")
    pipe.complete()
    
    assertThat(result1).containsExactly("a", "bb")
    assertThat(result2).containsExactly(1, 2)
  }

  @Test
  fun testPipeResultWithoutSink() {
    val pipe = Pipe<String>()
    
    assertThatThrownBy { pipe.result() }
      .isInstanceOf(NullPointerException::class.java)
  }

  @Test
  fun testPipeToSinkable() {
    val sink = SinkCollection(mutableListOf<String>())
    val pipe = Pipe<String>()
    pipe.sink(sink)
    
    pipe.accept("test")
    pipe.complete()
    
    assertThat(sink.toList()).containsExactly("test")
  }

  @Test
  fun testPipeComplete() {
    var completeCalled = false
    val result = mutableListOf<String>()
    val pipe = Pipe<String>()
    pipe.sink(result)
    pipe.sink(object : Sinkable<String> {
      override fun accept(elem: String) {}
      override fun complete() { completeCalled = true }
    })
    
    pipe.accept("a")
    pipe.complete()
    
    assertThat(completeCalled).isTrue()
    assertThat(result).containsExactly("a")
  }
}
