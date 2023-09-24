package analysis.technical;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

interface Scrapper {
    Map<String, Optional<BigDecimal>> getCurrentPrices();
    void start();
    void stop();
}
