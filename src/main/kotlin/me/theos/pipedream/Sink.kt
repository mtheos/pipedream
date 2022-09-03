package me.theos.pipedream

import com.google.common.base.Preconditions
import java.util.function.Consumer
import java.util.function.Supplier

class SinkCollection<T>(private val sink: MutableCollection<T>) : Sinkable<T>, Iterable<T>, Supplier<List<T>> {
  override fun accept(elem: T, last: Boolean) {
    sink.add(elem)
  }

  override fun get(): List<T> {
    return sink.toList()
  }

  override fun iterator(): Iterator<T> {
    return sink.iterator()
  }

  override fun toString(): String {
    return "${javaClass.simpleName}{sink=$sink}"
  }
}

class SinkValue<T> : Sinkable<T>, Supplier<T> {
  private var value: T? = null

  override fun get(): T {
    Preconditions.checkNotNull(value)
    return value!!
  }

  override fun accept(elem: T, last: Boolean) {
    Preconditions.checkState(value == null, "SinkValue can only hold 1 value")
    value = elem
  }

  override fun toString(): String {
    return "${javaClass.simpleName}{sink=$value}"
  }
}


class SinkConsumer<in T>(private val sink: Consumer<T>) : Sinkable<T> {
  override fun accept(elem: T, last: Boolean) {
    sink.accept(elem)
  }

  override fun toString(): String {
    return "${javaClass.simpleName}{sink=$sink}"
  }
}
