package me.theos.pipedream

import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

interface Pipeable<T> : Sinkable<T> {
  fun <R> map(transform: Function<T, R>): Pipeable<R>
  fun filter(predicate: Predicate<T>): Pipeable<T>
  fun sinkSupplier(): Supplier<List<T>>
  fun sinkIterable(): Iterable<T>
  fun sinkCollection(): SinkCollection<T>
  fun sinkCollection(sink: MutableCollection<T>): SinkCollection<T>
  fun sinkConsumer(sink: Consumer<T>)
  fun sinkValue(): Supplier<T>
  fun <R> reduce(ident: R, reducer: (prev: R, curr: T) -> R): Pipeable<R>
}
