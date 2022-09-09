package me.theos.pipedream

import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

interface Pipeable<T> : Sinkable<T> {
  fun into(pipeable: Pipeable<T>): Pipeable<T>
  fun <R> into(transformPipe: TransformPipe<T, R>): Pipeable<R>
  fun tee(): Pipeable<T>
  fun <R> map(transform: Function<T, R>): Pipeable<R>
  fun <R> biMap(transform: BiFunction<T, Boolean, R>): Pipeable<R>
  fun filter(predicate: Predicate<T>): Pipeable<T>
  fun sink(): SinkCollection<T>
  fun sink(sink: MutableCollection<T>): SinkCollection<T>
  fun sink(sink: Consumer<T>)
  fun sink(sink: Sinkable<T>)
  fun sinkValue(): Supplier<T>
  fun <R> reduce(ident: R, reducer: (prev: R, curr: T) -> R): Pipeable<R>
}
