package Model;

import java.util.HashMap;


public class Ranker {
    private double avdl;
    private int N;
    private double k;
    private double b;

    public Ranker (int docsInCollection, int docsTotalLengthes){
        avdl = docsTotalLengthes / docsInCollection;
        N = docsInCollection;
    }
    public void rank(HashMap<String, String> docsResults, String queryId, String queryDescription) {
    }
}
