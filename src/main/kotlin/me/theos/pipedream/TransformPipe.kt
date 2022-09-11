package me.theos.pipedream

import com.google.common.base.Preconditions

abstract class TransformPipe<T, R> : Sinkable<T> {
  private var sink: Sinkable<R>? = null

  override fun toString(): String {
    return "${javaClass.simpleName}{sinks=$sink}"
  }

  override fun accept(elem: T, last: Boolean) {
    sinkAccept(transform(elem, last)!!, last)
  }

  protected fun sinkAccept(elem: R, last: Boolean) {
    Preconditions.checkNotNull(sink)
    sink!!.accept(elem, last)
  }

  abstract fun transform(elem: T, last: Boolean): R?

  fun makePipe(): Pipeable<R> {
    Preconditions.checkState(sink == null)
    return Pipe<R>().also { sink = it }
  }
}
