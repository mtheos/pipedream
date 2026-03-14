package me.theos.pipedream

class ReductionPipe<T>(private val fn: (T, T) -> T) : Sinkable<T> {
  private var sink: Sinkable<T>? = null
  private var acc: T? = null
  private var hasValue = false

  override fun toString(): String {
    return "${javaClass.simpleName}{sink=$sink}"
  }

  override fun accept(elem: T) {
    if (!hasValue) {
      acc = elem
      hasValue = true
    } else {
      acc = fn(acc!!, elem)
    }
  }

  override fun complete() {
    if (hasValue) {
      sink?.accept(acc!!)
    }
    sink?.complete()
  }

  fun makePipe(): Pipeable<T> {
    return Pipe<T>().also { sink = it }
  }
}
