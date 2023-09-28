package analysis.technical;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

interface Scrapper extends Scheduledable {
    ConcurrentLinkedQueue<Entry> getCurrentPrices();
}
