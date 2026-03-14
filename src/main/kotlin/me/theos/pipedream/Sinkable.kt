package me.theos.pipedream

interface Sinkable<in T> {
    fun accept(elem: T): Unit
}

class SinkCollection<T>(private val sink: MutableCollection<T>) : Sinkable<T> {
    override fun accept(elem: T): Unit { sink.add(elem) }
    fun toList(): List<T> = sink.toList()
}
