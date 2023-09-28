package analysis.technical;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import analysis.strategies.Strategy;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TechnicalTest {

    // test of calculation of single strategy, and results
    // test it starts working and collecting after start() and stops after stop() <- rather e2e tests

    /*
    chcemy zrobić to, że po dodaniu nowych wyników aktualizują się nam strategie
    potrzebujemy zdefiniowanego już bufora cen, które były zdefiniowane,
        oraz czegoś, co pozwoli nam na obliczenie tychże strategii (może da się strategie obliczać iteracyjnie?)
    a co z periodami??
     */
    /*
    co gdyby: zamiast mapy zastosować kolejkę, i do niej zapisywać kolejne obliczenia?
     */
    @Test
    public void strategyUpdatesAfterReceivingCandle() {
        BigDecimal aapl_price_1000 = BigDecimal.valueOf(1000);
        BigDecimal strategy_41_123 = BigDecimal.valueOf(41.123);
        BigDecimal eur_price_4_654 = BigDecimal.valueOf(4.654);
        BigDecimal strategy_65_880 = BigDecimal.valueOf(65.880);
        String eurpln = "EUR/PLN";
        String aapl = "AAPL";
        String strategyName = "STRATEGY";

        Queue<Entry> stockDataQueue = new ArrayDeque<>();
        stockDataQueue.add(new Entry(100L, aapl, aapl_price_1000));
        stockDataQueue.add(new Entry(103L, eurpln, eur_price_4_654));

        Scrapper scrapper = Mockito.mock(Scrapper.class);
        Strategy strategy = Mockito.mock(Strategy.class);

        Mockito.when(strategy.calculate(aapl_price_1000)).thenReturn(strategy_41_123);
        Mockito.when(strategy.calculate(eur_price_4_654)).thenReturn(strategy_65_880);
        Technical technical = new TechnicalImpl(scrapper, stockDataQueue);

        technical.addStrategy(strategyName, strategy);

        ((TechnicalImpl) technical).updateStrategies();

        Map<String, BigDecimal> eurPlnStr = technical.getStrategyResultsForSymbol(eurpln);
        assertEquals(eurPlnStr.size(), 1);
        assertEquals(strategy_65_880, eurPlnStr.get(strategyName));

        Map<String, BigDecimal> aaplStr = technical.getStrategyResultsForSymbol(aapl);
        assertEquals(aaplStr.size(), 1);
        assertEquals(strategy_41_123, aaplStr.get(strategyName));

//        technical.stop();
    }

    @Test
    public void testGetRecentCandle() throws NoPriceFoundException {
        String eurpln = "EUR/PLN";
        String nonExistingUsdpln = "USD/PLN";

        Queue<Entry> stockDataQueue = new ArrayDeque<>(
                List.of(new Entry(0L, eurpln, BigDecimal.valueOf(1)),
                new Entry(1000L, eurpln, BigDecimal.valueOf(22)),
                new Entry(2000L, eurpln, BigDecimal.valueOf(333)),
                new Entry(55_000L, eurpln, BigDecimal.valueOf(4444)),
                new Entry(60_000L, eurpln, BigDecimal.valueOf(55555)),
                new Entry(63_000L, eurpln, BigDecimal.valueOf(666666))
                )
        );

        Scrapper scrapper = Mockito.mock(Scrapper.class);

        TechnicalImpl technical = new TechnicalImpl(scrapper, stockDataQueue);

        technical.updateStrategies();

        BigDecimal recentSecondCandle = technical.getRecentCandleFor(eurpln, Frequency.SECOND);
        assertEquals(BigDecimal.valueOf(666666), recentSecondCandle);

        BigDecimal recentMinuteCandle = technical.getRecentCandleFor(eurpln, Frequency.MINUTE);
        assertEquals(BigDecimal.valueOf(55555), recentMinuteCandle);

        BigDecimal recent15MinutesCandle = technical.getRecentCandleFor(eurpln, Frequency._15_MINUTES);
        assertEquals(BigDecimal.valueOf(1), recent15MinutesCandle);

        assertThrows(NoPriceFoundException.class, () -> technical.getRecentCandleFor(nonExistingUsdpln, Frequency.MINUTE));
    }
}

// todo:
// get exisiting symbol,
// get unexisting symbol
// test get candle buffer of specific size - mocked
// get existing symbol,
// get number greater than buffer,
// get number smaller than buffer,
// get unexisting symbol

// how to test collaboration between Scrapper and Technical? Queue? also e2e tests?