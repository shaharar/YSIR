package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;


public class Ranker {
    private double avdl;
    private int N;
    private double k;
    private double b;
    private TreeMap <String, Double> docsRanks;

    public Ranker (){
        avdl = 0;
        N = 0;
        docsRanks = new TreeMap<>();
    }
    public void rank(HashMap<String, HashMap<String, Integer>> docsResults, HashMap<String, Integer> queryTerms, HashMap<String, ArrayList<Integer>> dictionary, String queryId, String queryDescription) {
        for (String docId: docsResults.keySet()) {
            double rank;
            int docTf, queryTf, df, docLength;
            for (String term: queryTerms.keySet()) {
                docTf = docsResults.get(docId).get(term);
                queryTf = queryTerms.get(term);
                df = dictionary.get(term).get(1);
            }
        }
    }

}
