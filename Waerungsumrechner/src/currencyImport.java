import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.*;
import java.nio.file.*;

public class currencyImport {
    
    public static void main(String[] args) {
        URI GitCurrency = URI.create("https://api.exchangerate-api.com/v4/latest/USD");

        HttpRequest request = HttpRequest.newBuilder()
            .uri(GitCurrency)
            .GET()
            .build();
        
        HttpClient client = HttpClient.newHttpClient();

    }
    
}
