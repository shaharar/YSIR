package Model;

import java.util.HashMap;


public class City {
    String state;
    String currency;
    String population;
    HashMap<String,Integer> positionsInDocs;

    public City(String state, String currency, String population) {
        this.state = state;
        this.currency = currency;
        this.population = population;
        positionsInDocs = new HashMap<>();
    }
}
