package test.scrapping;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.ClientProperties;
import org.apache.cxf.jaxrs.client.spec.ClientBuilderImpl;
import org.apache.cxf.transport.http.HttpClientHTTPConduit;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class GoogleScrapper {
    private static final String URL_addr = "https://www.google.com/finance/quote/EUR-PLN";

    private final Client client;

    public GoogleScrapper() {
        client = ClientBuilder.newBuilder().newClient();

    }

    //todo jsaction="click:upfQVb"
    public String getCurrentPriceWithJAX_RS() throws IOException {
        WebTarget target = client.target(URL_addr);
        target.property(ClientProperties.HTTP_AUTOREDIRECT_PROP, Boolean.TRUE);
        Invocation.Builder builder = target.request();
        Response r = builder.get();

        String input;
        try(BufferedReader b = new BufferedReader(new InputStreamReader((InputStream) r.getEntity()))){
            while ((input = b.readLine()) != null) {
                if(input.startsWith("</script><c-wiz jsrenderer=\"jY5r6b\"")) { // przyspiesza, ew. jak się content strony zmienia, to można wyszukać, gdzie znajduje się fragment z interesującymi nas informacjami :)
                    int idx = input.indexOf("data-last-price");
                    if(idx != -1)
                        return input.substring(idx + 17, idx + 25);
                }
            }
        }
        return "";
    }

    public String getCurrentPrice() throws IOException {
//        Document doc = Jsoup.connect(URL).get();
        URL url = new URL(URL_addr);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        BufferedReader br =
                new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

        String input;
        String output = "";

        while ((input = br.readLine()) != null) {
            if(input.startsWith("</script><c-wiz jsrenderer=\"jY5r6b\"")) { // przyspiesza, ew. jak się content strony zmienia, to można wyszukać, gdzie znajduje się fragment z interesującymi nas informacjami :)
//                System.out.println(input.trim());
                int idx = input.indexOf("data-last-price");
                if(idx != -1)
                    output = input.substring(idx + 17, idx + 25);
            }

        }
        br.close();

        return output;
    }
}
