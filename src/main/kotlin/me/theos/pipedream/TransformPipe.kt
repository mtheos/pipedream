package me.theos.pipedream

import com.google.common.base.Preconditions

abstract class TransformPipe<T, R> : Sinkable<T> {
  private var sink: Sinkable<R>? = null

  override fun toString(): String {
    return "${javaClass.simpleName}{sinks=$sink}"
  }

  override fun accept(elem: T) {
    val transformed = transform(elem)
    if (transformed != null) {
      sinkAccept(transformed)
    }
  }

  override fun complete() {
    sink?.complete()
  }

  protected fun sinkAccept(elem: R) {
    Preconditions.checkNotNull(sink)
    sink!!.accept(elem)
  }

  abstract fun transform(elem: T): R?

  fun makePipe(): Pipeable<R> {
    Preconditions.checkState(sink == null)
    return Pipe<R>().also { sink = it }
  }
}
