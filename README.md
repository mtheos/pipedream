# Pipedream

A high-performance Kotlin library for processing data through composable transformations.

Uses a single-pass execution model for optimal performance - all transforms are stored in a list and applied in one loop, minimizing object allocations.

## Examples

### Basic Usage
```kotlin
val result = Pipeline.of(listOf("one", "two", "three"))
  .map { it.length }
  .sink()

println(result) // [3, 3, 5]
```

### Chaining Transformations
```kotlin
val result = Pipeline.of(listOf("ruby", "sapphire", "onyx", "emerald", "diamond"))
  .map { it.uppercase() }
  .filter { it.length > 5 }
  .sink()

println(result) // [SAPPHIRE, EMERALD, DIAMOND]
```

### Collect to Existing Collection
```kotlin
val result = mutableListOf<Int>()
Pipeline.of(listOf("one", "two", "three"))
  .map { it.length }
  .sink(result)

println(result) // [3, 3, 5]
```

### Side Effects with Consumer
```kotlin
var lastValue: String? = null
Pipeline.of(listOf("one", "two", "three"))
  .sink { lastValue = it }

println(lastValue) // three
```

### Reduce (Aggregation)
```kotlin
val result = Pipeline.of(listOf("one", "two", "three"))
  .map { it.length }
  .reduce { acc, len -> acc + len }
  .get()

println(result) // 11
```

### Varargs Source
```kotlin
val result = Pipeline.of("a", "b", "c").sink()
println(result) // [a, b, c]
```

## API

### Pipeline Creation
- `Pipeline.of(Iterable<T>)` - Create a pipeline from a list/collection
- `Pipeline.of(vararg T)` - Create a pipeline from varargs
- `Pipeline.of(Iterator<T>)` - Create a pipeline from an iterator
- `Pipeline.of(Stream<T>)` - Create a pipeline from a stream

### Transformations
- `map(transform: (T) -> R)` - Transform each element to a new type
- `filter(predicate: (T) -> Boolean)` - Filter elements based on predicate

### Terminal Operations
- `sink()` - Execute pipeline and return results as MutableList
- `sink(MutableCollection<in T>)` - Execute pipeline and collect to provided collection
- `sink((T) -> Unit)` - Execute pipeline with consumer for side effects
- `reduce((T, T) -> T)` - Reduce to a single value, returns Reduced<T>

### Result Types
- `MutableList<T>` - Returned by `sink()` with no argument
- `Terminal` - Returned by `sink(collection)` and `sink(consumer)` for chaining
- `Reduced<T>` - Returned by `reduce()`, call `.get()` to retrieve value

## Performance

Pipedream is designed for high-performance streaming with:

- **Single-pass execution** - all transforms applied in one loop
- **Minimal object allocation** - transforms stored as sealed class instances
- **Exception-based filter** - uses throw/catch for filtering (like Java Streams)
- **Map-map fusion** - consecutive `map` operations are composed into a single function at build time, eliminating intermediate object allocations
- **Boxed types only** - for consistent benchmark comparison
