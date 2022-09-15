# Pipedream

A library for creating streams with multiple transformations and multiple consumers.

Like a network of pipes you can apply transformations to your data and collect the results along the way.

## Example

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
