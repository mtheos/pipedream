package me.theos.pipedream

import com.google.common.base.Preconditions
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate

internal class FunctionalPipe<T, R>(private val fn: Function<T, R>) : Sinkable<T> {
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

internal class BiFunctionalPipe<T, R>(private val fn: BiFunction<T, Boolean, R>) : Sinkable<T> {
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

internal class FilterPipe<T>(private var fn: Predicate<T>) : Pipe<T>() {
  override fun accept(elem: T, last: Boolean) {
    if (fn.test(elem)) {
      super.accept(elem, last)
    }
  }
}

internal class ReductionPipe<T>(private val fn: BiFunction<T, T, T>) : Pipe<T>() {
  private var acc: T? = null

  override fun toString(): String {
    return "${javaClass.simpleName}{sinks=$sinks}"
  }

  override fun accept(elem: T, last: Boolean) {
    acc = when (acc) {
      null -> elem
      else -> fn.apply(acc!!, elem)
    }
    if (last) {
      super.accept(acc!!, true)
    }
  }
}

internal class FoldingPipe<T, R>(private var acc: R, private val fn: BiFunction<R, T, R>) : Sinkable<T> {
  private var sink: Sinkable<R>? = null

  override fun toString(): String {
    return "${javaClass.simpleName}{sinks=$sink}"
  }

  override fun accept(elem: T, last: Boolean) {
    Preconditions.checkNotNull(sink)
    acc = fn.apply(acc, elem)
    if (last) {
      sink!!.accept(acc, true)
    }
  }

  fun makePipe(): Pipeable<R> {
    Preconditions.checkState(sink == null)
    return Pipe<R>().also { sink = it }
  }
}
