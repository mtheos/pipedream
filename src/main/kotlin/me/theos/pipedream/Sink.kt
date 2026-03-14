package me.theos.pipedream

import com.google.common.base.Preconditions
import java.util.function.Consumer
import java.util.function.Supplier

class SinkCollection<T>(private val sink: MutableCollection<T>) : Sinkable<T> {
  override fun accept(elem: T) {
    sink.add(elem)
  }

  override fun complete() {}

  fun toList(): List<T> {
    return sink.toList()
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

  override fun accept(elem: T) {
    Preconditions.checkState(value == null, "SinkValue can only hold 1 value")
    value = elem
  }

  override fun complete() {}

  override fun toString(): String {
    return "${javaClass.simpleName}{sink=$value}"
  }
}


class SinkConsumer<in T>(private val sink: Consumer<T>) : Sinkable<T> {
  override fun accept(elem: T) {
    sink.accept(elem)
  }

  override fun complete() {}

  override fun toString(): String {
    return "${javaClass.simpleName}{sink=$sink}"
  }
}

class SinkReduction<T>(private val reducer: (T, T) -> T) : Sinkable<T>, Supplier<T> {
  private var acc: T? = null
  private var hasValue = false

  override fun get(): T {
    Preconditions.checkState(hasValue, "No elements to reduce")
    return acc!!
  }

  override fun accept(elem: T) {
    if (!hasValue) {
      acc = elem
      hasValue = true
    } else {
      acc = reducer(acc!!, elem)
    }
  }

  override fun complete() {}

  override fun toString(): String {
    return "${javaClass.simpleName}{acc=$acc}"
  }
}
