package me.theos.pipedream

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.AssertionError

internal class FilterPipeTest {
  companion object {
    private val fn = { it: String -> it.length % 2 == 0 }
  }

  @BeforeEach
  fun setUp() {
  }

  @Test
  fun testAppliesFilter() {
    val elements = listOf("even", "odd", "even", "odd")
    val pipe = FilterPipe(fn)
    pipe.sink { it -> if (it.length % 2 != 0) throw AssertionError() }
    elements.forEach { pipe.accept(it, false)}
  }
}
