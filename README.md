# Pipedream

A library for creating streams with multiple transformations and multiple consumers.

Like a network of pipes you can apply transformations to your data and collect the results along the way.

## Examples
* Every function creates its own pipe which can independently transform and filter data allowing you to process a stream in multiple ways at once.
```kotlin
val data = listOf("ruby", "sapphire", "onyx", "emerald", "diamond")
val pipe = pipeFor(data) // type matching or Pipe<String>()

// No sink, the values here won't be retrievable
val capitalize = pipe.map { it[0].uppercase() + it.substring(1) }

// Primary sink `sink()` can be retrieved later with `result()`
val longNames = pipe.filter { it.length > 5 }.sink()

// Pass your own collection to sink() and it will fill that (this is not a primary sink)
val capitalizedShortNamesList = mutableListOf<String>()
capitalize.filter { it.length < 5 }.sink(capitalizedShortNamesList)

val longestName = capitalize.reduce { acc, it -> if (acc.length > it.length) acc else it }
// supplier interface for singular results
val longestNameResult = longestName.sinkValue()

println(longNames.result()) // [] no data piped yet

data.source().pipe(pipe) // pipe the data from a source

println(longNames.result()) // [sapphire, emerald, diamond]
// println(capitalizedShortNames.result()) // Error - capitalizedShortNames doesn't have a primary sink
println(capitalizedShortNamesList) // [Ruby, Onyx]
println(longestNameResult.get()) // Sapphire
```
* Extend Pipe and override accept to chose how data is is aggregated and when it gets passed on.
```kotlin
class AddPairs : Pipe<Int>() {
  private var prev: Int? = null

  override fun accept(elem: Int, last: Boolean) {
    val next = next(elem)
    if (next != null) {
      super.accept(next, last)
    }
  }

  fun next(cur: Int): Int? {
    if (prev == null) {
      prev = cur
      return null
    }
    val sum = cur + prev!!
    prev = null
    return sum
  }
}
```
* Extend TransformPipe if your transformation changes the type of your data
```kotlin
class Derivative : TransformPipe<Point, Double>() {
  private var prev: Point? = null

  override fun accept(elem: Point, last: Boolean) {
    val next = transform(elem, last)
    if (next != null) {
      super.sinkAccept(next, last)
    }
  }

  override fun transform(elem: Point, last: Boolean): Double? {
    if (prev == null) {
      prev = elem
      return null
    }
    val derivative = (elem.y - prev!!.y) / (elem.x - prev!!.x)
    prev = elem
    return derivative
  }
}
```
