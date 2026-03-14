package me.theos.pipedream

class Source<T>(private val source: Iterator<T>) {
  private var consumed: Boolean = false

  fun pipe(sink: Sinkable<T>) {
    if (consumed) throw IllegalStateException("Source has already been consumed")
    consumed = true
    while (source.hasNext()) {
      sink.accept(source.next())
    }
    sink.complete()
  }

  override fun toString(): String {
    return "Source{source=$source}"
  }
}
