package me.theos.pipedream

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class FoldingPipeTest {
  companion object {
    private lateinit var fn: (Int, String) -> Int
    private lateinit var pipe: FoldingPipe<String, Int>
  }

  @BeforeEach
  fun setUp() {
    fn = { acc, it -> acc + it.length }
    pipe = FoldingPipe(0, fn)
  }

  @Test
  fun testFoldsElements() {
    val elements = listOf("One", "Two", "Three", "Four")
    val sinkValue = pipe.makePipe().sinkValue()
    elements.iterator().let {
      while (it.hasNext()) {
        pipe.accept(it.next(), !it.hasNext())
      }
    }
    assertEquals(elements.fold(0, fn), sinkValue.get())
  }
}
