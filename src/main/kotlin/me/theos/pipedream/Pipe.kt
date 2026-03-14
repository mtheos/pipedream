package me.theos.pipedream

import com.google.common.base.Preconditions
import java.util.function.Supplier

open class Pipe<T> : Pipeable<T> {
  protected val sinks: MutableList<Sinkable<T>> = mutableListOf()
  private var primarySink: SinkCollection<T>? = null

  override fun accept(elem: T) {
    Preconditions.checkState(sinks.size > 0, "Pipe doesn't connect to anything")
    sinks.forEach { sink ->
      sink.accept(elem)
    }
  }

  override fun complete() {
    sinks.forEach { it.complete() }
  }

  override fun <R> map(transform: (T) -> R): Pipeable<R> {
    return FunctionalPipe(transform).also { sinks.add(it) }.makePipe()
  }

  override fun filter(predicate: (T) -> Boolean): Pipeable<T> {
    return FilterPipe(predicate).also { sinks.add(it) }.makePipe()
  }

  override fun <R> fold(acc: R, reducer: (R, T) -> R): Pipeable<R> {
    return FoldingPipe(acc, reducer).also { sinks.add(it) }.makePipe()
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

  override fun sink(sink: Sinkable<T>): Pipeable<T> {
    sinks.add(sink)
    return this
  }

  override fun reduce(reducer: (T, T) -> T): Supplier<T> {
    return SinkReduction(reducer).also { sinks.add(it) }
  }

  override fun toString(): String {
    return "${javaClass.simpleName}{sinks=${sinks.size}}"
  }
}
