package Model;

import javafx.util.Pair;

import java.util.*;



public class Ranker {
    private double k;
    private double b;
//    private TreeMap <String, Double> docsRanks;
    private PriorityQueue <Pair <String, Double>> docsRanks;

    public Ranker (){
//        docsRanks = new TreeMap<>();
        docsRanks = new PriorityQueue<>(new Comparator<Pair<String, Double>>() {
            @Override
            public int compare(Pair<String, Double> p1, Pair<String, Double> p2) {
                return (p1.getValue().compareTo(p2.getValue()));
            }
        });
    }
    public void rank(HashMap<String, HashMap<String, Integer>> docsResults, HashMap<String, Integer> queryTerms, HashMap<String, ArrayList<Integer>> dictionary,HashMap <String, Integer> docsInfo, String queryId, String queryDescription) {
        int totalDocsLengths = 0, N;
        double avdl;
        for (Integer docLength: docsInfo.values()) {
            totalDocsLengths += docLength;
        }
        N = docsInfo.size();
        avdl = totalDocsLengths / N;
        for (String docId: docsResults.keySet()) {
            double rank = 0;
            int docTf = 0, queryTf = 0, df = 0, docLength = 0;
            for (String term: queryTerms.keySet()) {
                docTf = docsResults.get(docId).get(term);
                queryTf = queryTerms.get(term);
                df = dictionary.get(term).get(1);
                docLength = docsInfo.get(docId);
            }
            rank += queryTf * (((k + 1) * docTf) / (docTf + k * (1 - b + b * (docLength / avdl)))) * Math.log(N / df);
            docsRanks.add(new Pair<>(docId, rank));
//            docsRanks.put(docId, rank);
        }
        while (docsRanks.size() > 50){
            docsRanks.poll();
        }
        writeResultsToDisk(queryId);

        //        Map <String, Double> results = new TreeMap<>();
//        results.putAll(getTop50Docs(docsRanks));

    }

    public PriorityQueue<Pair<String, Double>> getDocsRanks() {
        return docsRanks;
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

    public void writeResultsToDisk(String queryId){

    }
}
