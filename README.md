# Pipedream

A library for creating streams with multiple transformations and multiple consumers.

Like a network of pipes you can apply transformations to your data and collect the results along the way.

## Examples

### Basic Usage
```kotlin
val result = mutableListOf<Int>()
Source.from(listOf("one", "two", "three"))
  .map { it.length }
  .sink(result)
  .pipe()

println(result) // [3, 3, 5]
```

### Chaining Transformations
```kotlin
val result = mutableListOf<String>()
Source.from(listOf("ruby", "sapphire", "onyx", "emerald", "diamond"))
  .map { it.uppercase() }
  .filter { it.length > 5 }
  .sink(result)
  .pipe()

println(result) // [SAPPHIRE, EMERALD, DIAMOND]
```

### Using Result Directly
```kotlin
val result = Source.from(listOf("one", "two", "three"))
  .map { it.length }
  .sink()
  .pipe()
  .result()

println(result) // [3, 3, 5]
```

### Fold (Aggregation)
```kotlin
val result = mutableListOf<Int>()
Source.from(listOf("one", "two", "three"))
  .fold(0) { acc, s -> acc + s.length }
  .sink(result)
  .pipe()

println(result) // [11]
```

### Varargs Source
```kotlin
val result = mutableListOf<String>()
Source.from("a", "b", "c")
  .sink(result)
  .pipe()

println(result) // [a, b, c]
```

### Sink Consumer
```kotlin
var lastValue: String? = null
Source.from(listOf("one", "two", "three"))
  .sink { lastValue = it }
  .pipe()

println(lastValue) // three
```

### Custom TransformPipe
Extend TransformPipe when your transformation changes the type of your data:
```kotlin
class Derivative : TransformPipe<Point, Double>() {
  private var prev: Point? = null

  override fun transform(elem: Point): Double? {
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

## API

### Source
- `Source.from(Iterable<T>)` - Create a pipeline from a list/collection
- `Source.from(vararg T)` - Create a pipeline from varargs
- `Source.from(Iterator<T>)` - Create a pipeline from an iterator
- `Source.from(Stream<T>)` - Create a pipeline from a stream

### Pipeline Operations
- `map(transform: (T) -> R)` - Transform each element
- `filter(predicate: (T) -> Boolean)` - Filter elements
- `fold(acc: R, reducer: (R, T) -> R)` - Aggregate elements

### Terminal Operations
- `sink()` - Add a primary sink (collects to internal list, retrievable via result())
- `sink(MutableCollection<T>)` - Add a sink that collects to a provided collection
- `sink((T) -> Unit)` - Add a consumer sink for side effects
- `pipe()` - Execute the pipeline
- `result()` - Get the result from a primary sink
