package test.scrapping;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class GoogleScrapperTest {
    @Test
    public void testScrapper() {
        try {
            System.out.println(new GoogleScrapper().getCurrentPrice());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testScrapperJaxRs() throws IOException {
        var gs = new GoogleScrapper();

        System.out.println(gs.getCurrentPriceWithJAX_RS());
    }
}
