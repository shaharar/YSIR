package Model;

import javafx.util.Pair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;



public class Ranker {
    private double k;
    private double b;
//    private TreeMap <String, Double> docsRanks;
    private PriorityQueue <Pair <String, Double>> docsRanks;
    private HashMap <String, ArrayList <String>> queryResults;

    public Ranker (){
//        docsRanks = new TreeMap<>();
        docsRanks = new PriorityQueue<>(new Comparator<Pair<String, Double>>() {
            @Override
            public int compare(Pair<String, Double> p1, Pair<String, Double> p2) {
                return (p1.getValue().compareTo(p2.getValue()));
            }
        });
        queryResults = new HashMap<>();
        b = 0.3;
        k = 1.2;
    }
    public void rank(HashMap<String, HashMap<String, Integer>> docsResults, HashSet<String> docsOfChosenCities, HashMap<String, Integer> queryTerms, HashMap<String, ArrayList<Integer>> dictionary, HashMap<String, Integer> docsInfo, HashMap <String, Double> weightsPerDoc, String queryId, String queryDescription, String saveInPath) {
        int totalDocsLengths = 0, N;
        double avdl;
        for (Integer docLength: docsInfo.values()) {
            totalDocsLengths += docLength;
        }
        N = docsInfo.size();
        avdl = totalDocsLengths / N;
        for (String docId: docsResults.keySet()) {
            if(docsOfChosenCities.isEmpty() || (!docsOfChosenCities.isEmpty() && docsOfChosenCities.contains(docId))) {
                double rank = 0;
                double cosSimilarity = 0;
                double cosSimilarityNumerator = 0;
                double docTermsWeights = weightsPerDoc.get(docId);
                double queryTermsWeights = 0;
                int docTf = 0, queryTf = 0, df = 0, docLength = 0;
                for (String term : queryTerms.keySet()) {
                    if(docsResults.get(docId).containsKey(term)) {
                        docTf = docsResults.get(docId).get(term);
                        queryTf = queryTerms.get(term);
                        df = dictionary.get(term).get(1);
                        docLength = docsInfo.get(docId);
                        cosSimilarityNumerator += docTf / docLength;
                        queryTermsWeights += Math.pow(1, 2);
                    }
                }
                rank += queryTf * (((k + 1) * docTf) / (docTf + k * (1 - b + b * (docLength / avdl)))) * Math.log(N / df);
                cosSimilarity = cosSimilarityNumerator / (Math.sqrt(docTermsWeights * queryTermsWeights));
                if (rank > 0) {
                    docsRanks.add(new Pair<>(docId, 0.8 * rank + 0.2 * cosSimilarity));
                }
//            docsRanks.put(docId, rank);
            }
        }

        ArrayList <String> docsId = new ArrayList<>();
        for (int i = 0 ; i < 50; i++){
            if(i < docsRanks.size()) {
                docsId.add(docsRanks.poll().getKey());
            }
        }
        queryResults.put(queryId, docsId);

        docsRanks.clear();
//        writeResultsToDisk(queryId, saveInPath, docsRanks);

        //        Map <String, Double> results = new TreeMap<>();
//        results.putAll(getTop50Docs(docsRanks));

    }

    public void displayQueryResults (){
        ArrayList <String> displayResults = new ArrayList<>();
        for (String queryId: queryResults.keySet()) {
            String line = queryId + ": ";
            for (String docId: queryResults.get(queryId)) {
                line += docId + " ";
            }
            displayResults.add(line);
        }

    }


    //    private Map<String, Double> getTop50Docs(TreeMap<String, Double> docsRanks) {
//        Comparator <String> valueComparator = new Comparator<String>() {
//            @Override
//            public int compare(String s1, String s2) {
//                return docsRanks.get(s1).compareTo(docsRanks.get(s2));
//            }
//        };
//        TreeMap <String, Double> sortedResultsByValues = new TreeMap<>(valueComparator);
//        sortedResultsByValues.putAll(docsRanks);
//        return sortedResultsByValues;
//    }
//
//    public TreeMap<String, Double> getDocsRanks() {
//        return docsRanks;
//    }

    public void writeResultsToDisk(String saveResultsPath){
        StringBuilder sb = new StringBuilder();
//        Iterator <Pair<String, Double>> it = docsRanks.iterator();
//        while (it.hasNext()) {
//            String docId = it.next().getKey();
//            sb.append(queryId + " 0 " + docId + " 1 42.38 mt\n");
//        }
        for (String queryId: queryResults.keySet()) {
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
            fw = new FileWriter(rankerResults, true);
            fw.append(sb.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
