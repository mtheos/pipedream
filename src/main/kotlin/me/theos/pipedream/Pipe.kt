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
  private var primarySink: SinkCollection<T>? = null

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
    return FunctionalPipe(transform).also { sinks.add(it) }.makePipe()
  }

  override fun <R> biMap(transform: (T, Boolean) -> R): Pipeable<R> {
    return BiFunctionalPipe(transform).also { sinks.add(it) }.makePipe()
  }

  override fun <R> biMap(transform: BiFunction<T, Boolean, R>): Pipeable<R> {
    return BiFunctionalPipe(transform).also { sinks.add(it) }.makePipe()
  }

  override fun reduce(reducer: (T, T) -> T): Pipeable<T> {
    return ReductionPipe(reducer).also { sinks.add(it) }
  }

  override fun reduce(reducer: BiFunction<T, T, T>): Pipeable<T> {
    return ReductionPipe(reducer).also { sinks.add(it) }
  }

  override fun <R> reduce(acc: R, reducer: (R, T) -> R): Pipeable<R> {
    return fold(acc, reducer)
  }

  override fun <R> reduce(acc: R, reducer: BiFunction<R, T, R>): Pipeable<R> {
    return fold(acc, reducer)
  }

  override fun <R> fold(acc: R, reducer: (R, T) -> R): Pipeable<R> {
    return FoldingPipe(acc, reducer).also { sinks.add(it) }.makePipe()
  }

  override fun <R> fold(acc: R, reducer: BiFunction<R, T, R>): Pipeable<R> {
    return FoldingPipe(acc, reducer).also { sinks.add(it) }.makePipe()
  }

  override fun filter(predicate: (T) -> Boolean): Pipeable<T> {
    return FilterPipe(predicate).also { sinks.add(it) }
  }

  override fun filter(predicate: Predicate<T>): Pipeable<T> {
    return FilterPipe(predicate).also { sinks.add(it) }
  }

  override fun result(): List<T> {
    Preconditions.checkNotNull(primarySink, "Pipe doesn't have a primary sink. Use `sink()` to create a primary sink on this pipe.")
    return primarySink!!.toList()
  }

  override fun sink(): Pipeable<T> {
    SinkCollection<T>(mutableListOf()).also {
      sinks.add(it);
      primarySink = it
    }
    return this
  }

  override fun sink(sink: MutableCollection<T>): Pipeable<T> {
    SinkCollection(sink).also { sinks.add(it) }
    return this
  }

  override fun sink(sink: (T) -> Unit): Pipeable<T> {
    return sink(SinkConsumer(sink))
  }

  override fun sink(sink: Consumer<T>): Pipeable<T> {
    return sink { elem, _ -> sink.accept(elem) }
  }

  override fun sink(sink: Sinkable<T>): Pipeable<T> {
    sinks.add(sink)
    return this
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
