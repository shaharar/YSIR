package Model;

import java.io.*;
import java.util.*;

public class CityIndexer {
    private HashMap<String, Integer> citiesDictionary;
    private HTTPRequest httpRequest;
    private CountriesCollection countries;
    private String path;

    public CityIndexer(String path) throws IOException {
        citiesDictionary = new HashMap<>();
        this.path = path;
        httpRequest = new HTTPRequest("https://restcountries.eu/rest/v2/all");
        countries = new CountriesCollection(httpRequest.getJsonObj());
        new File(this.path + "\\cityIndexResults").mkdir();
        try {
            (new File(path + "\\cityIndexResults\\posting_city.txt")).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void index(HashMap<String , ArrayList <String>> cityDocs) {
        ArrayList<String> listPosting = new ArrayList<>();
        StringBuilder docsListStr, strPosting;
        Integer pointer;

        //read posting file from disk, and insert it's lines to list
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(path + "\\cityIndexResults\\posting_city.txt")));
            String line = "";
            while ((line = (br.readLine())) != null) {
                listPosting.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int currIdx = listPosting.size() + 1; // the next free index in the listPosting

        for (String city : cityDocs.keySet()) {
            if (!citiesDictionary.containsKey(city.toUpperCase())) {
                String countryName;
                String currency;
                String population;
                if (countries.getCountryInfo(city.toUpperCase()) != null) {
                    CountryInfo country = countries.getCountryInfo(city.toUpperCase());
                    countryName = country.getCountryName();
                    currency = country.getCurrency();
                    population = country.getPopulation();
                } else {
                    countryName = "";
                    currency = "";
                    population = "";
                }
//                HashMap<String, ArrayList<Integer>> positionsInDocs = new HashMap<>();
//                positionsInDocs.putAll(citiesPositions.get(city.toUpperCase()));
                docsListStr = new StringBuilder();
//                for (String docID : positionsInDocs.keySet()) {
//                    docsListStr.append(docID + " : ");
//                    ArrayList<Integer> positions = positionsInDocs.get(docID);
//                    for (Integer position : positions) {
//                        docsListStr.append(position + " ");
//                    }
//                }
                ArrayList <String> docs = cityDocs.get(city);
                for (int i = 0; i < docs.size(); i++){
                    docsListStr.append(docs.get(i) + " ");
                }
                pointer = currIdx;
                currIdx++;
                citiesDictionary.put(city.toUpperCase(), pointer);
                listPosting.add(city.toUpperCase() + " " + "country name: " + countryName + " " + "currency: " + currency + " " + "population: " + population + " " + docsListStr);
            } else {
//                HashMap<String, ArrayList<Integer>> positionsInDocs = new HashMap<>();
//                positionsInDocs.putAll(citiesPositions.get(city.toUpperCase()));
                pointer = citiesDictionary.get(city.toUpperCase());
                docsListStr = new StringBuilder();
                ArrayList <String> docs = cityDocs.get(city);
//                for (String docID : positionsInDocs.keySet()) {
//                    docsListStr.append(docID + " : ");
//                    ArrayList<Integer> positions = positionsInDocs.get(docID);
//                    for (Integer position : positions) {
//                        docsListStr.append(position + " ");
//                    }
//                }
                for (int i = 0; i < docs.size(); i++){
                    docsListStr.append(docs.get(i) + " ");
                }
                listPosting.set(pointer - 1, listPosting.get(pointer - 1) + docsListStr);
            }
        }

        strPosting = new StringBuilder();

        for (String postingRec : listPosting) {
            strPosting.append(postingRec + "\n");
        }

        listPosting.clear();

        // create file writer
        FileWriter fwPosting = null;
        try {
            fwPosting = new FileWriter(new File(path + "\\cityIndexResults\\posting_city.txt"));
            fwPosting.write(strPosting.toString());
            fwPosting.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void writeDictionaryToDisk() {

        StringBuilder sb = new StringBuilder();
        ArrayList <String> strList = new ArrayList<>();
        for (String termStr: citiesDictionary.keySet()) {
            strList.add(termStr);
        }
        Collections.sort(strList);
        for (String cityStr : strList) {
            if (cityStr.length() == 0){
                break;
            }
            sb.append(cityStr + " : " + citiesDictionary.get(cityStr)).append("\n");
        }
        File dictionary = new File(path + "\\cityIndexResults\\citiesDictionary.txt");
        try {
            dictionary.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(dictionary);
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finished(HashMap<String , ArrayList <String>> cityDocs) {
        index(cityDocs);
        writeDictionaryToDisk();
    }
}


















//            if (cities.containsKey(city.toUpperCase())){
//                HashMap<String, ArrayList <Integer>> positionsInDocs = cities.get(city.toUpperCase()).getPositionsInDocs();
//                positionsInDocs.putAll(citiesPositions.get(city.toUpperCase()));
//
//            }
//            else{
//                CountryInfo country = countries.getCountries().get(city.toUpperCase());
//                String countryName = country.getCountryName();
//                String currency = country.getCurrency();
//                String population = country.getPopulation();
//                HashMap<String, ArrayList <Integer>> positionsInDocs = new HashMap<>();
//                positionsInDocs.putAll(citiesPositions.get(city.toUpperCase()));
//                CountryInfo countryInfo = new CountryInfo(countryName, currency, population, positionsInDocs);
//            }




