package de.waehrungsumrechner.data;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/** Lädt die Kurse per HTTP von der API. */
public final class HttpRateSource implements RateSource {

    public static final URI DEFAULT_URL = URI.create("https://open.er-api.com/v6/latest/EUR");

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();
    private final URI uri;

    public HttpRateSource() {
        this(DEFAULT_URL);
    }

    public HttpRateSource(URI uri) {
        this.uri = uri;
    }

    @Override
    public String fetchLatest() throws ExchangeRateException {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(10))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ExchangeRateException("Die API antwortete mit HTTP " + response.statusCode());
            }
            return response.body();
        } catch (IOException e) {
            throw new ExchangeRateException("Kurse konnten nicht geladen werden", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExchangeRateException("Abruf der Kurse wurde unterbrochen", e);
        }
    }
}
