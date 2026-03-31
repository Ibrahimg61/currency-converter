import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.json.JSONObject;

public class currency_Import {
    public static void main(String[] args) throws Exception {
        
        String jsonUrl = "https://open.er-api.com/v6/latest/EUR";
        Path historyFile = Paths.get("currency_history.json");

        try {
            String heute = LocalDate.now().toString();

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

            JSONObject historyData;
            if (Files.exists(historyFile)) {
                String vorhandenerInhalt = Files.readString(historyFile);
                historyData = new JSONObject(vorhandenerInhalt);
            } else {
                historyData = new JSONObject();
            }

            historyData.put(heute, rates);

            Files.writeString(historyFile, historyData.toString(4));
            System.out.println("Erfolg! Die Kurse für den " + heute + " wurden in der Historie gespeichert.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
