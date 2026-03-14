package me.theos.pipedream

import java.util.function.Consumer
import java.util.function.Supplier

class SinkCollection<T>(private val sink: MutableCollection<T>) : Sinkable<T> {
  override fun accept(elem: T) {
    sink.add(elem)
  }

  override fun complete() {}

  fun toList(): List<T> = sink.toList()

  override fun toString() = "SinkCollection{sink=$sink}"
}

class SinkValue<T> : Sinkable<T>, Supplier<T> {
  private var value: T? = null

  override fun get(): T = value ?: throw NullPointerException("No value set")

  override fun accept(elem: T) {
    if (value != null) throw IllegalStateException("SinkValue can only hold 1 value")
    value = elem
  }

  override fun complete() {}

  override fun toString() = "SinkValue{value=$value}"
}

class SinkConsumer<in T>(private val sink: Consumer<T>) : Sinkable<T> {
  override fun accept(elem: T) = sink.accept(elem)
  override fun complete() {}
  override fun toString() = "SinkConsumer{sink=$sink}"
}

class SinkReduction<T>(private val reducer: (T, T) -> T) : Sinkable<T>, Supplier<T> {
  private var acc: T? = null
  private var hasValue = false

  override fun get(): T = if (hasValue) acc!! else throw IllegalStateException("No elements to reduce")

  override fun accept(elem: T) {
    if (!hasValue) {
      acc = elem
      hasValue = true
    } else {
      acc = reducer(acc!!, elem)
    }
  }

  override fun complete() {}

  override fun toString() = "SinkReduction{acc=$acc}"
}
