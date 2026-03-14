package me.theos.pipedream

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
@Fork(1, jvmArgsAppend = ["-Xmx512m", "-Xms512m", "-Xlog:gc*:file=gc.log"])
open class PipelineBenchmark {
    private val list = (1..10000).map { "item$it" }

    @Benchmark
    fun pipelineMapFilterSink(): List<Int> {
        val result = mutableListOf<Int>()
        Pipeline.from(list)
            .map { it.length }
            .filter { it > 5 }
            .sink(result)
            .pipe()
        return result
    }

    @Benchmark
    fun streamMapFilterCollect(): List<Int> {
        return list.stream()
            .map { it.length }
            .filter { it > 5 }
            .toList()
    }

    @Benchmark
    fun pipelineMap(): List<Int> {
        val result = mutableListOf<Int>()
        Pipeline.from(list)
            .map { it.length }
            .sink(result)
            .pipe()
        return result
    }

    @Benchmark
    fun streamMap(): List<Int> {
        return list.stream()
            .map { it.length }
            .toList()
    }

    @Benchmark
    fun pipelineReduce(): Int {
        val pipeline = Pipeline.from(list)
            .map { it.length }
            .reduce { acc, i -> acc + i }
        pipeline.pipe()
        return pipeline.reduced()
    }

    @Benchmark
    fun streamReduce(): Int {
        return list.stream()
            .map { it.length }
            .reduce { acc, i -> acc + i }
            .orElse(0)
    }

    @Benchmark
    fun pipelineLargeCollection(): List<Int> {
        val largeList = (1..100000).map { "item$it" }
        val result = mutableListOf<Int>()
        Pipeline.from(largeList)
            .map { it.length }
            .filter { it > 5 }
            .sink(result)
            .pipe()
        return result
    }

    @Benchmark
    fun streamLargeCollection(): List<Int> {
        val largeList = (1..100000).map { "item$it" }
        return largeList.stream()
            .map { it.length }
            .filter { it > 5 }
            .toList()
    }
}
