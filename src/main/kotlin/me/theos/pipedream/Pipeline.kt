package me.theos.pipedream

import java.util.stream.Stream

sealed class Transform<T, R> {
    abstract fun apply(value: T): R
    
    class Map<T, R>(val fn: (T) -> R) : Transform<T, R>() {
        override fun apply(value: T): R = fn(value)
    }
    
    class Filter<T>(val predicate: (T) -> Boolean) : Transform<T, T>() {
        override fun apply(value: T): T {
            if (!predicate(value)) throw FILTERED
            return value
        }
    }
}

private object FILTERED : Throwable()

class Pipeline<T> private constructor(
    private val source: Iterator<T>,
    private val transforms: List<Transform<T, *>>
) {
    fun <R> map(transform: (T) -> R): Pipeline<R> {
        @Suppress("UNCHECKED_CAST")
        val newTransforms = if (transforms.isNotEmpty() && transforms.last() is Transform.Map<*, *>) {
            val lastMap = transforms.last() as Transform.Map<Any?, Any?>
            val composedFn: (Any?) -> Any? = { input -> transform(lastMap.fn(input) as T) }
            transforms.dropLast(1) + Transform.Map(composedFn) as Transform<T, *>
        } else {
            val erasedFn: (Any?) -> Any? = { input -> transform(input as T) }
            transforms + Transform.Map(erasedFn) as Transform<T, *>
        }
        return Pipeline(source, newTransforms) as Pipeline<R>
    }

    fun filter(predicate: (T) -> Boolean): Pipeline<T> {
        @Suppress("UNCHECKED_CAST")
        return Pipeline(source, transforms + Transform.Filter(predicate) as Transform<T, *>)
    }

    fun sink(): MutableList<T> {
        val result = mutableListOf<T>()
        execute(result)
        return result
    }

    fun sink(result: MutableCollection<in T>): Terminal<T> {
        execute(result)
        return Terminal(this)
    }

    fun sink(consumer: (T) -> Unit): Terminal<T> {
        for (item in source) {
            try {
                var value: Any? = item
                for (transform in transforms) {
                    value = (transform as Transform<Any?, Any?>).apply(value!!)
                }
                consumer(value as T)
            } catch (e: FILTERED) {
                // filtered
            }
        }
        return Terminal(this)
    }

    fun reduce(reducer: (T, T) -> T): Reduced<T> {
        var acc: T? = null
        var hasValue = false
        
        for (item in source) {
            try {
                var value: Any? = item
                for (transform in transforms) {
                    value = (transform as Transform<Any?, Any?>).apply(value!!)
                }
                val typedValue = value as T
                if (!hasValue) {
                    acc = typedValue
                    hasValue = true
                } else {
                    acc = reducer(acc!!, typedValue)
                }
            } catch (e: FILTERED) {
                // filtered
            }
        }
        
        return Reduced(acc!!)
    }

    @Suppress("UNCHECKED_CAST")
    internal fun execute(result: MutableCollection<in T>) {
        for (item in source) {
            try {
                var value: Any? = item
                for (transform in transforms) {
                    value = (transform as Transform<Any?, Any?>).apply(value!!)
                }
                result.add(value as T)
            } catch (e: FILTERED) {
                // filtered out
            }
        }
    }

    companion object {
        fun <T> of(data: Iterable<T>): Pipeline<T> = of(data.iterator())
        fun <T> of(vararg data: T): Pipeline<T> = of(data.iterator())
        fun <T> of(iterator: Iterator<T>): Pipeline<T> = Pipeline(iterator, emptyList())
        fun <T> of(stream: Stream<T>): Pipeline<T> = of(stream.iterator())
    }
}

class Terminal<T>(private val pipeline: Pipeline<T>) {
    fun pipe() {
        val result = mutableListOf<T>()
        pipeline.execute(result)
    }
    
    fun result(): List<T> {
        val result = mutableListOf<T>()
        pipeline.execute(result)
        return result
    }
}

class Reduced<T>(private val value: T) {
    fun get(): T = value
}
