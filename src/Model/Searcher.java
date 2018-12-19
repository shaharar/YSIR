package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Searcher {

    Parse parser;
    Ranker ranker;
    HashMap<String,String> docsResults;

    public Searcher() {
        docsResults = new HashMap<>();
    }

    public void search (Indexer indexer, String query, boolean withStemming, String saveInPath, String corpusPath){
        parser = new Parse(withStemming,saveInPath,corpusPath);
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

       // ranker.rank(docsResults);

    }

    private void findDocsFromLine(String line, String term) {
        String docs = line.substring(0,line.indexOf("[") - 1); //get 'docsListStr'
        String[] docsArr = docs.split("; ");
        for (String docInfo : docsArr) {
            String doc = docInfo.substring(0,docInfo.indexOf(": "));
            String tf = docInfo.substring(docInfo.indexOf(": ") + 1);
            if(!docsResults.containsKey(doc)){
                docsResults.put(doc,term + "-" + tf);
            }
            else{
                String termsInDoc = docsResults.get(doc);
                docsResults.replace(doc, termsInDoc + "|" + term + " -" + tf);
            }
//            System.out.println("DocNo: "+ doc + " term&tf: " + docsResults.get(doc));
        }
    }

    public static void main(String[] args){
        Searcher searcher = new Searcher();
        searcher.findDocsFromLine("FBIS3-21309: 1; LA030290-0012: 2;[10.41]", "invariables");
    }
}
