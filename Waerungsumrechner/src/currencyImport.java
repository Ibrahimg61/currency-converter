import java.net.http;
import java.net.URI;
import java.net.http.*;
import java.net.*;
import java.nio.file.*;

public class currencyImport {
    
    public static void main(String[] args) {
        URI GitCurrency = URI.create("https://api.exchangerate-api.com/v4/latest/USD");

        HttpRequest request = HttpRequest.newBuilder();
        
        HttpClient client = HttpClient.newHttpClient();
            .version(Version.HTTP_2)
            .bu

    }
    
}
