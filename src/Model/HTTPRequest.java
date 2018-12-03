package Model;

import org.json.JSONObject;
import org.jsoup.helper.HttpConnection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class HTTPRequest {
    private JSONObject jsonObj;

    public HTTPRequest(String urlAddress) throws IOException {
        URL url = new URL(urlAddress);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");
        httpConn.setRequestProperty("Accept", "application/json");
        String json = "{\"result\":";
        Scanner scanner = new Scanner(url.openStream());
        while (scanner.hasNext()) {
            json += scanner.nextLine();
        }
        scanner.close();
        json += "}";
        jsonObj = new JSONObject(json);
    }

    public JSONObject getJsonObj() {
        return jsonObj;
    }
}

