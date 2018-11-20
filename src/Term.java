public class Term {
    int tf;

    public Term(int tf){
        this.tf = tf;
    }
    public int getTf() {
        return tf;
    }

    public void updateTf() {
        this.tf++;
    }
}
