package me.theos.pipedream

abstract class TransformPipe<T, R> : Sinkable<T> {
  private var sink: Sinkable<R>? = null

  override fun accept(elem: T) {
    val transformed = transform(elem)
    if (transformed != null) {
      sink!!.accept(transformed)
    }
  }

  override fun complete() {
    sink?.complete()
  }

  protected fun sinkAccept(elem: R) {
    sink!!.accept(elem)
  }

  abstract fun transform(elem: T): R?

  @Suppress("UNCHECKED_CAST")
  fun makePipe(): Pipeable<R> {
    val pipe = Pipe<R>()
    sink = pipe
    return pipe
  }

  override fun toString(): String = "TransformPipe{sink=$sink}"
}
