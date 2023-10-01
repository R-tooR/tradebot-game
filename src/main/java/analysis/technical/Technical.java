package analysis.technical;

import analysis.strategies.Strategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Technical extends Scheduledable {
    BigDecimal getRecentCandleFor(String pairName, Frequency freq) throws NoPriceFoundException;
    List<Entry> getCandleBuffer(int size, Frequency freq, String pairName) throws NoPriceFoundException;
    Map<String, BigDecimal> getStrategyResultsForSymbol(String symbol);

    void addStrategy(String strategyName, Strategy strategy);
}
