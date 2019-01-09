package Model;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Term {
    String termStr;
    HashMap<String,AtomicInteger> docs;

    /**
     * constructor
     */
    public Term(){
        termStr = "";
        docs = new HashMap<>();
    }

    //Getters

    public int getTf(String docNo) {
        return this.docs.get(docNo).intValue();
    }

    public String getTermStr() {
        return termStr;
    }

    public HashMap<String, AtomicInteger> getDocs() {
        return docs;
    }

    //Setters

    public int updateTf(String docNo) {
        return this.docs.get(docNo).incrementAndGet();
    }

    public void setTermStr(String termStr) {
        this.termStr = termStr;
    }
}
