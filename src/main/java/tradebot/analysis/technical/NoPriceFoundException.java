package tradebot.analysis.technical;

import java.io.Serial;

public class NoPriceFoundException extends Exception {

    @Serial
    private static final long serialVersionUID = 124123532L;

    public NoPriceFoundException(String message) {
        super(message);
    }
}
