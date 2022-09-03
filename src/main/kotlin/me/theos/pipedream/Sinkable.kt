package me.theos.pipedream

interface Sinkable<in T> {
  fun accept(elem: T, last: Boolean)
}
