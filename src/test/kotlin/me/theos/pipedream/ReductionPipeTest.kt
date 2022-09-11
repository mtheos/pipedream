package me.theos.pipedream

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ReductionPipeTest {
  companion object {
    private val fn = { acc: String, it: String -> "$acc $it" }
  }

  @BeforeEach
  fun setUp() {
  }

  @Test
  fun testReducesElements() {
    val elements = listOf("One", "Two", "Three", "Four")
    val pipe = ReductionPipe(fn)
    val sinkValue = pipe.sinkValue()
    elements.iterator().let {
      while (it.hasNext()) {
        pipe.accept(it.next(), !it.hasNext())
      }
    }
    elements.flatMap { it.split("") }
    assertEquals(elements.reduce(fn), sinkValue.get())
  }
}
