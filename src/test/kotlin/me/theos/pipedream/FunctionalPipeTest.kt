package me.theos.pipedream

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class FunctionalPipeTest {
  companion object {
    private val fn = String::length
  }

  @BeforeEach
  fun setUp() {
  }

  @Test
  fun testAppliesFn() {
    val elem = "One"
    val pipe = FunctionalPipe(fn)
    val sinkValue = pipe.makePipe().sinkValue()
    pipe.accept(elem, false)
    assertEquals(fn(elem), sinkValue.get())
  }
}
