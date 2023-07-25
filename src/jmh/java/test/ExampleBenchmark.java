package test;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@Fork(value = 1, warmups = 1)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class ExampleBenchmark {
    @Benchmark
    public static void fibonacciSpeed(Blackhole bh) {
        int fibonacci = new Fibonacci().get(20);

        bh.consume(fibonacci);
    }

    @Benchmark
    public static void fibonacciMemoizedSpeed(Blackhole bh) {
        int fibonacci = new FibonacciMemoized().get(20);

        bh.consume(fibonacci);
    }
}
