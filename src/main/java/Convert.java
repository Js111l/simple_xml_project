import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class Convert {
    private Convert() {
    }

    static public String fromTo(String from, String to, String amount) {
        HttpResponse<String> response = null;
        try {
            amount = new StringBuilder(amount).deleteCharAt(amount.length() - 1).toString().trim();
            String url_str = "https://api.exchangerate.host/latest?base=" + from;
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url_str)).GET().build();
            response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject object = new JSONObject(response.body());
        JSONObject rates = (JSONObject) object.get("rates");
        BigDecimal priceToConvert = rates.getBigDecimal(to);
        return priceToConvert.multiply(new BigDecimal(amount)).toPlainString();
    }
}
