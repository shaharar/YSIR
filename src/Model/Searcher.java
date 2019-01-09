package Model;

import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Searcher {

    private Parse parser;
    private HashMap<String, HashMap<String, Integer>> docsResults;
    private HashMap<String, Integer> docsInfo;
    private HashMap<String, HashMap<String, Integer>> entities;
    static int queryID = 1;
    private WordsSemantic ws;

    /**
     * constructor
     */
    public Searcher() {
        docsInfo = new HashMap<>();
        entities = new HashMap<>();
        ws = new WordsSemantic();
    }

    /**
     * the following function searches for relevant documents (documents in which query terms appear)
     * @param indexer
     * @param cityIndexer
     * @param ranker
     * @param query
     * @param withSemantic
     * @param chosenCities
     * @param citiesByTag
     * @param withStemming
     * @param saveInPath
     * @param queryId
     * @param queryDescription
     */
    public void search(Indexer indexer, CityIndexer cityIndexer, Ranker ranker, String query, boolean withSemantic, ArrayList<String> chosenCities, ObservableList<String> citiesByTag, boolean withStemming, String saveInPath, String queryId, String queryDescription) {
        String[] originalQueryTerms = query.split(" ");
        String originQuery = query;
        docsResults = new HashMap<>();
        parser = new Parse(withStemming, saveInPath, saveInPath);
        HashSet<String> docsOfChosenCities = new HashSet<>();
        query = "" + query + queryDescription;
        HashMap<String, Integer> queryTerms = parser.parseQuery(query);
        HashMap<String, ArrayList<Integer>> dictionary = indexer.getDictionary();
        if (!withStemming){
            setDocsInfo(saveInPath + "\\docsInformation.txt");
        }
        else{
            setDocsInfo(saveInPath + "\\docsInformation_stemming.txt");
        }


        //add semantic words of each term in query to 'queryTerms'
        if(withSemantic) {
            HashMap<String,ArrayList<String>> semanticWords = ws.connectToApi(originQuery);
        }

        //give an ID to query if it's a regular query (not queries file)
        if(queryId.equals("")){
            queryId = "" + Searcher.queryID;
            Searcher.queryID++;
        }

        String postingDir;
        if (!withStemming) {
            postingDir = "\\indexResults\\postingFiles";
        } else {
            postingDir = "\\indexResults\\postingFiles_Stemming";
        }
        int pointer = 0;

        //find docs that contain the terms in the query in their text
        HashMap<String, Integer> queryTermsIgnoreCase = new HashMap<>();
        for (String term : queryTerms.keySet()) {
            String originTerm = term;
            if (!dictionary.containsKey(term.toUpperCase()) && !dictionary.containsKey(term.toLowerCase())) {
                continue;
            }
            if(dictionary.containsKey(term.toLowerCase())){
                term = term.toLowerCase();
            }
            else {
                term = term.toUpperCase();
            }
            queryTermsIgnoreCase.put(term,queryTerms.get(originTerm));
            pointer = dictionary.get(term).get(2);
            String chunk = ("" + term.charAt(0)).toUpperCase();

            //get the relevant line from posting file
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(new File(saveInPath + postingDir + "\\posting_" + chunk + ".txt")));
                String line = "";
                int i = 1;
                while ((line = (br.readLine())) != null) {
                    if (i == pointer) {
                        break;
                    }
                    i++;
                }
                br.close();

                //get docs from posting line and add them to the data structure 'docsResults'
                if(line != null) {
                    findDocsFromLine(line, term);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //find docs that contain the chosen cities in their text
        for (String cityTerm : chosenCities) {
            if (!dictionary.containsKey(cityTerm) && !dictionary.containsKey(cityTerm.toLowerCase())) {
                continue;
            }
            if(dictionary.containsKey(cityTerm.toLowerCase())){
                cityTerm = cityTerm.toLowerCase();
            }
            pointer = dictionary.get(cityTerm).get(2);
            // char chunk = indexer.classifyToPosting(term).charAt(0);
            String chunk = ("" + cityTerm.charAt(0)).toUpperCase();

            //get the relevant line from posting file
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(new File(saveInPath + postingDir + "\\posting_" + chunk + ".txt")));
                String line = "";
                int i = 1;
                while ((line = (br.readLine())) != null) {
                    if (i == pointer) {
                        break;
                    }
                    i++;
                }
                br.close();

                //get docs from posting line and add them to the data structure 'docsOfChosenCities'
                String docs = line.substring(0, line.indexOf("[") - 1); //get 'docsListStr'
                String[] docsArr = docs.split(";");
                for (String docInfo : docsArr) {
                    String doc = docInfo.substring(0, docInfo.indexOf(": "));
                    while(doc.charAt(0) == ' '){
                        doc = doc.substring(1);
                    }
                    docsOfChosenCities.add(doc);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //find docs that contain the chosen cities in their city tag
        if(!chosenCities.isEmpty()){
            for (String cityDicRec: chosenCities) {
                //get pointer to posting from cityDictionary
                try {
                    pointer = cityIndexer.getCitiesDictionary().get(cityDicRec);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                //get the relevant line from posting file
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(new File(saveInPath + "\\cityIndexResults" + "\\posting_city" + ".txt")));
                    String line = "";
                    int i = 1;
                    while ((line = (br.readLine())) != null) {
                        if (i == pointer) {
                            break;
                        }
                        i++;
                    }
                    br.close();

                    //get docs from posting line and add them to the data structure 'docsOfChosenCities'
                    String docs = line.substring(line.indexOf("[") + 1, line.indexOf("]")); //get 'docsListStr'
                    String[] docsArr = docs.split("; ");
                    for (String doc : docsArr) {
                        docsOfChosenCities.add(doc);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        ranker.rank(docsResults, docsOfChosenCities, queryTermsIgnoreCase, dictionary, docsInfo,entities, queryId);
    }

    /**
     * the following function reads relevant data from posting files
     * @param line
     * @param term
     */
    private void findDocsFromLine(String line, String term) {
        String docs = line.substring(0, line.indexOf("[") - 1); //get 'docsListStr'
        String[] docsArr = docs.split(";");
        for (String docInfo : docsArr) {
            String doc = docInfo.substring(0, docInfo.indexOf(": "));
            while(doc.charAt(0) == ' '){
                doc = doc.substring(1);
            }
            String tf = docInfo.substring(docInfo.indexOf(":") + 2);
            if (!docsResults.containsKey(doc)) {
                HashMap<String, Integer> termsTf = new HashMap<>();
                try{
                    termsTf.put(term, Integer.parseInt(tf));
                }
                catch (NumberFormatException e){
                    e.printStackTrace();
                }
                docsResults.put(doc, termsTf);
            } else {
                HashMap<String, Integer> termsTf = docsResults.get(doc);
                HashMap<String, Integer> newTermsTf = termsTf;
                newTermsTf.put(term, Integer.parseInt(tf));
                docsResults.replace(doc, termsTf, newTermsTf);
            }
        }
    }

    /**
     * the following function separates queries in queries document by labels
     * @param indexer
     * @param cityIndexer
     * @param ranker
     * @param queriesFile
     * @param withSemantic
     * @param chosenCities
     * @param citiesByTag
     * @param withStemming
     * @param saveInPath
     */
    public void separateFileToQueries(Indexer indexer, CityIndexer cityIndexer, Ranker ranker, File queriesFile, boolean withSemantic, ArrayList<String> chosenCities, ObservableList<String> citiesByTag, boolean withStemming, String saveInPath) {
        try {
            String allQueries = new String(Files.readAllBytes(Paths.get(queriesFile.getAbsolutePath())), Charset.defaultCharset());
            String[] allQueriesArr = allQueries.split("<top>");

            for (String query : allQueriesArr) {
                if(query.equals("")){
                    continue;
                }
                String queryId = "", queryText = "", queryDescription = "";
                String[] lines = query.toString().split("\n");
                for (int i = 0; i < lines.length; i++){
                    if(lines[i].contains("<num>")){
                        queryId = lines[i].substring(lines[i].indexOf(":") + 2);
                    }
                    else if(lines[i].contains("<title>")){
                        queryText = lines[i].substring(8);
                    }
                    else if(lines[i].contains("<desc>")){
                        i++;
                        while(i < lines.length && !lines[i].equals("")){
                            queryDescription += lines[i];
                            i++;
                        }
                    }
                }
                search(indexer, cityIndexer, ranker, queryText, withSemantic, chosenCities, citiesByTag, withStemming, saveInPath, queryId, queryDescription);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * the following function reads relevant data from documents information file
     * @param path
     */
    public void setDocsInfo(String path) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(path)));
            String line = "";
            while ((line = (br.readLine())) != null) {
                String [] splitLineByDelimiter = line.split(", ");
                String docId = splitLineByDelimiter[0].substring(0, splitLineByDelimiter[0].indexOf(":"));
                String docLength = splitLineByDelimiter[1];
                docsInfo.put(docId, Integer.parseInt(docLength));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * the following function reads relevant data from entities file
     * @param entitiesFile
     */
    public void loadEntities (File entitiesFile){
        BufferedReader br = null;
        int lineNum = 0;
        try {
            String term;
            br = new BufferedReader(new FileReader(entitiesFile));
            String line = "";
            while ((line = (br.readLine())) != null) {
                lineNum++;
                if (!line.contains("--noEntities--")) {
                    String docId = line.substring(0, line.indexOf(":"));
                    String [] entitiesStr = line.split("; ");
                    if (entitiesStr.length > 0){
                        entitiesStr[0] = entitiesStr[0].substring(line.indexOf(":") + 2);
                    }
                    HashMap <String, Integer> entitiesInfo = new HashMap<>();
                    for (int i = 0; i < entitiesStr.length ; i++){
                        String [] entitiesRankStr = entitiesStr[i].split(" - ");
                        if (entitiesRankStr.length < 2){
                            System.out.println(lineNum + " " + entitiesRankStr[0]);
                        }
                        entitiesInfo.put(entitiesRankStr[0], Integer.parseInt(entitiesRankStr[1]));
                    }
                    entities.put(docId, entitiesInfo);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * getter
     * @return
     */
    public HashMap<String, HashMap<String, Integer>> getEntities() {
        return entities;
    }
}
