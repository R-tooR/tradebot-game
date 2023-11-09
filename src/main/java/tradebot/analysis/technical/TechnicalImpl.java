package tradebot.analysis.technical;

import tradebot.analysis.strategies.Strategy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TechnicalImpl implements Technical {

    private static final long TIME_SHIFT_TOLLERANCE_IN_MILLIS = 800;
    private final Map<String, List<Entry>> pairsCandles;

                //   para         //strategia //wartość strategii

    //todo: nie ma potrzeby kolekcji 'pairsStrategiesResults': wynik może być przechowywany w instancji klasy Strategy...
    private final Map<String, Map<String, BigDecimal>> pairsStrategiesResults;
    private final Map<String, Strategy> strategies;

    private final Queue<Entry> pricesQueue;

    private final ScheduledExecutorService scheduledExecutorService;
    public TechnicalImpl(Queue<Entry> pricesQueue) {
        this.pricesQueue = pricesQueue;
        pairsCandles = new HashMap<>();
        strategies = new HashMap<>();
        pairsStrategiesResults = new HashMap<>();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    @Override
    public BigDecimal getRecentCandleFor(String pairName, Frequency freq) throws NoPriceFoundException {
        return extractRecentCandles(1, freq, pairName, getTimestampMillis()).get(0).value();
    }

    private static boolean isRecentPriceForFrequencyApprox(Frequency freq, Entry entry) {
        return entry.timestamp() % freq.milliseconds < TIME_SHIFT_TOLLERANCE_IN_MILLIS;
    }

    @Override
    public List<Entry> getCandleBuffer(int size, Frequency freq, String pairName) throws NoPriceFoundException {
        return extractRecentCandles(size, freq, pairName, getTimestampMillis());
    }

    private List<Entry> extractRecentCandles(int size, Frequency freq, String pairName, long currEpochMillis) throws NoPriceFoundException {
        if(pairsCandles.containsKey(pairName)) {
            List<Entry> setForSymbol = pairsCandles.get(pairName);

            List<Entry> results = setForSymbol.stream()
                    .filter(entry -> isRecentPriceForFrequencyApprox(freq, entry))
                    .filter(entry -> entry.timestamp() >= currEpochMillis - (size* freq.milliseconds))
                    .limit(size).collect(Collectors.toList());

            if(results.isEmpty()) {
                throw new NoPriceFoundException(
                        "Cannot find most recent price for " + pairName + " for " + freq.toString()
                );
            } else {
                return results;
            }
        } else {
            throw new NoPriceFoundException("Prices for " + pairName + " are not registered!");
        }
    }

    @Override
    public Map<String, BigDecimal> getStrategyResultsForSymbol(String symbol) {
        return pairsStrategiesResults.get(symbol);
    }

    @Override
    public void addStrategy(String strategyName, Strategy strategy) {
        strategies.put(strategyName, strategy);
    }

    @Override
    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(this::processQueue, 0, 5, TimeUnit.SECONDS);
    }

    protected void processQueue() {
        while (!pricesQueue.isEmpty()) {
            Entry entry = pricesQueue.poll();

            updateCandlesBuffer(entry);
            updateStrategiesResults(entry);
        }
    }

    private void updateStrategiesResults(Entry entry) {
        if (!pairsStrategiesResults.containsKey(entry.pairName())) {
            pairsStrategiesResults.put(entry.pairName(), new HashMap<>());
        }
        var strValuesForSpecificPair = pairsStrategiesResults.get(entry.pairName());
        strategies.forEach((strategyName, strategy) ->
            strValuesForSpecificPair.put(strategyName, strategy.calculate(Optional.ofNullable(entry.value()).orElse(BigDecimal.ZERO)))
        );
        pairsStrategiesResults.put(entry.pairName(), strValuesForSpecificPair);
    }

    private void updateCandlesBuffer(Entry entry) {
        if (!pairsCandles.containsKey(entry.pairName())) {
            pairsCandles.put(entry.pairName(), new LinkedList<>());
        }
        pairsCandles.get(entry.pairName()).add(0, new Entry(entry.timestamp(), entry.pairName(), Optional.ofNullable(entry.value()).orElse(BigDecimal.ZERO)));
    }

    @Override
    public void stop() {
        scheduledExecutorService.shutdown();
    }

    public long getTimestampMillis() {
        return Instant.now().toEpochMilli();
    }
}
