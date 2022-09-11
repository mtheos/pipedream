package me.theos.pipedream

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class BiFunctionalPipeTest {
  companion object {
    private val biFn = { it: String, last: Boolean -> if (!last) it.length else 0 }
  }

  @BeforeEach
  fun setUp() {
  }

  @Test
  fun testAppliesBiFn() {
    val elem = "One"
    val biPipe = BiFunctionalPipe(biFn)
    var value: Int? = null
    biPipe.makePipe().sink { it, _ -> value = it }
    biPipe.accept(elem, false)
    assertEquals(biFn(elem, false), value)
    biPipe.accept(elem, true)
    assertEquals(biFn(elem, true), value)
  }
}
