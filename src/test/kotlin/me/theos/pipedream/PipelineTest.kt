package me.theos.pipedream

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PipelineTest {
  private lateinit var elements: List<String>

  @BeforeEach
  fun setUp() {
    elements = listOf("one", "two", "three", "four")
  }

  @Test
  fun testBasicPipeline() {
    val result = mutableListOf<Int>()
    Pipeline.of(elements)
      .map { it.length }
      .sink(result)
    
    assertThat(result).containsExactly(3, 3, 5, 4)
  }

  @Test
  fun testMapThenFilter() {
    val result = mutableListOf<String>()
    Pipeline.of(elements)
      .map { it.uppercase() }
      .filter { it.length > 3 }
      .sink(result)
    
    assertThat(result).containsExactly("THREE", "FOUR")
  }

  @Test
  fun testFilter() {
    val result = mutableListOf<String>()
    Pipeline.of(elements)
      .filter { it.length % 2 == 1 }
      .sink(result)
    
    assertThat(result).containsExactly("one", "two", "three")
  }

  @Test
  fun testMultipleTransformations() {
    val result = mutableListOf<String>()
    Pipeline.of(elements)
      .map { it.uppercase() }
      .map { it.reversed() }
      .filter { it.length > 3 }
      .sink(result)
    
    assertThat(result).containsExactly("EERHT", "RUOF")
  }

  @Test
  fun testSinkToResult() {
    val result = Pipeline.of(elements)
      .map { it.length }
      .sink()
    
    assertThat(result).containsExactly(3, 3, 5, 4)
  }

  @Test
  fun testEmptySource() {
    val result = mutableListOf<Int>()
    Pipeline.of(emptyList<String>())
      .map { it.length }
      .sink(result)
    
    assertThat(result).isEmpty()
  }

  @Test
  fun testSingleElement() {
    val result = mutableListOf<Int>()
    Pipeline.of(listOf("a"))
      .map { it.length }
      .sink(result)
    
    assertThat(result).containsExactly(1)
  }

  @Test
  fun testSinkConsumer() {
    var lastValue: String? = null
    Pipeline.of(elements)
      .sink { lastValue = it }
    
    assertThat(lastValue).isEqualTo("four")
  }

  @Test
  fun testVarargsSource() {
    val result = mutableListOf<String>()
    Pipeline.of("a", "b", "c")
      .sink(result)
    
    assertThat(result).containsExactly("a", "b", "c")
  }

  @Test
  fun testFilterThenMap() {
    val result = mutableListOf<Int>()
    Pipeline.of(elements)
      .filter { it.length > 3 }
      .map { it.length }
      .sink(result)
    
    assertThat(result).containsExactly(5, 4)
  }

  @Test
  fun testReduce() {
    val result = Pipeline.of(elements)
      .map { it.length }
      .reduce { acc, it -> acc + it }
    
    assertThat(result.get()).isEqualTo(15)
  }

  @Test
  fun testLargeCollection() {
    val largeList = (1..10000).map { "item$it" }
    val result = mutableListOf<Int>()
    
    Pipeline.of(largeList)
      .map { it.length }
      .sink(result)
    
    assertThat(result.size).isEqualTo(10000)
  }

  @Test
  fun testMapReturningNull() {
    val result: MutableList<String?> = mutableListOf()
    Pipeline.of(listOf("a", "bb", "ccc"))
      .map<String?> { if (it.length > 1) it else null }
      .sink(result)
    
    assertThat(result).containsExactly(null, "bb", "ccc")
  }

  @Test
  fun testMultipleMapFilters() {
    val result = mutableListOf<String>()
    Pipeline.of(listOf("a", "bb", "ccc", "dddd", "eeeee"))
      .map { it.uppercase() }
      .filter { it.startsWith("C") || it.startsWith("D") }
      .map { it.reversed() }
      .sink(result)
    
    assertThat(result).containsExactly("CCC", "DDDD")
  }
}
