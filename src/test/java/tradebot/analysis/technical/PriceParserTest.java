package tradebot.analysis.technical;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriceParserTest {
    @ParameterizedTest
    @CsvSource({"2.3124,2.3124", "2.333333333,2.3333", "2.312\"dsew,2.312", "5437893.321,5437893.321"})
    public void testPriceAdjustParser(String input, String expected) {
        String price = PriceParser.cleanAndParse(input);

        assertEquals(expected, price);
    }
}
