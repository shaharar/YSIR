package Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class CountriesCollection {
    private HashMap <String, CountryInfo> countries;

    public CountriesCollection(JSONObject countriesInfo) {
        this.countries = new HashMap();
        JSONArray info = countriesInfo.getJSONArray("result");
        for (Object obj:info) {
            JSONObject data = (JSONObject) obj;
            CountryInfo country = new CountryInfo(data);
            countries.put(country.getCapitalCity(), country);
        }


    }

    public HashMap<String, CountryInfo> getCountries() {
        return countries;
    }

    public CountryInfo getCountryInfo (String capitalCity){
        return countries.get(capitalCity);
    }
}
