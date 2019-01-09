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

    /*
    constructor
     */
    public Ranker (){
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

    /**
     * the following function ranks relevant docs for the given query. only 50 documents or less would be retrieved
     * @param docsResults
     * @param docsOfChosenCities
     * @param queryTerms
     * @param dictionary
     * @param docsInfo
     * @param entities
     * @param queryId
     */
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
                double queryTermsWeights = 0;
                int docTf = 0, queryTf = 0, df = 0, docLength = 0;
                for (String term : queryTerms.keySet()) {
                    if(docsResults.get(docId).containsKey(term)) {
                        docTf = docsResults.get(docId).get(term);
                        queryTf = queryTerms.get(term);
                        df = dictionary.get(term).get(1);
                        docLength = docsInfo.get(docId);
                        if (entities.containsKey(docId) && entities.get(docId).containsKey(term.toUpperCase()) && queryTf > 1){
                            entitiesScore = entitiesScore * queryTf;
                        }

                        bm25 += queryTf * (((k + 1) * docTf) / (docTf + k * (1 - b + b * (docLength / avdl)))) * Math.log10((N + 1)/ df);
                    }
                }
                rank = 0.95 * bm25 + 0.05 * entitiesScore ;
                docsRanks.add(new Pair<>(docId, rank));
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


    /**
     * the following function writes retrieval results to disk
     * @param saveResultsPath
     */
    public void writeResultsToDisk(String saveResultsPath){
       // PriorityQueue <String> sortedQueryIDs = new PriorityQueue<>();
        PriorityQueue <Integer> sortedQueryIDs = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        HashMap<String, ArrayList<String>> newQueryResults = new HashMap<>();
        for (String qID: queryResults.keySet()) {
            String queryIDStr = "";
            for(char c : qID.toCharArray()){
                if(Character.isDigit(c)){
                    queryIDStr = queryIDStr + c;
                }
            }
            newQueryResults.put(queryIDStr, queryResults.get(qID));
        }

/*        for (String queryID: queryResults.keySet()) {
            // sortedQueryIDs.add(queryID);
            sortedQueryIDs.add(Integer.parseInt(queryID));
        }*/

        for (String qID: newQueryResults.keySet()) {
            sortedQueryIDs.add(Integer.parseInt(qID));
        }

        StringBuilder sb = new StringBuilder();
        while(sortedQueryIDs.size() > 0){
            //String queryId = sortedQueryIDs.poll();
            Integer queryId = sortedQueryIDs.poll();
            for (String docId: newQueryResults.get(queryId.toString())) {
               // sb.append(queryId + " 0 " + docId + " 1 42.38 mt\n");
                sb.append(queryId.toString() + " 0 " + docId + " 1 42.38 mt\n");
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

    /**
     * getter
     * @return
     */
    public HashMap<String, ArrayList<String>> getQueryResults() {
        return queryResults;
    }
}
