package analysis.technical;

import analysis.strategies.Strategy;

import java.math.BigDecimal;
import java.util.*;

public class TechnicalImpl implements Technical {

    private final Scrapper scrapper;
    private final Map<String, Queue<EntryValue>> pairsCandles;
    private final Map<String, Map<String, BigDecimal>> pairsStrategies; //todo nie uwzgledniaja czasu, uprosc,bo to za duzo i za nieczytelnie...
    private final Map<String, Strategy> strategies;
    public TechnicalImpl(Scrapper scrapper) {
        this.scrapper = scrapper;
        pairsCandles = new HashMap<>();
        strategies = new HashMap<>();
        pairsStrategies = new HashMap<>();
    }

    @Override
    public BigDecimal getRecentCandleFor(String pairName, Frequency freq) {
        return null;
    }

    @Override
    public Optional<List<EntryValue>> getCandleBuffer(int size, Frequency freq, String pairName) {
        return null;
    }

    @Override
    public Map<String, BigDecimal> getStrategyResultsForSymbol(String symbol) {
        return pairsStrategies.get(symbol);
    }

    @Override
    public void addStrategy(String strategyName, Strategy strategy) {
        strategies.put(strategyName, strategy);
    }

    @Override
    public void start() {
        var newPrices = scrapper.getCurrentPrices();
        newPrices.forEach((pairName, price) -> {
            if (!pairsCandles.containsKey(pairName)) {
                pairsCandles.put(pairName, new LinkedList<>());
            }
            pairsCandles.get(pairName).add(new EntryValue(pairName, price));

            if (!pairsStrategies.containsKey(pairName)) {
                pairsStrategies.put(pairName, new HashMap<>());
            }
            var strValuesForSpecificPair = pairsStrategies.get(pairName);
            strategies.forEach((strategyName, strategy) -> {
                strValuesForSpecificPair.put(strategyName, strategy.calculate(price));
            });

            pairsStrategies.put(pairName, strValuesForSpecificPair);

        });
    }

    @Override
    public void stop() {

    }
}
