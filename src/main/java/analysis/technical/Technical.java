package analysis.technical;

import analysis.strategies.Strategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Technical {
    BigDecimal getRecentCandleFor(String pairName, Frequency freq);
    Optional<List<EntryValue>> getCandleBuffer(int size, Frequency freq, String pairName);
    Map<String, BigDecimal> getStrategyResultsForSymbol(String symbol);

    void addStrategy(String strategyName, Strategy strategy);

    void start();
    void stop();
}
