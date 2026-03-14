package me.theos.pipedream

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class TransformPipeTest {
  @Test
  fun testTransformPipe() {
    val result = mutableListOf<Int>()
    val transformPipe = object : TransformPipe<String, Int>() {
      override fun transform(elem: String): Int = elem.length
    }
    val pipe = transformPipe.makePipe()
    pipe.sink(result)
    
    transformPipe.accept("a")
    transformPipe.accept("bb")
    transformPipe.complete()
    
    assertThat(result).containsExactly(1, 2)
  }

  @Test
  fun testTransformPipeNullFilters() {
    val result = mutableListOf<String>()
    val transformPipe = object : TransformPipe<String, String>() {
      override fun transform(elem: String): String? {
        return if (elem.length > 1) elem else null
      }
    }
    val pipe = transformPipe.makePipe()
    pipe.sink(result)
    
    transformPipe.accept("a")
    transformPipe.accept("bb")
    transformPipe.accept("ccc")
    transformPipe.complete()
    
    assertThat(result).containsExactly("bb", "ccc")
  }

  @Test
  fun testTransformPipeTypeChange() {
    val result = mutableListOf<Double>()
    val transformPipe = object : TransformPipe<Int, Double>() {
      override fun transform(elem: Int): Double = elem.toDouble()
    }
    val pipe = transformPipe.makePipe()
    pipe.sink(result)
    
    transformPipe.accept(1)
    transformPipe.accept(2)
    transformPipe.complete()
    
    assertThat(result).containsExactly(1.0, 2.0)
  }

  @Test
  fun testTransformPipeComplete() {
    var completeCalled = false
    val transformPipe = object : TransformPipe<String, String>() {
      override fun transform(elem: String): String = elem
    }
    val pipe = transformPipe.makePipe()
    pipe.sink(object : Sinkable<String> {
      override fun accept(elem: String) {}
      override fun complete() { completeCalled = true }
    })
    
    transformPipe.accept("test")
    transformPipe.complete()
    
    assertThat(completeCalled).isTrue()
  }

  @Test
  fun testTransformPipeSinkAccept() {
    val result = mutableListOf<String>()
    val transformPipe = object : TransformPipe<Int, String>() {
      override fun transform(elem: Int): String = "num:$elem"
    }
    val pipe = transformPipe.makePipe()
    pipe.sink(result)
    
    transformPipe.accept(1)
    transformPipe.accept(2)
    transformPipe.complete()
    
    assertThat(result).containsExactly("num:1", "num:2")
  }
}
