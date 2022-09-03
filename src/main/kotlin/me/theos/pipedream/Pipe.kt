package me.theos.pipedream

import com.google.common.base.Preconditions
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

open class Pipe<T> : Pipeable<T> {
  protected val sinks: MutableList<Sinkable<T>> = mutableListOf()

  override fun accept(elem: T, last: Boolean) {
    sinks.forEach { sink ->
      sink.accept(elem, last)
    }
  }

  override fun toString(): String {
    return "${javaClass.simpleName}{sinks=${sinks.size}}"
  }

  override fun <R> map(transform: Function<T, R>): Pipeable<R> {
    return TransformPipe(transform).also { sinks.add(it) }.makePipe()
  }

  override fun <R> reduce(ident: R, reducer: (R, T) -> R): Pipeable<R> {
    return ReductionPipe(ident, reducer).also { sinks.add(it) }.makePipe()
  }

  override fun filter(predicate: Predicate<T>): Pipeable<T> {
    return FilterPipe(predicate).also { sinks.add(it) }
  }

  override fun sinkSupplier(): Supplier<List<T>> {
    return sinkCollection()
  }

  override fun sinkIterable(): Iterable<T> {
    return sinkCollection()
  }

  override fun sinkCollection(): SinkCollection<T> {
    return SinkCollection(mutableListOf<T>()).also { sinks.add(it) }
  }

  override fun sinkCollection(sink: MutableCollection<T>): SinkCollection<T> {
    return SinkCollection(sink).also { sinks.add(it) }
  }

  override fun sinkConsumer(sink: Consumer<T>) {
    sinks.add(SinkConsumer { t: T -> sink.accept(t) })
  }

  override fun sinkValue(): Supplier<T> {
    return SinkValue<T>().also { sinks.add(it) }
  }
}

class TransformPipe<T, R>(private val fn: Function<T, R>) : Sinkable<T> {
  private var sink: Sinkable<R>? = null

  override fun toString(): String {
    return "${javaClass.simpleName}{sinks=$sink}"
  }

  override fun accept(elem: T, last: Boolean) {
    Preconditions.checkNotNull(sink)
    sink!!.accept(fn.apply(elem), last)
  }

  fun makePipe(): Pipeable<R> {
    Preconditions.checkState(sink == null)
    return Pipe<R>().also { sink = it }
  }
}

class ReductionPipe<T, R>(private var ident: R, private val fn: (R, T) -> R) : Sinkable<T> {
  private var sink: Sinkable<R>? = null

  override fun toString(): String {
    return "${javaClass.simpleName}{sinks=$sink}"
  }

  override fun accept(elem: T, last: Boolean) {
    Preconditions.checkNotNull(sink)
    ident = fn(ident, elem)
    if (last) {
      sink!!.accept(ident, true)
    }
  }


  fun makePipe(): Pipeable<R> {
    Preconditions.checkState(sink == null)
    return Pipe<R>().also { sink = it }
  }
}

class FilterPipe<T>(private var fn: Predicate<T>) : Pipe<T>(), Pipeable<T> {
  override fun accept(elem: T, last: Boolean) {
    sinks.forEach { sink ->
      if (fn.test(elem)) {
        sink.accept(elem, last)
      }
    }
  }
}
