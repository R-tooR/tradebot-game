package analysis.technical;

public class PriceParser {

    /**
     *
     * @param price Scrapped price
     * @param precision Number of decimal points
     * @return Price String without non-number suffix, with maximum 'precision' decimal points
     */
    public static String cleanAndParse(String price, int precision) {
        var sb = new StringBuilder();
        char[] input = price.toCharArray();
        int i = 0;
        while (i < input.length && isNumber(input[i])) {
            sb.append(input[i]);
            ++i;
        }
        if(i < input.length && input[i] == '.') {
            sb.append(input[i]);
            ++i;
        }
        int j = 0;
        while (i < input.length && j < precision && isNumber(input[i])) {
            sb.append(input[i]);
            ++j;
            ++i;
        }
        return sb.toString();
    }

    private static boolean isNumber(char input) {
        return input >= 48 && input <= 57;
    }

    public static String cleanAndParse(String price) {
        return cleanAndParse(price, 4);
    }

}
