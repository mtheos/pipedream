package me.theos.pipedream

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class BiFunctionalPipeTest {
  companion object {
    private lateinit var biFn: (String, Boolean) -> Int
    private lateinit var biPipe: BiFunctionalPipe<String, Int>
  }

  @BeforeEach
  fun setUp() {
    biFn = { it, last -> if (!last) it.length else 0 }
    biPipe = BiFunctionalPipe(biFn)
  }

  @Test
  fun testAppliesBiFn() {
    val elem = "One"
    var value: Int? = null
    biPipe.makePipe().sink { it, _ -> value = it }
    biPipe.accept(elem, false)
    assertEquals(biFn(elem, false), value)
    biPipe.accept(elem, true)
    assertEquals(biFn(elem, true), value)
  }
}
