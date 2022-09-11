package me.theos.pipedream

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class SourceTest {
  companion object {
    private lateinit var source: Source<String>
  }

  @BeforeEach
  fun setUp() {
    source = sourceOf("One", "Two", "Three")
  }

  @Test
  fun testSourcePipesInOrder() {
    val produced = setOf("One", "Two", "Three")
    val final = "Three"
    val sink = { elem: String, last: Boolean ->
      assertTrue(elem in produced, "Element $elem not in $produced")
      assertTrue(elem == final || !last, "Element $elem not last")
    }
    source.pipe(sink)
  }

  @Test
  fun testSourceCanOnlyBeUsedOnce() {
    val sink = Sinkable { _: String, _: Boolean -> }
    source.pipe(sink)
    assertThatThrownBy { source.pipe(sink) }.isExactlyInstanceOf(IllegalStateException::class.java)
  }
}
