package me.theos.pipedream

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SinkValueTest {
  companion object {
  }

  @BeforeEach
  fun setUp() {
  }

  @Test
  fun testSinkHoldsItem() {
    val produced = "One"
    val sink = SinkValue<String>()
    sink.accept(produced, false)
    assertEquals(produced, sink.get())
  }

  @Test
  fun testSinkHoldsOnlyOneItem() {
    val produced = "One"
    val sink = SinkValue<String>()
    sink.accept(produced, false)
    assertThatThrownBy { sink.accept(produced, false) }.isExactlyInstanceOf(IllegalStateException::class.java)
    assertEquals(produced, sink.get())
  }
}
