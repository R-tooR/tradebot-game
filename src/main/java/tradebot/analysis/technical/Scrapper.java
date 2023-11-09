package tradebot.analysis.technical;

import java.util.concurrent.ConcurrentLinkedQueue;

interface Scrapper extends Scheduledable {
    ConcurrentLinkedQueue<Entry> getCurrentPrices();
}
