package Model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class Ranker {
    private double avdl;
    private int N;
    private double k;
    private double b;
    private TreeMap <String, Double> docsRanks;

    public Ranker (int docsInCollection, int docsTotalLengthes){
        avdl = docsTotalLengthes / docsInCollection;
        N = docsInCollection;
        docsRanks = new TreeMap<>();
    }
    public void rank(HashMap<String, String> docsResults, HashMap<String, Integer> queryTerms, String queryId, String queryDescription) {
        for (String docId: docsResults.keySet()) {
            double rank;
            int docTf, queryTf, df, docLength;
        }
    }
}
