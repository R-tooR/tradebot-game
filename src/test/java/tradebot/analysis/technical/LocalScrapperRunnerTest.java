package tradebot.analysis.technical;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Disabled
public class LocalScrapperRunnerTest {
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
        Technical technical = new TechnicalImpl(currentStockPrices);

        scrapper.start();
        technical.start();
        long startTime = Instant.now().toEpochMilli();
        long endTime = Instant.now().toEpochMilli();
        while (endTime - startTime < 60_000) {
            System.out.println(scrapper.getCurrentPrices());
            try {
                System.out.println(technical.getRecentCandleFor("BTC-USD", Frequency.SECOND));
            } catch (NoPriceFoundException e) {
                System.err.println(e.getMessage());
            }
            Thread.sleep(1000);
            endTime = Instant.now().toEpochMilli();
        }

        scrapper.stop();
        technical.stop();
        service.shutdown();
    }

    //todo: round timestamp data - in configurable mode, so the dates will be rounded to whole seconds
}
