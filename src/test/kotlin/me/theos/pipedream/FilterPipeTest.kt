package me.theos.pipedream

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.AssertionError
import kotlin.test.assertEquals

internal class FilterPipeTest {
  companion object {
    private lateinit var fn: (String) -> Boolean
    private lateinit var pipe: FilterPipe<String>
  }

  @BeforeEach
  fun setUp() {
    fn = { it.length % 2 == 0 }
    pipe = FilterPipe(fn)
  }

  @Test
  fun testAppliesFilter() {
    val elements = listOf("even", "odd", "even", "odd")
    pipe.sink { it -> if (it.length % 2 != 0) throw AssertionError() }
    elements.forEach { pipe.accept(it, false)}
  }
}
