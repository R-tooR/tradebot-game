package analysis.technical;

import java.math.BigDecimal;
import java.util.Map;

interface Scrapper {
    Map<String, BigDecimal> getCurrentPrices();
}
