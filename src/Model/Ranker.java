package Model;

import javafx.util.Pair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;



public class Ranker {
    private double k;
    private double b;
    private PriorityQueue <Pair <String, Double>> docsRanks;
    private HashMap <String, ArrayList <String>> queryResults;

    public Ranker (){
//        docsRanks = new TreeMap<>();
        docsRanks = new PriorityQueue<>(new Comparator<Pair<String, Double>>() {
            @Override
            public int compare(Pair<String, Double> p1, Pair<String, Double> p2) {
                return (p1.getValue().compareTo(p2.getValue())) * -1;
            }
        });
        queryResults = new HashMap<>();
        b = 0.3;
        k = 1.2;
    }
    public void rank(HashMap<String, HashMap<String, Integer>> docsResults, HashSet<String> docsOfChosenCities, HashMap<String, Integer> queryTerms, HashMap<String, ArrayList<Integer>> dictionary, HashMap<String, Integer> docsInfo, HashMap<String, HashMap<String, Integer>> entities, String queryId) {
        int totalDocsLengths = 0, N;
        double avdl;
        for (Integer docLength: docsInfo.values()) {
            totalDocsLengths += docLength;
        }
        N = docsInfo.size();
        avdl = totalDocsLengths / N;
        for (String docId: docsResults.keySet()) {
            double rank = 0;
            if(docsOfChosenCities.isEmpty() || (!docsOfChosenCities.isEmpty() && docsOfChosenCities.contains(docId))) {
                double bm25 = 0;
                double entitiesScore = 1;
//                double firstQueryTermScore = 0;
//                double cosSimilarity = 0;
//                double cosSimilarityNumerator = 0;
//                double docTermsWeights = 0;
//                if (weightsPerDoc.containsKey(docId)){
//                    docTermsWeights = weightsPerDoc.get(docId);
//                }
                double queryTermsWeights = 0;
                int docTf = 0, queryTf = 0, df = 0, docLength = 0;
                for (String term : queryTerms.keySet()) {
                    if(docsResults.get(docId).containsKey(term)) {
                        docTf = docsResults.get(docId).get(term);
                        queryTf = queryTerms.get(term);
                        df = dictionary.get(term).get(1);
                        docLength = docsInfo.get(docId);
//                        cosSimilarityNumerator += (docTf / docLength) * (Math.log10(N / df));
//                        queryTermsWeights += Math.pow(1, 2);
                        if (entities.containsKey(docId) && entities.get(docId).containsKey(term.toUpperCase()) && queryTf > 1){
                            //----------------------------------------------------------------------------------------complete
                            entitiesScore = entitiesScore * queryTf;
                        }

                        bm25 += queryTf * (((k + 1) * docTf) / (docTf + k * (1 - b + b * (docLength / avdl)))) * Math.log10((N + 1)/ df);
                    }
                }
//                if (docTermsWeights == 0){
//                    cosSimilarity = 0;
//                }
//                else{
//                    cosSimilarity = cosSimilarityNumerator / (Math.sqrt(docTermsWeights * queryTermsWeights));
//                }
//                if (0.5 * rank + 0.5 * cosSimilarity > 0) {
//                    docsRanks.add(new Pair<>(docId, 0.5 * rank + 0.5 * cosSimilarity));
                rank = 0.95 * bm25 + 0.05 * entitiesScore ;
                docsRanks.add(new Pair<>(docId, rank));
//                }
            }
        }

        ArrayList <String> docsId = new ArrayList<>();
        for (int i = 0 ; i < 50; i++){
            if(i < docsRanks.size()) {
                docsId.add(docsRanks.poll().getKey());
            }
        }
        if (queryId.endsWith(" ")){
            queryId = queryId.substring(0, queryId.length() - 1);
        }
        queryResults.put(queryId, docsId);

        docsRanks.clear();
    }


    public void writeResultsToDisk(String saveResultsPath){
        PriorityQueue <String> sortedQueryIDs = new PriorityQueue<>();
        for (String queryID: queryResults.keySet()) {
            sortedQueryIDs.add(queryID);
        }
        StringBuilder sb = new StringBuilder();
        while(sortedQueryIDs.size() > 0){
            String queryId = sortedQueryIDs.poll();
            for (String docId: queryResults.get(queryId)) {
                sb.append(queryId + " 0 " + docId + " 1 42.38 mt\n");
            }
        }
        File rankerResults = new File(saveResultsPath + "\\results.txt");
        try {
            rankerResults.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(rankerResults);
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, ArrayList<String>> getQueryResults() {
        return queryResults;
    }
}
