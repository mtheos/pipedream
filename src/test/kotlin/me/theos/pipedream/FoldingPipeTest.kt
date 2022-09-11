package me.theos.pipedream

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class FoldingPipeTest {
  companion object {
    private val fn = { acc: Int, it: String -> acc + it.length }
  }

  @BeforeEach
  fun setUp() {
  }

  @Test
  fun testFoldsElements() {
    val elements = listOf("One", "Two", "Three", "Four")
    val pipe = FoldingPipe(0, fn)
    val sinkValue = pipe.makePipe().sinkValue()
    elements.iterator().let {
      while (it.hasNext()) {
        pipe.accept(it.next(), !it.hasNext())
      }
    }
    assertEquals(elements.fold(0, fn), sinkValue.get())
  }
}
