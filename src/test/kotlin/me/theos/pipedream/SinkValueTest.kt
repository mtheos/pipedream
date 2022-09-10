package me.theos.pipedream

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SinkValueTest {

  companion object {
    private var sink: SinkValue<String>? = null
  }

  @BeforeEach
  fun setUp() {
    sink = SinkValue()
  }

  @Test
  fun testSinkHoldsItem() {
    val produced = "One"
    sink!!.accept(produced, false)
    assertEquals(produced, sink!!.get())
  }

  @Test
  fun testSinkHoldsOnlyOneItem() {
    val produced = "One"
    sink!!.accept(produced, false)
    assertThatThrownBy { sink!!.accept(produced, false) }.isExactlyInstanceOf(IllegalStateException::class.java)
    assertEquals(produced, sink!!.get())
  }
}
