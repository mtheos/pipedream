package me.theos.pipedream

import java.util.stream.Stream

class Source<T>(private val source: Iterator<T>) {
  private var consumed: Boolean = false

  fun pipe(pipe: Pipeable<T>) {
    if (consumed) throw IllegalStateException("Source has already been consumed")
    consumed = true
    while (source.hasNext()) {
      val value = source.next()
      val last = !source.hasNext()
      pipe.accept(value, last)
    }
  }

  override fun toString(): String {
    return "Source{source=$source}"
  }
}

fun <T> Stream<T>.source(): Source<T> {
  return Source(iterator())
}

fun <T> List<T>.source(): Source<T> {
  return Source(iterator())
}

fun <T> Iterator<T>.source(): Source<T> {
  return Source(this)
}

fun <T> sourceOf(vararg source: T): Source<T> {
  return Source((source.iterator()))
}
