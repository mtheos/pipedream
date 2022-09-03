package me.theos.pipedream

fun interface Sinkable<in T> {
  fun accept(elem: T, last: Boolean)
}
