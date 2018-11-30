import javafx.util.Pair;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Term {
    Pair<String,Integer> postingPointer;
    int totalFreq;
    String termStr;
    int df;
    //int tf;
    //ArrayList<String> docs;
    HashMap<String,AtomicInteger> docs;

    public Term(){
        //this.tf = tf;
        //docs = new ArrayList<>();
        totalFreq = 0;
        termStr = "";
        df = 0;
        postingPointer = new Pair<>("",0);
        docs = new HashMap<>();
    }


//    public void updateTf() {
//        this.tf++;
//    }

    //Getters
    public Pair<String,Integer> getPostingPointer() {
        return postingPointer;
    }

    public int getTf(String docNo) {
        return this.docs.get(docNo).intValue();
    }

    public int getTotalFreq() {
        int totalFreq = 0;
        for (AtomicInteger tf:docs.values()) {
            totalFreq += tf.intValue();
        }
        return totalFreq;
    }

    public String getTermStr() {
        return termStr;
    }

    //    public int getDf() {
//        return df;
//    }

    public int getDf (){
        return this.docs.size();
    }

    public HashMap<String, AtomicInteger> getDocs() {
        return docs;
    }

    public void updateDf (){
        this.df += this.docs.size();
    }


    public int updateTf(String docNo) {
        return this.docs.get(docNo).incrementAndGet();
    }

    public void setTermStr(String termStr) {
        this.termStr = termStr;
    }

    //    public ArrayList<String> getDocs() {
//        return docs;
//    }
}
