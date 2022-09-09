package me.theos.pipedream

import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.stream.Stream

open class Pipe<T> : Pipeable<T> {
  protected val sinks: MutableList<Sinkable<T>> = mutableListOf()

  override fun accept(elem: T, last: Boolean) {
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

  override fun tee(): Pipeable<T> {
    return this.also { sink() }
  }

  override fun <R> map(transform: Function<T, R>): Pipeable<R> {
    return FunctionalPipe(transform).also { sinks.add(it) }.makePipe()
  }
  override fun <R> biMap(transform: BiFunction<T, Boolean, R>): Pipeable<R> {
    return BiFunctionalPipe(transform).also { sinks.add(it) }.makePipe()
  }

  override fun <R> reduce(ident: R, reducer: (R, T) -> R): Pipeable<R> {
    return ReductionPipe(ident, reducer).also { sinks.add(it) }.makePipe()
  }

  override fun filter(predicate: Predicate<T>): Pipeable<T> {
    return FilterPipe(predicate).also { sinks.add(it) }
  }

  override fun sink(): SinkCollection<T> {
    return SinkCollection(mutableListOf<T>()).also { sinks.add(it) }
  }

  override fun sink(sink: MutableCollection<T>): SinkCollection<T> {
    return SinkCollection(sink).also { sinks.add(it) }
  }

  override fun sink(sink: Consumer<T>) {
    sink(SinkConsumer(sink::accept))
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
