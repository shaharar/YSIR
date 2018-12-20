package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Searcher {

    Parse parser;
    Ranker ranker;
    HashMap<String, HashMap<String, Integer>> docsResults;
    private HashMap<String, Integer> docsInfo;
    static int queryID = 1;

    public Searcher() {
        docsInfo = new HashMap<>();
    }

    public void search(Indexer indexer, String query, boolean withStemming, String saveInPath, String corpusPath, String queryId, String queryDescription) {
        docsResults = new HashMap<>();
        parser = new Parse(withStemming, saveInPath, corpusPath);
        ranker = new Ranker();
        HashMap<String, Integer> queryTerms = parser.parseQuery(query);
        HashMap<String, ArrayList<Integer>> dictionary = indexer.getDictionary();
        setDocsInfo(saveInPath + "\\docsInformation.txt");

        String postingDir;
        if (!withStemming) {
            postingDir = "\\indexResults\\postingFiles";
        } else {
            postingDir = "\\indexResults\\postingFiles_Stemming";
        }
        int pointer = 0;
        for (String term : queryTerms.keySet()) {
            if (!dictionary.containsKey(term)) {
                continue;
            }
            pointer = dictionary.get(term).get(2);
            // char chunk = indexer.classifyToPosting(term).charAt(0);
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
                findDocsFromLine(line, term);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ranker.rank(docsResults, queryTerms, dictionary, docsInfo, queryId, queryDescription);

    }

    private void findDocsFromLine(String line, String term) {
        String docs = line.substring(0, line.indexOf("[") - 1); //get 'docsListStr'
        String[] docsArr = docs.split("; ");
        for (String docInfo : docsArr) {
            String doc = docInfo.substring(0, docInfo.indexOf(": "));
            String tf = docInfo.substring(docInfo.indexOf(":") + 2);
/*            if(!docsResults.containsKey(doc)){
                docsResults.put(doc,term + " -" + tf);
            }
            else{
                String termsInDoc = docsResults.get(doc);
                docsResults.replace(doc, termsInDoc + "|" + term + " -" + tf);
            }*/
            if (!docsResults.containsKey(doc)) {
                HashMap<String, Integer> termsTf = new HashMap<>();
                termsTf.put(term, Integer.parseInt(tf));
                docsResults.put(doc, termsTf);
            } else {
                HashMap<String, Integer> termsTf = docsResults.get(doc);
                termsTf.put(term, Integer.parseInt(tf));
                docsResults.replace(doc, termsTf);
            }
            //  System.out.println("DocNo: "+ doc + " term&tf: " + docsResults.get(doc));
            //    System.out.println(doc + " " + docsResults.get(doc));
        }
    }


    public void separateFileToQueries(Indexer indexer, File queriesFile, boolean withStemming, String saveInPath, String corpusPath) {
        try {
            org.jsoup.nodes.Document document = Jsoup.parse(queriesFile, "UTF-8");
            org.jsoup.select.Elements elements = document.getElementsByTag("top");
            for (Element e : elements) {
                String queryText = e.select("title").text();
                String queryId = e.select("num").text();
                String queryDescription = e.select("desc").text();
                search(indexer, queryText, withStemming, saveInPath, corpusPath, queryId, queryDescription);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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


//    public static void main(String[] args){
//        Searcher searcher = new Searcher();
//        searcher.findDocsFromLine("FBIS3-21309: 1; LA030290-0012: 2;[10.41]", "invariables");
//    }
    }
}
