package me.theos.pipedream

import java.util.function.Supplier

interface Pipeable<T> : Sinkable<T> {
  fun <R> map(transform: (T) -> R): Pipeable<R>
  fun filter(predicate: (T) -> Boolean): Pipeable<T>
  fun <R> fold(acc: R, reducer: (acc: R, it: T) -> R): Pipeable<R>
  fun result(): List<T>
  fun sink(): Pipeable<T>
  fun sink(sink: MutableCollection<T>): Pipeable<T>
  fun sink(sink: (T) -> Unit): Pipeable<T>
  fun sink(sink: Sinkable<T>): Pipeable<T>
  fun reduce(reducer: (T, T) -> T): Supplier<T>
}
