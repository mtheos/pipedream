package me.theos.pipedream

import io.reactivex.rxjava3.core.Flowable
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1, jvmArgsAppend = ["-Xmx512m", "-Xms512m"])
open class PipelineBenchmark {
    private val list = (1..10000).map { "item$it" }

    // ========== Pipedream ==========

    @Benchmark
    fun pipelineMapFilterSink(): List<Int> {
        val result = mutableListOf<Int>()
        Pipeline.of(list)
            .map { it.length }
            .filter { it > 5 }
            .sink(result)
        return result
    }

    @Benchmark
    fun pipelineMap(): List<Int> {
        val result = mutableListOf<Int>()
        Pipeline.of(list)
            .map { it.length }
            .sink(result)
        return result
    }

    @Benchmark
    fun pipelineReduce(): Int {
        return Pipeline.of(list)
            .map { it.length }
            .reduce { acc, i -> acc + i }
            .get()
    }

    @Benchmark
    fun pipelineLargeCollection(): List<Int> {
        val largeList = (1..100000).map { "item$it" }
        val result = mutableListOf<Int>()
        Pipeline.of(largeList)
            .map { it.length }
            .filter { it > 5 }
            .sink(result)
        return result
    }

    // ========== Java Streams ==========

    @Benchmark
    fun streamMapFilterCollect(): List<Int> {
        return list.stream()
            .map { it.length }
            .filter { it > 5 }
            .toList()
    }

    @Benchmark
    fun streamMap(): List<Int> {
        return list.stream()
            .map { it.length }
            .toList()
    }

    @Benchmark
    fun streamReduce(): Int {
        return list.stream()
            .map { it.length }
            .reduce { acc, i -> acc + i }
            .orElse(0)
    }

    @Benchmark
    fun streamLargeCollection(): List<Int> {
        val largeList = (1..100000).map { "item$it" }
        return largeList.stream()
            .map { it.length }
            .filter { it > 5 }
            .toList()
    }

    // ========== RxJava ==========

    @Benchmark
    fun rxJavaMapFilter(): List<Int> {
        return Flowable.fromIterable(list)
            .map { it.length }
            .filter { it > 5 }
            .toList()
            .blockingGet()
    }

    @Benchmark
    fun rxJavaMap(): List<Int> {
        return Flowable.fromIterable(list)
            .map { it.length }
            .toList()
            .blockingGet()
    }

    @Benchmark
    fun rxJavaReduce(): Int {
        return Flowable.fromIterable(list)
            .map { it.length }
            .reduce { acc, i -> acc + i }
            .blockingGet() ?: 0
    }

    @Benchmark
    fun rxJavaLargeCollection(): List<Int> {
        val largeList = (1..100000).map { "item$it" }
        return Flowable.fromIterable(largeList)
            .map { it.length }
            .filter { it > 5 }
            .toList()
            .blockingGet()
    }
}
