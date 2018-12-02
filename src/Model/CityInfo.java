package Model;


import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.helper.HttpConnection;

import java.io.IOException;
//
//public class CityInfo {
//    private JsonElement element;
//
//    public CityInfo() throws IOException {
//        OkHttpClient client = new OkHttpClient();
//        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://restcountries.eu/rest/v2/all?fields=capital;name;population;currencies").newBuilder();
//        String url = urlBuilder.build().toString();
//        HttpConnection.Request request = new Request.Builder().url(url).build();
//        Response response = client.newCall(request).execute();
//        JsonParser parser = new JsonParser();
//        element = parser.parse(response.body().string());
//    }
//
//    public String getCityInfo(String city) {
//        JsonArray cityInfoArr = element.getAsJsonArray();
//        for (int i = 0; i < cityInfoArr.size(); i++) {
//            JsonObject cityInfoObject = (JsonObject) (cityInfoArr.get(i));
//            String capitalCity = cityInfoObject.get("capital").getAsString();
//            String stateName = cityInfoObject.get("name").getAsString();
//            String cityPopulation = getPopulationRepresentation(cityInfoObject.get("population").getAsString());
//            if (capitalCity.equalsIgnoreCase(city)) {
//                JsonArray cityCurrencyArr = objectCityDetails.get("currencies").getAsJsonArray();
//                JsonObject cityCurrencyObject = (JsonObject) (cityCurrencyArr.get(0));
//                String cityCurrencyCode = cityCurrencyObject.get("code").getAsString();
//                String cityInfo = stateName + cityCurrencyCode + cityPopulation;
//                return cityInfo;
//            }
//        }
//        return "";
//    }
//
//    private String getPopulationRepresentation(String population){
//
//    }
//}
