package tradebot.analysis.strategies;

import java.math.BigDecimal;

public interface Strategy {
    //design this abstraction - for now, temporary workaround
    BigDecimal calculate(BigDecimal price);

    BigDecimal getCurrentValue();

}
