package me.theos.pipedream

class FilterPipe<T>(private val predicate: (T) -> Boolean) : Sinkable<T> {
  private var sink: Sinkable<T>? = null

  override fun toString(): String {
    return "${javaClass.simpleName}{sink=$sink}"
  }

  override fun accept(elem: T) {
    if (predicate(elem)) {
      sink?.accept(elem)
    }
  }

  override fun complete() {
    sink?.complete()
  }

  fun makePipe(): Pipeable<T> {
    return Pipe<T>().also { sink = it }
  }
}
