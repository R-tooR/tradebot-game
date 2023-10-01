package analysis.technical;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.ClientProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public class GoogleFinanceScrapper implements Scrapper {
    private static final String URL_addr_prefix = "https://www.google.com/finance/quote/";

    private final Client client;

    private final ConcurrentLinkedQueue<Entry> currentStockPrices;
    private final List<String> pairs;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;

    public GoogleFinanceScrapper(List<String> pairs, ConcurrentLinkedQueue<Entry> currentStockPrices, ExecutorService executor) {
        this.currentStockPrices = currentStockPrices;
        this.pairs = pairs;
        ClientBuilder.newBuilder();
        client = ClientBuilder.newClient();
        executorService = executor;
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    private ConcurrentHashMap<String, Optional<BigDecimal>> initializeMapWithKeys(List<String> pairs) {
        ConcurrentHashMap<String, Optional<BigDecimal>> map = new ConcurrentHashMap<>();
        for(String pair: pairs) {
            map.put(pair, Optional.<BigDecimal>empty());
        }

        return map;
    }

    //todo jsaction="click:upfQVb"
    @Override
    public ConcurrentLinkedQueue<Entry> getCurrentPrices() {
        return currentStockPrices;
    }

    @Override
    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(this::updatePrices, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        scheduledExecutorService.shutdown();
    }

    private void updatePrices() {
        pairs.forEach(pair -> executorService.submit(() -> updatePrice(pair)));
    }

    private void updatePrice(String pair) {
        try {
            String currPrice = Objects.requireNonNull(getCurrentPriceForIndex(pair));
            currentStockPrices.offer(
                    new Entry(Instant.now().toEpochMilli(), pair, new BigDecimal(PriceParser.cleanAndParse(currPrice)))
            );
        } catch (IOException e) {
            System.out.println("Failed to update price for " + pair);
        }
    }

    private String getCurrentPriceForIndex(String index) throws IOException {
        WebTarget target = client.target(URL_addr_prefix + index);
        target.property(ClientProperties.HTTP_AUTOREDIRECT_PROP, Boolean.TRUE);
        Invocation.Builder builder = target.request();
        Response r = builder.get();

        String input;
        try(BufferedReader b = new BufferedReader(new InputStreamReader((InputStream) r.getEntity()))){
            while ((input = b.readLine()) != null) {
                if(input.startsWith("</script><c-wiz jsrenderer=\"jY5r6b\"")) { // przyspiesza, ew. jak się content strony zmienia, to można wyszukać, gdzie znajduje się fragment z interesującymi nas informacjami :)
                    int idx = input.indexOf("data-last-price");
                    if(idx != -1)
                        return input.substring(idx + 17, idx + 25);
                }
            }
        }
        return null;
    }

}
