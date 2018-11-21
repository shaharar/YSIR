import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Indexer {
    private HashMap <String , DicRecDetails> dictionary;
    private HashMap <String , ArrayList<PostingTermDetails>> tempPosting;
    private ArrayList <String> sortedTermList;
    int numOfDocs, docsCountPost;
    StringBuilder sb;
    String path;
    boolean withStemming;
    int writtenFilesCount;

    public Indexer(String path, boolean withStemming){
        dictionary = new HashMap<>();
        tempPosting = new HashMap<>();
        sortedTermList = new ArrayList<>();
        numOfDocs = 0;
        docsCountPost = 0;
        sb = new StringBuilder();
        this.path = path;
        this.withStemming = withStemming;
        writtenFilesCount = 0;
    }

    public void index (HashMap<String, Term> terms, String docID, String frequentTerm, int maxTf, String city){
        sb.append(docID + ": " + terms.size() + " " + frequentTerm + " " + maxTf + " " + city);
        for (String str : terms.keySet()) {
            mergeDics(str, terms.get(str).getTf());
            createTempPosting(terms.get(str), str, docID);
        }
        numOfDocs++;
        docsCountPost++;
        if (numOfDocs > 10000){
            sortTempPosting();
            writeTempPostingToDisk();
            numOfDocs = 0;
            tempPosting.clear();
            sortedTermList.clear();
        }
    }

    public void mergeDics (String str, int tf){
            if (!dictionary.containsKey(str)){
                dictionary.put(str, new DicRecDetails());
            }
            dictionary.get(str).totalFreq += tf;
            dictionary.get(str).df++;
    }

    public void createTempPosting (Term term,String str, String docID){
        if (tempPosting.containsKey(str)){
            tempPosting.get(str).add(new PostingTermDetails(term, docID));
        }
        else{
            ArrayList <PostingTermDetails> ptdList = new ArrayList<>();
            ptdList.add(new PostingTermDetails(term, docID));
            tempPosting.put(str, ptdList);
        }
    }

    public void sortTempPosting (){
        //copy from tempPosting to a sorted data structure
        for (String str : tempPosting.keySet()) {
            sortedTermList.add(str);
        }
        Collections.sort(sortedTermList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });
    }

    public void writeTempPostingToDisk (){
        new File(path + "\\tempPostingFiles").mkdir();
        File postingFile = new File (path + "\\tempPostingFiles\\tempPosting" + writtenFilesCount + ".txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter(postingFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String str : sortedTermList) {
            try {
                fw.write(str + " " + tempPosting.get(str).size() + " : ");
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (PostingTermDetails ptdList : tempPosting.get(str)) {
                try {
                    fw.write(ptdList.docId + ", " + ptdList.term.getTf() + ";");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                fw.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writtenFilesCount++;

    }
}
