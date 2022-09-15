package me.theos.pipedream

import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

interface Pipeable<T> : Sinkable<T> {
  fun into(pipeable: Pipeable<T>): Pipeable<T>
  fun <R> into(transformPipe: TransformPipe<T, R>): Pipeable<R>
  fun <R> map(transform: (T) -> R): Pipeable<R>
  fun <R> map(transform: Function<T, R>): Pipeable<R>
  fun <R> biMap(transform: (T, Boolean) -> R): Pipeable<R>
  fun <R> biMap(transform: BiFunction<T, Boolean, R>): Pipeable<R>
  fun filter(predicate: (T) -> Boolean): Pipeable<T>
  fun filter(predicate: Predicate<T>): Pipeable<T>
  fun sink(): SinkCollection<T>
  fun sink(sink: MutableCollection<T>): SinkCollection<T>
  fun sink(sink: (T) -> Unit)
  fun sink(sink: Consumer<T>)
  fun sink(sink: Sinkable<T>)
  fun sinkValue(): Supplier<T>
  fun reduce(reducer: (acc: T, it: T) -> T): Pipeable<T>
  fun reduce(reducer: BiFunction<T, T, T>): Pipeable<T>
  fun <R> reduce(acc: R, reducer: (acc: R, it: T) -> R): Pipeable<R>
  fun <R> reduce(acc: R, reducer: BiFunction<R, T, R>): Pipeable<R>
  fun <R> fold(acc: R, reducer: (acc: R, it: T) -> R): Pipeable<R>
  fun <R> fold(acc: R, reducer: BiFunction<R, T, R>): Pipeable<R>
}
