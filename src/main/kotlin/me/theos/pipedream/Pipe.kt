package me.theos.pipedream

import com.google.common.base.Preconditions
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.stream.Stream

open class Pipe<T> : Pipeable<T> {
  protected val sinks: MutableList<Sinkable<T>> = mutableListOf()

  override fun accept(elem: T, last: Boolean) {
    Preconditions.checkState(sinks.size > 0, "Pipe doesn't connect to anything")
    sinks.forEach { sink ->
      sink.accept(elem, last)
    }
  }

  override fun into(pipeable: Pipeable<T>): Pipeable<T> {
    return pipeable.also { sinks.add(it) }
  }

  override fun <R> into(transformPipe: TransformPipe<T, R>): Pipeable<R> {
    return transformPipe.also { sinks.add(it) }.makePipe()
  }

  override fun <R> map(transform: (T) -> R): Pipeable<R> {
    return FunctionalPipe(transform).also { sinks.add(it) }.makePipe()
  }

  override fun <R> map(transform: Function<T, R>): Pipeable<R> {
    @Suppress("UNCHECKED_CAST")
    return map(transform as (T) -> R)
  }

  override fun <R> biMap(transform: (T, Boolean) -> R): Pipeable<R> {
    return BiFunctionalPipe(transform).also { sinks.add(it) }.makePipe()
  }

  override fun <R> biMap(transform: BiFunction<T, Boolean, R>): Pipeable<R> {
    @Suppress("UNCHECKED_CAST")
    return biMap(transform as (T, Boolean) -> R)
  }

  override fun reduce(reducer: (T, T) -> T): Pipeable<T> {
    return ReductionPipe(reducer).also { sinks.add(it) }
  }

  override fun reduce(reducer: BiFunction<T, T, T>): Pipeable<T> {
    @Suppress("UNCHECKED_CAST")
    return reduce(reducer as (T, T) -> T)
  }

  override fun <R> reduce(acc: R, reducer: (R, T) -> R): Pipeable<R> {
    return fold(acc, reducer)
  }

  override fun <R> reduce(acc: R, reducer: BiFunction<R, T, R>): Pipeable<R> {
    @Suppress("UNCHECKED_CAST")
    return fold(acc, reducer as (R, T) -> R)
  }

  override fun <R> fold(acc: R, reducer: (R, T) -> R): Pipeable<R> {
    return FoldingPipe(acc, reducer).also { sinks.add(it) }.makePipe()
  }

  override fun <R> fold(acc: R, reducer: BiFunction<R, T, R>): Pipeable<R> {
    @Suppress("UNCHECKED_CAST")
    return fold(acc, reducer as (R, T) -> R)
  }

  override fun filter(predicate: (T) -> Boolean): Pipeable<T> {
    return FilterPipe(predicate).also { sinks.add(it) }
  }

  override fun filter(predicate: Predicate<T>): Pipeable<T> {
    @Suppress("UNCHECKED_CAST")
    return filter(predicate as (T) -> Boolean)
  }

  override fun sink(): SinkCollection<T> {
    return SinkCollection(mutableListOf<T>()).also { sinks.add(it) }
  }

  override fun sink(sink: MutableCollection<T>): SinkCollection<T> {
    return SinkCollection(sink).also { sinks.add(it) }
  }

  override fun sink(sink: (T) -> Unit) {
    sink(SinkConsumer(sink))
  }

  override fun sink(sink: Consumer<T>) {
    @Suppress("UNCHECKED_CAST")
    sink(sink as (T) -> Unit)
  }

  override fun sink(sink: Sinkable<T>) {
    sinks.add(sink)
  }

  override fun sinkValue(): Supplier<T> {
    return SinkValue<T>().also { sinks.add(it) }
  }

  override fun toString(): String {
    return "${javaClass.simpleName}{sinks=${sinks.size}}"
  }
}

fun <T> pipeFor(@Suppress("UNUSED_PARAMETER") unused: Source<T>): Pipe<T> {
  return Pipe()
}
fun <T> pipeFor(@Suppress("UNUSED_PARAMETER") unused: Iterable<T>): Pipe<T> {
  return Pipe()
}
fun <T> pipeFor(@Suppress("UNUSED_PARAMETER") unused: Stream<T>): Pipe<T> {
  return Pipe()
}
fun <T> pipeFor(@Suppress("UNUSED_PARAMETER") unused: T): Pipe<T> {
  return Pipe()
}
