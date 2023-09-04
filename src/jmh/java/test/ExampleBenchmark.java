package test;

import org.junit.jupiter.api.BeforeAll;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import test.scrapping.GoogleScrapper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Fork(value = 1, warmups = 1)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(org.openjdk.jmh.annotations.Scope.Benchmark)
public class ExampleBenchmark {
    private GoogleScrapper scrapper;
    @Setup
    public void init() {
        scrapper = new GoogleScrapper();
    }
//    @Benchmark
//    public static void fibonacciSpeed(Blackhole bh) {
//        int fibonacci = new Fibonacci().get(20);
//
//        bh.consume(fibonacci);
//    }
//
//    @Benchmark
//    public static void fibonacciMemoizedSpeed(Blackhole bh) {
//        int fibonacci = new FibonacciMemoized().get(20);
//
//        bh.consume(fibonacci);
//    }

    @Benchmark
    public void googleFinanceSpeed(Blackhole bh) throws IOException {
        String price = scrapper.getCurrentPrice();

        bh.consume(price);
    }

    @Benchmark
    public void googleFinanceSpeedJAXRS(Blackhole bh) throws IOException {
        String price = scrapper.getCurrentPriceWithJAX_RS();

        bh.consume(price);
    }
}
