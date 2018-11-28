import javafx.util.Pair;

public class DicRecDetails {
    Pair<String,Integer> postingPointer;
    int totalFreq;
    int df;

    //constructor
    public DicRecDetails(int totalFreq) {
        this.totalFreq = totalFreq;
        df = 0;
        postingPointer = new Pair<>("",0);
    }

    //Getters
    public Pair<String,Integer> getPostingPointer() {
        return postingPointer;
    }

    public int getTotalFreq() {
        return totalFreq;
    }

    public int getDf() {
        return df;
    }
}
