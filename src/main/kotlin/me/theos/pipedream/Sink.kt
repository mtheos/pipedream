package me.theos.pipedream

import com.google.common.base.Preconditions
import java.util.function.Consumer
import java.util.function.Supplier

internal class SinkCollection<T>(private val sink: MutableCollection<T>) : Sinkable<T> {
  override fun accept(elem: T, last: Boolean) {
    sink.add(elem)
  }

  fun toList(): List<T> {
    return sink.toList()
  }

  override fun toString(): String {
    return "${javaClass.simpleName}{sink=$sink}"
  }
}

internal class SinkValue<T> : Sinkable<T>, Supplier<T> {
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


internal class SinkConsumer<in T>(private val sink: Consumer<T>) : Sinkable<T> {
  override fun accept(elem: T, last: Boolean) {
    sink.accept(elem)
  }

  override fun toString(): String {
    return "${javaClass.simpleName}{sink=$sink}"
  }
}
