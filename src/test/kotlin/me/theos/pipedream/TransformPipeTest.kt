package me.theos.pipedream

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TransformPipeTest {
  companion object {
    private lateinit var fn1: (String) -> Boolean
    private lateinit var fn2: (String) -> Int
    private lateinit var pipe: TransformPipe<String, Int>
  }

  @BeforeEach
  fun setUp() {
    fn1 = { it -> it.length % 2 == 0 }
    fn2 = { it -> it.length }
    pipe = object : TransformPipe<String, Int>() {
      override fun accept(elem: String, last: Boolean) {
        val it = transform(elem, last)
        if (it != null) {
          super.sinkAccept(it, last)
        }
      }
      override fun transform(elem: String, last: Boolean) : Int? {
        return when (elem.length % 2) {
          0 -> elem.length
          1 -> null
          else -> throw AssertionError("elem $elem produced mod 2 result ${elem.length % 2} which is not 0 or 1")
        }
      }
    }
  }

  @Test
  fun testTransformsElements() {
    val elements = listOf("One", "Two", "Three", "Four")
    val sink = pipe.makePipe().sink()
    elements.iterator().let {
      while (it.hasNext()) {
        pipe.accept(it.next(), !it.hasNext())
      }
    }
    assertEquals(elements.filter(fn1).map(fn2), sink.get())
  }
}
