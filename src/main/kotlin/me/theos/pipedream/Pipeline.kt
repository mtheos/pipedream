package me.theos.pipedream

import java.util.function.Supplier
import java.util.stream.Stream

class Pipeline<T> private constructor(
  private val source: Source<*>,
  private val firstPipe: Pipeable<*>,
  private val lastPipe: Pipeable<T>,
  private val reductionSink: Supplier<T>?
) {
  @Suppress("UNCHECKED_CAST")
  fun <R> map(transform: (T) -> R): Pipeline<R> {
    val newPipeable = (lastPipe as Pipeable<T>).map(transform)
    return Pipeline(source, firstPipe, newPipeable as Pipeable<R>, null)
  }

  @Suppress("UNCHECKED_CAST")
  fun filter(predicate: (T) -> Boolean): Pipeline<T> {
    val newPipeable = (lastPipe as Pipeable<T>).filter(predicate)
    return Pipeline(source, firstPipe, newPipeable, null)
  }

  @Suppress("UNCHECKED_CAST")
  fun <R> fold(acc: R, reducer: (R, T) -> R): Pipeline<R> {
    val newPipeable = (lastPipe as Pipeable<T>).fold(acc, reducer)
    return Pipeline(source, firstPipe, newPipeable as Pipeable<R>, null)
  }

  fun sink(): Pipeline<T> {
    lastPipe.sink()
    return Pipeline(source, firstPipe, lastPipe, null)
  }

  fun sink(collection: MutableCollection<T>): Pipeline<T> {
    lastPipe.sink(collection)
    return Pipeline(source, firstPipe, lastPipe, null)
  }

  fun sink(consumer: (T) -> Unit): Pipeline<T> {
    lastPipe.sink(consumer)
    return Pipeline(source, firstPipe, lastPipe, null)
  }

  fun reduce(reducer: (T, T) -> T): Pipeline<T> {
    val sink = lastPipe.reduce(reducer)
    return Pipeline(source, firstPipe, lastPipe, sink)
  }

  @Suppress("UNCHECKED_CAST")
  fun pipe() {
    (source as Source<T>).pipe(firstPipe as Pipeable<T>)
  }

  fun result(): List<T> {
    return lastPipe.result()
  }

  fun reduced(): T {
    return reductionSink!!.get()
  }

  companion object {
    fun <T> from(iterable: Iterable<T>): Pipeline<T> {
      return from(iterable.iterator())
    }

    fun <T> from(vararg elements: T): Pipeline<T> {
      return from(elements.iterator())
    }

    fun <T> from(iterator: Iterator<T>): Pipeline<T> {
      val pipe = Pipe<T>()
      return Pipeline(me.theos.pipedream.Source(iterator), pipe, pipe, null)
    }

    fun <T> from(stream: Stream<T>): Pipeline<T> {
      return from(stream.iterator())
    }
  }
}
