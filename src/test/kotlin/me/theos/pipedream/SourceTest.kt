package me.theos.pipedream

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class SourceTest {

  companion object {
    private var source: Source<String>? = null
    @JvmStatic
    @BeforeAll
    fun setUp() {
      source = sourceOf("One", "Two", "Three")
    }
  }

  @Test
  fun pipe() {
    val produced = setOf("One", "Two", "Three")
    val final = "Three"
    val sink = Sinkable { elem: String, last: Boolean ->
      kotlin.test.assertTrue(elem in produced, "Element $elem not in $produced")
      kotlin.test.assertTrue(elem == final || !last, "Element $elem not last")
    }
    source!!.pipe(sink)
  }
}
