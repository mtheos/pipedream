package me.theos.pipedream

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ReductionPipeTest {
  companion object {
    private lateinit var fn: (String, String) -> String
    private lateinit var pipe: ReductionPipe<String>
  }

  @BeforeEach
  fun setUp() {
    fn = { acc, it -> "$acc $it" }
    pipe = ReductionPipe(fn)
  }

  @Test
  fun testReducesElements() {
    val elements = listOf("One", "Two", "Three", "Four")
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
