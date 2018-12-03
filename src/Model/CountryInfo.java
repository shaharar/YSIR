package Model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class CountryInfo {
    private String countryName;
    private String currency;
    private String population;
    private String capitalCity;
//    private HashMap<String,ArrayList <Integer>> positionsInDocs;

    public CountryInfo(JSONObject cityInfo) {
        this.countryName = cityInfo.get("name").toString();
        this.currency = cityInfo.getJSONArray("currencies").getJSONObject(0).get("name").toString();
        this.population = parse(cityInfo.get("population").toString());
        this.capitalCity = cityInfo.get("capital").toString();
//        this.positionsInDocs = new HashMap<>();
    }

//    public CountryInfo(String countryName, String currency, String population) {
//        this.countryName = countryName;
//        this.currency = currency;
//        this.population = parse(population);
////        this.positionsInDocs = positionsInDocs;
//    }

    public String getCountryName() {
        return countryName;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPopulation() {
        return population;
    }

//    public HashMap<String, ArrayList <Integer>> getPositionsInDocs() {
//        return positionsInDocs;
//    }

    private String parse(String string) {
            double num;
            String parseStr = "";
            try{
                num = Double.parseDouble(string.replace(",",""));
            } catch (Exception e){
                return parseStr;
            }
                //num between 1,000 to 999,000
                if (num < 1000000) {
                    parseStr = (num / 1000) + "K";
                }
                //num between 1,000,000 to 999,000,000
                else if (num < 1000000000) {

                    parseStr = (num / 1000000) + "M";
                }
                //num up to 1,000,000,000
                else {
                    parseStr = (num / 1000000000) + "B";
                }
//            }
            return parseStr;
        }

    public String getCapitalCity() {
        return capitalCity;
    }
}
