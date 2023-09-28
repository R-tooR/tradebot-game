package analysis.technical;

import java.math.BigDecimal;

public record Entry(long timestamp, String pairName, BigDecimal value) {
    public static Entry emptyEntry = new Entry(-1L, "EMPTY", BigDecimal.ZERO);
}
