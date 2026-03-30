import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

private static final String address = "https://api.exchangerate-api.com/v4/latest/EUR";

public class import {
   

CloseableHttpClient httpClient = HttpClients.createDefault();
HttpGet request = new HttpGet(address);
CloseableHttpResponse response = httpClient.execute(request);
HttpEntity entity = response.getEntity();
String result = EntityUtils.toString(entity);


}