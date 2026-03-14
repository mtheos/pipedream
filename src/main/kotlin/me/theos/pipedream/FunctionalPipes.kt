package me.theos.pipedream

import com.google.common.base.Preconditions

class FunctionalPipe<T, R>(private val fn: (T) -> R) : Sinkable<T> {
  private var sink: Sinkable<R>? = null

  override fun toString(): String {
    return "${javaClass.simpleName}{sinks=$sink}"
  }

  override fun accept(elem: T) {
    Preconditions.checkNotNull(sink)
    sink!!.accept(fn(elem))
  }

  override fun complete() {
    sink?.complete()
  }

  fun makePipe(): Pipeable<R> {
    Preconditions.checkState(sink == null)
    return Pipe<R>().also { sink = it }
  }
}

class FoldingPipe<T, R>(private var acc: R, private val fn: (R, T) -> R) : Sinkable<T> {
  private var sink: Sinkable<R>? = null

  override fun toString(): String {
    return "${javaClass.simpleName}{sinks=$sink}"
  }

  override fun accept(elem: T) {
    Preconditions.checkNotNull(sink)
    acc = fn(acc, elem)
  }

  override fun complete() {
    sink?.accept(acc)
    sink?.complete()
  }

  fun makePipe(): Pipeable<R> {
    Preconditions.checkState(sink == null)
    return Pipe<R>().also { sink = it }
  }
}
