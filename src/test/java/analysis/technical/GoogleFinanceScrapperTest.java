package analysis.technical;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoogleFinanceScrapperTest {
    @Test
    public void test() throws InterruptedException {
        ExecutorService service = Executors.newWorkStealingPool(2);
        Scrapper scrapper = new GoogleFinanceScrapper(List.of("BTC-USD", "ETH-USD"), service);

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
