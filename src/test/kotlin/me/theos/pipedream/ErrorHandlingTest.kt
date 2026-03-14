package me.theos.pipedream

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class ErrorHandlingTest {
  @Test
  fun testResultWithoutSink() {
    val pipeline = Pipeline.from(listOf("a", "b"))
    
    assertThatThrownBy { pipeline.result() }
      .isInstanceOf(NullPointerException::class.java)
  }

  @Test
  fun testPipeWithoutSink() {
    val pipeline = Pipeline.from(listOf("a", "b"))
    
    assertThatThrownBy { pipeline.pipe() }
      .isInstanceOf(IllegalStateException::class.java)
      .hasMessage("Pipe doesn't connect to anything")
  }

  @Test
  fun testReduceOnEmptySource() {
    val pipeline = Pipeline.from(emptyList<String>())
      .reduce { acc, s -> acc + s }
    
    pipeline.pipe()
    
    assertThatThrownBy { pipeline.reduced() }
      .isInstanceOf(IllegalStateException::class.java)
      .hasMessage("No elements to reduce")
  }

  @Test
  fun testSinkValueMultipleValues() {
    val sink = SinkValue<String>()
    sink.accept("first")
    
    assertThatThrownBy { sink.accept("second") }
      .isInstanceOf(IllegalStateException::class.java)
      .hasMessage("SinkValue can only hold 1 value")
  }

  @Test
  fun testFunctionalPipeWithoutSink() {
    val pipe = FunctionalPipe<String, Int> { it.length }
    
    assertThatThrownBy { pipe.accept("test") }
      .isInstanceOf(NullPointerException::class.java)
  }

  @Test
  fun testFilterPipeWithoutSink() {
    val pipe = FilterPipe<String> { it.length > 1 }
    pipe.accept("test")
  }

  @Test
  fun testFoldingPipeWithoutSink() {
    val pipe = FoldingPipe<String, Int>(0) { acc, s -> acc + s.length }
    
    assertThatThrownBy { pipe.accept("test") }
      .isInstanceOf(NullPointerException::class.java)
  }

  @Test
  fun testReductionPipeWithoutSink() {
    val pipe = ReductionPipe<String> { acc, s -> acc + s }
    pipe.accept("test")
  }
}
