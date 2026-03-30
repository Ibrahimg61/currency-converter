import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.JSONObject;

public class currency_Import {
    public static void main(String[] args) throws Exception {
        
        String jsonUrl = "https://open.er-api.com/v6/latest/EUR";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(jsonUrl))
                .build();

            System.out.println("Lade Wechselkurse von GitHub...");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

                // Speichern der JSON-Daten in einer Datei
            Path originalerPath = Paths.get("currency_original.json");
            Files.writeString(originalerPath, responseBody);
            System.out.println("Originaldatei gespeichert unter: " + originalerPath.toAbsolutePath());


            JSONObject jsonDaten = new JSONObject(responseBody);
            JSONObject rates = jsonDaten.getJSONObject("rates");


            double usdKurs = rates.getDouble("USD");
            System.out.println("Der aktuelle Kurs für 1 EUR in USD ist: " + usdKurs);

            double chfKurs = rates.getDouble("CHF"); 
            System.out.println("Der aktuelle Kurs für 1 EUR in CHF ist: " + chfKurs);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
