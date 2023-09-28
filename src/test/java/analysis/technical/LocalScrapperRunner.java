package analysis.technical;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalScrapperRunner {
    @Test
//    @org.junit.jupiter.api.Disabled
    public void runScrapperInRealEnvironment() throws InterruptedException {
        ExecutorService service = Executors.newWorkStealingPool(2);
        ConcurrentLinkedQueue<Entry> currentStockPrices = new ConcurrentLinkedQueue<>();
        Scrapper scrapper = new GoogleFinanceScrapper(List.of("BTC-USD", "ETH-USD"), currentStockPrices, service);

        scrapper.start();
        long startTime = Instant.now().toEpochMilli();
        long endTime = Instant.now().toEpochMilli();
        while (endTime - startTime < 60_000) {
            System.out.println(scrapper.getCurrentPrices());
            Thread.sleep(1000);
            endTime = Instant.now().toEpochMilli();
        }

        scrapper.stop();
        service.shutdown();
    }

    @Test
//    @org.junit.jupiter.api.Disabled
    public void runScrapperWithTechnicalInRealEnvironment() throws InterruptedException {
        ExecutorService service = Executors.newWorkStealingPool(2);
        ConcurrentLinkedQueue<Entry> currentStockPrices = new ConcurrentLinkedQueue<>();
        Scrapper scrapper = new GoogleFinanceScrapper(List.of("BTC-USD", "ETH-USD"), currentStockPrices, service);
        Technical technical = new TechnicalImpl(scrapper, currentStockPrices);
        scrapper.start();
        long startTime = Instant.now().toEpochMilli();
        long endTime = Instant.now().toEpochMilli();
        while (endTime - startTime < 60_000) {
            System.out.println(scrapper.getCurrentPrices());
            Thread.sleep(1000);
            endTime = Instant.now().toEpochMilli();
        }

        scrapper.stop();
        service.shutdown();
    }
}
