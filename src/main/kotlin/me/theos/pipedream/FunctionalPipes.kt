package me.theos.pipedream

class FunctionalPipe<T, R>(private val fn: (T) -> R) : Sinkable<T> {
  private var sink: Sinkable<R>? = null

  @Suppress("UNCHECKED_CAST")
  fun makePipe(): Pipeable<R> {
    val pipe = Pipe<R>()
    sink = pipe
    return pipe
  }

  override fun accept(elem: T) {
    sink!!.accept(fn(elem))
  }

  override fun complete() {
    sink?.complete()
  }

  override fun toString(): String {
    return "FunctionalPipe{sink=$sink}"
  }
}

class FoldingPipe<T, R>(private var acc: R, private val fn: (R, T) -> R) : Sinkable<T> {
  private var sink: Sinkable<R>? = null

  @Suppress("UNCHECKED_CAST")
  fun makePipe(): Pipeable<R> {
    val pipe = Pipe<R>()
    sink = pipe
    return pipe
  }

  override fun accept(elem: T) {
    acc = fn(acc, elem)
  }

  override fun complete() {
    sink?.accept(acc)
    sink?.complete()
  }

  override fun toString(): String {
    return "FoldingPipe{sink=$sink}"
  }
}
