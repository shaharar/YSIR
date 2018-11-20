import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Indexer {
    private HashMap <String , DicRecDetails> dictionary;
    private HashMap <String , ArrayList<PostingTermDetails>> cache;
    private HashMap <String , ArrayList<PostingTermDetails>> tempPosting;
    int numOfDocs;

    public Indexer(){
        dictionary = new HashMap<>();
        cache = new HashMap<>();
        tempPosting = new HashMap<>();
        numOfDocs = 0;
    }

    public void createTemporaryPosting (HashMap<String, Term> terms, String DocID){
        for (String str: terms.keySet()) {
            if (!dictionary.containsKey(str)){
                dictionary.put(str, new DicRecDetails());

            }
            dictionary.get(str).totalFreq += terms.get(str).getTf();
            dictionary.get(str).df++;
        }
        numOfDocs++;

    }
}
