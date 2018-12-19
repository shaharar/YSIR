package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Searcher {

    Parse parser;
    Indexer indexer;
    Ranker ranker;
    HashMap<String,String> docsResults;

    public Searcher() {
        docsResults = new HashMap<>();
    }

    public void search (String query, boolean withStemming, String saveInPath, String corpusPath, String queryId, String queryDescription){
        parser = new Parse(withStemming,saveInPath,corpusPath);
        indexer = new Indexer(saveInPath,withStemming);
        ranker = new Ranker(parser.getDocsInCollection(), parser.getDocsTotalLengthes());
        HashMap<String,Integer> terms = parser.parseQuery(query);

        HashMap <String, ArrayList<Integer>> dictionary = indexer.getDictionary();
        int pointer = 0;
        for (String term: terms.keySet()) {
            if(!dictionary.containsKey(term)){
                continue;
            }
            pointer = dictionary.get(term).get(0);
            //char chunk = term.charAt(0);
            char chunk = indexer.classifyToPosting(term).charAt(0);

            //get the relevant line from posting file
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(new File(saveInPath + indexer.getPostingDir() + "\\posting_" + chunk + ".txt")));
                String line = "";
                int i = 1;
                while ((line = (br.readLine())) != null) {
                    if(i == pointer){
                        break;
                    }
                    i++;
                }
                br.close();

                //get docs from posting line and add them to the data structure 'docsResults'
                findDocsFromLine(line, term);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ranker.rank(docsResults, queryId, queryDescription);

    }

    private void findDocsFromLine(String line, String term) {
        String docs = line.substring(0,line.indexOf("[") - 1); //get 'docsListStr'
        String[] docsArr = docs.split(", ");
        for (String doc:docsArr) {
            if(!docsResults.containsKey(doc)){
                docsResults.put(doc,term);
            }
            else{
                String termsInDoc = docsResults.get(doc);
                docsResults.replace(doc, termsInDoc + "|" + term);
            }
        }
    }


    private void separateFileToQueries(String path, boolean withStemming, String saveInPath, String corpusPath){
        File queriesFile = new File(path);
        try {
            org.jsoup.nodes.Document document = Jsoup.parse(queriesFile,"UTF-8");
            org.jsoup.select.Elements elements = document.getElementsByTag("top");
            for (Element e: elements) {
                String queryText = e.select("title").text();
                String queryId = e.select("num").text();
                String queryDescription = e.select("desc").text();
                search(queryText, withStemming, saveInPath, corpusPath, queryId, queryDescription);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
