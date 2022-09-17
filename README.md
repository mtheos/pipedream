# Pipedream

A library for creating streams with multiple transformations and multiple consumers.

Like a network of pipes you can apply transformations to your data and collect the results along the way.

## Examples
* Every function creates it's own pipe which can independently transform and filter data allowing you to process a stream in multiple ways at once. 
  ```kotlin
  val data = listOf("ruby", "sapphire", "onyx", "emerald", "diamond")
  val pipe = pipeFor(data) // type matching or Pipe<String>()
  
  val capitalize = pipe.map { it[0].uppercase() + it.substring(1) }
  val longNames = pipe.filter { it.length > 5 }
  val capitalizedShortNames = capitalize.filter { it.length < 5 }
  
  // Create sinks
  val longNamesSink = longNames.sink()
  val capitalizedShortNamesSink = capitalizedShortNames.sink()
  
  println(longNamesSink.get()) // [] no data piped yet
  
  data.source().pipe(pipe) // pipe the data from a source
  
  println(longNamesSink.get()) // [sapphire, emerald, diamond]
  println(capitalizedShortNamesSink.get()) // [Ruby, Onyx]
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
    override fun accept(elem: Point, last: Boolean) {
      val next = transform(elem, last)
      if (next != null) {
        super.sinkAccept(next, last)
      }
    }
  
    private var prev: Point? = null
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

## Future:
* I don't like the verbosity of having to declare the pipe and sink separately.
* Maybe something like this would be better:
    ```kotlin
    val data = listOf("ruby", "sapphire", "onyx", "emerald", "diamond")
    val pipe = pipeFor(data) // type matching or Pipe<String>()

    val capitalize = pipe.map { it[0].uppercase() + it.substring(1) }
    val longNames = pipe.filter { it.length > 5 }.withSink() // attach sink here
    val capitalizedShortNames = capitalize.filter { it.length < 5 }.withSink()

    data.source().pipe(pipe) // pipe the data from a source

    println(capitalize.getSink()) // error - no sink was created
    println(longNames.getSink()) // [sapphire, emerald, diamond]
    println(capitalizedShortNames.getSink()) // [Ruby, Onyx]
    ```
