import javafx.util.Pair;

import java.util.ArrayList;

public class Term {
    Pair<String,Integer> postingPointer;
    int totalFreq;
    int df;
    int tf;
    ArrayList<String> docs;

    public Term(int tf){
        this.tf = tf;
        docs = new ArrayList<>();
        totalFreq = 0;
        df = 0;
        postingPointer = null;
    }

    public void updateTf() {
        this.tf++;
    }

    //Getters
    public Pair<String,Integer> getPostingPointer() {
        return postingPointer;
    }

    public int getTf() {
        return tf;
    }

    public int getTotalFreq() {
        return totalFreq;
    }

    public int getDf() {
        return df;
    }

    public ArrayList<String> getDocs() {
        return docs;
    }
}
