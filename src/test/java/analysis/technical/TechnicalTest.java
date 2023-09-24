package analysis.technical;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import analysis.strategies.Strategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TechnicalTest {

    // test of calculation of single strategy, and results
    // test it starts working and collecting after start() and stops after stop() <- rather e2e tests

    /*
    chcemy zrobić to, że po dodaniu nowych wyników aktualizują się nam strategie
    potrzebujemy zdefiniowanego już bufora cen, które były zdefiniowane,
        oraz czegoś, co pozwoli nam na obliczenie tychże strategii (może da się strategie obliczać iteracyjnie?)
    a co z periodami??
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

        Scrapper scrapper = Mockito.mock(Scrapper.class);
        Strategy strategy = Mockito.mock(Strategy.class);

        Mockito.when(strategy.calculate(aapl_price_1000)).thenReturn(strategy_41_123);
        Mockito.when(strategy.calculate(eur_price_4_654)).thenReturn(strategy_65_880);
        Technical technical = new TechnicalImpl(scrapper);

        technical.addStrategy(strategyName, strategy);

        Map<String, Optional<BigDecimal>> mockedSampleResults = new HashMap<>();

        mockedSampleResults.put(aapl, Optional.of(aapl_price_1000));

        mockedSampleResults.put(eurpln, Optional.of(eur_price_4_654));
        Mockito.when(scrapper.getCurrentPrices()).thenReturn(mockedSampleResults);

        technical.start();

        Map<String, BigDecimal> eurPlnStr = technical.getStrategyResultsForSymbol(eurpln);
        assertEquals(eurPlnStr.size(), 1);
        assertEquals(strategy_65_880, eurPlnStr.get(strategyName));

        Map<String, BigDecimal> aaplStr = technical.getStrategyResultsForSymbol(aapl);
        assertEquals(aaplStr.size(), 1);
        assertEquals(strategy_41_123, aaplStr.get(strategyName));

        technical.stop();
    }
}

//todo: Scrapper
// test get recent candle for a symbol - mocked
// get exisiting symbol,
// get unexisting symbol
// test get candle buffer of specific size - mocked
// get existing symbol,
// get number greater than buffer,
// get number smaller than buffer,
// get unexisting symbol

// how to test collaboration between Scrapper and Technical? Queue? also e2e tests?