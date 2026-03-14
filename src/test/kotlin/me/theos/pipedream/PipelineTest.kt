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
    Pipeline.from(elements)
      .map { it.length }
      .sink(result)
      .pipe()
    
    assertThat(result).containsExactly(3, 3, 5, 4)
  }

  @Test
  fun testMapThenFilter() {
    val result = mutableListOf<String>()
    Pipeline.from(elements)
      .map { it.uppercase() }
      .filter { it.length > 3 }
      .sink(result)
      .pipe()
    
    assertThat(result).containsExactly("THREE", "FOUR")
  }

  @Test
  fun testFilter() {
    val result = mutableListOf<String>()
    val pipeline = Pipeline.from(elements)
      .filter { it.length % 2 == 1 }
      .sink(result)
    
    pipeline.pipe()
    
    assertThat(result).containsExactly("one", "two", "three")
  }

  @Test
  fun testFold() {
    val result = mutableListOf<Int>()
    Pipeline.from(elements)
      .fold(0) { acc: Int, s: String -> acc + s.length }
      .sink(result)
      .pipe()
    
    assertThat(result).containsExactly(15)
  }

  @Test
  fun testMultipleTransformations() {
    val result = mutableListOf<String>()
    Pipeline.from(elements)
      .map { it.uppercase() }
      .map { it.reversed() }
      .filter { it.length > 3 }
      .sink(result)
      .pipe()
    
    assertThat(result).containsExactly("EERHT", "RUOF")
  }

  @Test
  fun testSinkToResult() {
    val pipeline = Pipeline.from(elements)
      .map { it.length }
      .sink()
    pipeline.pipe()
    val result = pipeline.result()
    
    assertThat(result).containsExactly(3, 3, 5, 4)
  }

  @Test
  fun testEmptySource() {
    val result = mutableListOf<Int>()
    Pipeline.from(emptyList<String>())
      .map { it.length }
      .sink(result)
      .pipe()
    
    assertThat(result).isEmpty()
  }

  @Test
  fun testSingleElement() {
    val result = mutableListOf<Int>()
    Pipeline.from(listOf("a"))
      .map { it.length }
      .sink(result)
      .pipe()
    
    assertThat(result).containsExactly(1)
  }

  @Test
  fun testSourceCanOnlyBeUsedOnce() {
    val pipeline = Pipeline.from(elements).map { it.length }.sink()
    pipeline.pipe()
    
    assertThatThrownBy { pipeline.pipe() }
      .isInstanceOf(IllegalStateException::class.java)
      .hasMessage("Source has already been consumed")
  }

  @Test
  fun testSinkConsumer() {
    var lastValue: String? = null
    Pipeline.from(elements)
      .sink { lastValue = it }
      .pipe()
    
    assertThat(lastValue).isEqualTo("four")
  }

  @Test
  fun testVarargsSource() {
    val result = mutableListOf<String>()
    Pipeline.from("a", "b", "c")
      .sink(result)
      .pipe()
    
    assertThat(result).containsExactly("a", "b", "c")
  }

  @Test
  fun testFilterThenMap() {
    val result = mutableListOf<Int>()
    Pipeline.from(elements)
      .filter { it.length > 3 }
      .map { it.length }
      .sink(result)
      .pipe()
    
    assertThat(result).containsExactly(5, 4)
  }

  @Test
  fun testMultipleSinks() {
    val result1 = mutableListOf<String>()
    val result2 = mutableListOf<Int>()
    
    Pipeline.from(elements)
      .map { it.uppercase() }
      .sink(result1)
      .map { it.length }
      .sink(result2)
      .pipe()
    
    assertThat(result1).containsExactly("ONE", "TWO", "THREE", "FOUR")
    assertThat(result2).containsExactly(3, 3, 5, 4)
  }

  @Test
  fun testReduce() {
    val pipeline = Pipeline.from(elements)
      .map { it.length }
      .reduce { acc, it -> acc + it }
    
    pipeline.pipe()
    
    assertThat(pipeline.reduced()).isEqualTo(15)
  }

  @Test
  fun testLargeCollection() {
    val largeList = (1..10000).map { "item$it" }
    val result = mutableListOf<Int>()
    
    Pipeline.from(largeList)
      .map { it.length }
      .sink(result)
      .pipe()
    
    assertThat(result.size).isEqualTo(10000)
  }

  @Test
  fun testMapReturningNull() {
    val result: MutableList<String?> = mutableListOf()
    Pipeline.from(listOf("a", "bb", "ccc"))
      .map<String?> { if (it.length > 1) it else null }
      .sink(result)
      .pipe()
    
    assertThat(result).containsExactly(null, "bb", "ccc")
  }

  @Test
  fun testMultipleMapFilters() {
    val result = mutableListOf<String>()
    Pipeline.from(listOf("a", "bb", "ccc", "dddd", "eeeee"))
      .map { it.uppercase() }
      .filter { it.startsWith("C") || it.startsWith("D") }
      .map { it.reversed() }
      .sink(result)
      .pipe()
    
    assertThat(result).containsExactly("CCC", "DDDD")
  }

  @Test
  fun testChainedFold() {
    val result = mutableListOf<Int>()
    Pipeline.from(listOf("a", "bb", "ccc"))
      .fold(0) { acc, s -> acc + 1 }
      .sink(result)
      .pipe()
    
    assertThat(result).containsExactly(3)
  }
}
