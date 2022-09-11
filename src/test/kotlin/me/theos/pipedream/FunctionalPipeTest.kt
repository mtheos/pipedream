package me.theos.pipedream

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class FunctionalPipeTest {
  companion object {
    private lateinit var fn: (String) -> Int
    private lateinit var pipe: FunctionalPipe<String, Int>
  }

  @BeforeEach
  fun setUp() {
    fn = String::length
    pipe = FunctionalPipe(fn)
  }

  @Test
  fun testAppliesFn() {
    val elem = "One"
    val sinkValue = pipe.makePipe().sinkValue()
    pipe.accept(elem, false)
    assertEquals(fn(elem), sinkValue.get())
  }
}
