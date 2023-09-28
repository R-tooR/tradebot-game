package analysis.technical;

import analysis.strategies.Strategy;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TechnicalImpl implements Technical {

    private static final long TIME_SHIFT_TOLLERANCE_IN_MILLIS = 800;
    private final Scrapper scrapper;
    private final Map<String, List<Entry>> pairsCandles;

                //   para         //strategia //wartość strategii
    private final Map<String, Map<String, BigDecimal>> pairsStrategiesResults;
    private final Map<String, Strategy> strategies;

    private final Queue<Entry> pricesQueue;

    private final ScheduledExecutorService scheduledExecutorService;
    public TechnicalImpl(Scrapper scrapper, Queue<Entry> pricesQueue) {
        this.scrapper = scrapper;
        this.pricesQueue = pricesQueue;
        pairsCandles = new HashMap<>();
        strategies = new HashMap<>();
        pairsStrategiesResults = new HashMap<>();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    @Override
    public BigDecimal getRecentCandleFor(String pairName, Frequency freq) throws NoPriceFoundException {
        if(pairsCandles.containsKey(pairName)) {
            List<Entry> setForSymbol = pairsCandles.get(pairName);

            Optional<Entry> mostRecentForGivenFreq = setForSymbol.stream().filter(entry -> isRecentPriceForFrequencyApprox(freq, entry)).findFirst();
            Entry entry = mostRecentForGivenFreq.orElseThrow(() -> new NoPriceFoundException(
                    "Cannot find most recent price for " + pairName + " for " + freq.toString()
            ));

            return entry.value();
        } else {
            throw new NoPriceFoundException("Prices for " + pairName + " are not registered!");
        }
    }

    private static boolean isRecentPriceForFrequencyApprox(Frequency freq, Entry entry) {
        return entry.timestamp() % freq.milliseconds < TIME_SHIFT_TOLLERANCE_IN_MILLIS;
    }

    @Override
    public Optional<List<Entry>> getCandleBuffer(int size, Frequency freq, String pairName) {
        return null;
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
        scheduledExecutorService.scheduleAtFixedRate(this::updateStrategies, 0, 5, TimeUnit.SECONDS);
    }

    protected void updateStrategies() {
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
        strategies.forEach((strategyName, strategy) -> {
            strValuesForSpecificPair.put(strategyName, strategy.calculate(Optional.ofNullable(entry.value()).orElse(BigDecimal.ZERO)));
        });
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
}
