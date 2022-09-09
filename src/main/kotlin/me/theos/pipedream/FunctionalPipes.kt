package me.theos.pipedream

import com.google.common.base.Preconditions
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate

class FunctionalPipe<T, R>(private val fn: Function<T, R>) : Sinkable<T> {
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

class BiFunctionalPipe<T, R>(private val fn: BiFunction<T, Boolean, R>) : Sinkable<T> {
  private var sink: Sinkable<R>? = null

  override fun toString(): String {
    return "${javaClass.simpleName}{sinks=$sink}"
  }

  override fun accept(elem: T, last: Boolean) {
    Preconditions.checkNotNull(sink)
    sink!!.accept(fn.apply(elem, last), last)
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
