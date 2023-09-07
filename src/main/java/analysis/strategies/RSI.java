package analysis.strategies;

import java.math.BigDecimal;

public class RSI implements Strategy {

    private BigDecimal currentValue = BigDecimal.ZERO;
    private final Integer period;
    public RSI(int period) {
        this.period = period;
    }
    @Override
    public BigDecimal calculate(BigDecimal price) {
        currentValue = BigDecimal.valueOf(100).subtract(BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(currentValue)));
        return currentValue;
    }

    @Override
    public BigDecimal getCurrentValue() {
        return currentValue;
    }
}
