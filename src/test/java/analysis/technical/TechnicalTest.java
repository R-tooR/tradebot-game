package analysis.technical;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import analysis.strategies.Strategy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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

        Strategy strategy = Mockito.mock(Strategy.class);

        when(strategy.calculate(aapl_price_1000)).thenReturn(strategy_41_123);
        when(strategy.calculate(eur_price_4_654)).thenReturn(strategy_65_880);
        Technical technical = new TechnicalImpl(stockDataQueue);

        technical.addStrategy(strategyName, strategy);

        ((TechnicalImpl) technical).processQueue();

        Map<String, BigDecimal> eurPlnStr = technical.getStrategyResultsForSymbol(eurpln);
        assertEquals(eurPlnStr.size(), 1);
        assertEquals(strategy_65_880, eurPlnStr.get(strategyName));

        Map<String, BigDecimal> aaplStr = technical.getStrategyResultsForSymbol(aapl);
        assertEquals(aaplStr.size(), 1);
        assertEquals(strategy_41_123, aaplStr.get(strategyName));
    }

    @Test
    public void testGetRecentCandleWhenTimeIsInRange() throws NoPriceFoundException {
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

        TechnicalImpl technical = Mockito.spy(new TechnicalImpl(stockDataQueue));

        when(technical.getTimestampMillis()).thenReturn(64_000L);

        technical.processQueue();

        BigDecimal recentSecondCandle = technical.getRecentCandleFor(eurpln, Frequency.SECOND);
        assertEquals(BigDecimal.valueOf(666666), recentSecondCandle);

        BigDecimal recentMinuteCandle = technical.getRecentCandleFor(eurpln, Frequency.MINUTE);
        assertEquals(BigDecimal.valueOf(55555), recentMinuteCandle);

        BigDecimal recent15MinutesCandle = technical.getRecentCandleFor(eurpln, Frequency._15_MINUTES);
        assertEquals(BigDecimal.valueOf(1), recent15MinutesCandle);
    }

    @Test
    public void testGetRecentCandleForNonExistingSymbol() {
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

        TechnicalImpl technical = new TechnicalImpl(stockDataQueue);
        technical.processQueue();

        assertThrows(NoPriceFoundException.class, () -> technical.getRecentCandleFor(nonExistingUsdpln, Frequency.MINUTE));
    }

    @Test
    public void testGetStrategiesForNonExistingSymbol() {
        BigDecimal eur_price_4_654 = BigDecimal.valueOf(4.654);
        BigDecimal strategy_65_880 = BigDecimal.valueOf(65.880);
        String eurpln = "EUR/PLN";
        String nonExistingAapl = "AAPL";
        String strategyName = "STRATEGY";

        Queue<Entry> stockDataQueue = new ArrayDeque<>();
        stockDataQueue.add(new Entry(103L, eurpln, eur_price_4_654));

        Strategy strategy = Mockito.mock(Strategy.class);

        when(strategy.calculate(eur_price_4_654)).thenReturn(strategy_65_880);
        TechnicalImpl technical = new TechnicalImpl(stockDataQueue);

        technical.addStrategy(strategyName, strategy);

        technical.processQueue();

        assertNull(technical.getStrategyResultsForSymbol(nonExistingAapl));
    }

    @ParameterizedTest
    @MethodSource("candleBuffers")
    public void getCandleBuffer(Frequency freq, int bufferSize, List<Double> expectedResults) throws NoPriceFoundException {
        String eurpln = "EUR/PLN";

        Queue<Entry> stockDataQueue = new ArrayDeque<>(
                List.of(new Entry(0L, eurpln,       BigDecimal.valueOf(4.01)),
                        new Entry(1000L, eurpln,    BigDecimal.valueOf(4.02)),
                        new Entry(2000L, eurpln,    BigDecimal.valueOf(4.03)),
                        new Entry(3000L, eurpln,    BigDecimal.valueOf(4.04)),
                        new Entry(4000L, eurpln,    BigDecimal.valueOf(4.05)),
                        new Entry(5000L, eurpln,    BigDecimal.valueOf(4.06)),
                        new Entry(6000L, eurpln,    BigDecimal.valueOf(4.07)),
                        new Entry(7000L, eurpln,    BigDecimal.valueOf(4.08)),
                        new Entry(8000L, eurpln,    BigDecimal.valueOf(4.09)),
                        new Entry(15_000L, eurpln,  BigDecimal.valueOf(4.10)),
                        new Entry(30_000L, eurpln,  BigDecimal.valueOf(4.11)),
                        new Entry(45_000L, eurpln,  BigDecimal.valueOf(4.12)),
                        new Entry(55_000L, eurpln,  BigDecimal.valueOf(4.13)),
                        new Entry(60_000L, eurpln,  BigDecimal.valueOf(4.14)),
                        new Entry(75_000L, eurpln,  BigDecimal.valueOf(4.15)),
                        new Entry(76_000L, eurpln,  BigDecimal.valueOf(4.16)),
                        new Entry(90_000L, eurpln,  BigDecimal.valueOf(4.17)),
                        new Entry(105_000L, eurpln, BigDecimal.valueOf(4.18)),
                        new Entry(115_000L, eurpln, BigDecimal.valueOf(4.19)),
                        new Entry(116_000L, eurpln, BigDecimal.valueOf(4.20)),
                        new Entry(117_000L, eurpln, BigDecimal.valueOf(4.21)),
                        new Entry(118_000L, eurpln, BigDecimal.valueOf(4.22)),
                        new Entry(119_000L, eurpln, BigDecimal.valueOf(4.23)),
                        new Entry(120_000L, eurpln, BigDecimal.valueOf(4.24)),
                        new Entry(121_000L, eurpln, BigDecimal.valueOf(4.25))
                )
        );

        TechnicalImpl technical = Mockito.spy(new TechnicalImpl(stockDataQueue));

        when(technical.getTimestampMillis()).thenReturn(121_111L);

        technical.processQueue();

        List<Entry> candleBuffer = technical.getCandleBuffer(bufferSize, freq, eurpln);

        assertNotNull(candleBuffer);
        assertEntryValuesInSpecifiedOrder(candleBuffer, expectedResults);
    }

    private static Stream<Arguments> candleBuffers() {
        return Stream.of(
                Arguments.of(Frequency.SECOND, 7, List.of(4.25,4.24,4.23,4.22,4.21,4.20,4.19)),
                Arguments.of(Frequency.SECOND, 4, List.of(4.25,4.24,4.23,4.22)),
                Arguments.of(Frequency.SECOND, 10, List.of(4.25,4.24,4.23,4.22,4.21,4.20,4.19)),
                Arguments.of(Frequency.MINUTE, 3, List.of(4.24,4.14,4.01))
        );
    }

    private void assertEntryValuesInSpecifiedOrder(List<Entry> entries, List<Double> doubles) {
        assertEquals(doubles.size(), entries.size(), """
                List of entries does not match expected length of 'doubles'
                """ + doubles + " != " + entries.stream().map(Entry::value).toList());
        for(int i = 0; i < entries.size(); i++) {
            assertEquals(entries.get(i).value().doubleValue(), doubles.get(i));
        }
    }
}


// test get candle when some candles may be missing - maybe enable feature of approximation??
// get number greater than buffer,
// test when current time is far away from recent candle

// todo: should technical accept only specific pairs?
// how to test collaboration between Scrapper and Technical? Queue? also e2e tests?