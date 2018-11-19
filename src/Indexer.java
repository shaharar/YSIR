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

    public void createTemporaryPosting (HashSet<Term> terms, String DocID){
        for (Term term : terms) {
            if (!dictionary.containsKey(term.getTermStr())){
                dictionary.put(term.getTermStr(), new DicRecDetails());

            }
            dictionary.get(term.getTermStr()).totalFreq += term.getTf();
            dictionary.get(term.getTermStr()).df++;
        }
        numOfDocs++;

    }
}
