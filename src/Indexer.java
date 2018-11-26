import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class Indexer {
    private HashMap<String, DicRecDetails> dictionary;
    private HashMap<String, ArrayList<PostingTermDetails>> tempPosting;
    private ArrayList<String> sortedTermList;
    int numOfDocs, docsCountPost;
    StringBuilder sb;
    String path;
    boolean withStemming;
    int writtenFilesCount;

    public Indexer(String path, boolean withStemming) {
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

    public void index(HashMap<String, Term> terms, String docID, String frequentTerm, int maxTf, String city) {
        sb.append(docID + ": " + terms.size() + " " + frequentTerm + " " + maxTf + " " + city);
        for (String str : terms.keySet()) {
            mergeDics(str, terms.get(str).getTf());
            createTempPosting(terms.get(str), str, docID);
        }
        numOfDocs++;
        docsCountPost++;
        if (numOfDocs > 10000) {
            sortTempPosting();
            writeTempPostingToDisk();
            numOfDocs = 0;
            tempPosting.clear();
            sortedTermList.clear();
        }
    }

    public void mergeDics(String str, int tf) {
        if (!dictionary.containsKey(str)) {
            dictionary.put(str, new DicRecDetails());
        }
        dictionary.get(str).totalFreq += tf;
        dictionary.get(str).df++;
    }

    public void createTempPosting(Term term, String str, String docID) {
        if (tempPosting.containsKey(str)) {
            tempPosting.get(str).add(new PostingTermDetails(term, docID));
        } else {
            ArrayList<PostingTermDetails> ptdList = new ArrayList<>();
            ptdList.add(new PostingTermDetails(term, docID));
            tempPosting.put(str, ptdList);
        }
    }

    public void sortTempPosting() {
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

    public void writeTempPostingToDisk() {
        new File(path + "\\tempPostingFiles").mkdir();
        File postingFile = new File(path + "\\tempPostingFiles\\tempPosting" + writtenFilesCount + ".txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter(postingFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String str : sortedTermList) {
            try {
                fw.write(str + " (" + tempPosting.get(str).size() + ")" + " : ");
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

    private void mergeTempPostings (){







    }

    private String mergeSort(String posting1, String posting2) {

        int currLine1 = 0, currLine2 = 0; //index of line in posting file
        String mergedPostings = "";
        String[] lines1 = posting1.split("\n");
        String[] lines2 = posting2.split("\n");
        ArrayList<String> linesList1 = (ArrayList<String>) Arrays.asList(lines1);
        ArrayList<String> linesList2 = (ArrayList<String>) Arrays.asList(lines2);

        //get the first term in each of the posting files
        String firstTerm1 = lines1[0].substring(0, lines1[0].indexOf("(") - 1);
        String firstTerm2 = lines2[0].substring(0, lines2[0].indexOf("(") - 1);

        //firstTerm1 > firstTerm2
        if (firstTerm1.compareTo(firstTerm2) >= 0) {
            linesList2 = mergeSortHelper(lines1,lines2,linesList2,currLine1,currLine2);
            mergedPostings = linesList2.toString();
        }
        //firstTerm2 > firstTerm1
        else{
            linesList1 = mergeSortHelper(lines2,lines1,linesList1,currLine2,currLine1);
            mergedPostings = linesList1.toString();
        }
        return mergedPostings;
    }

    private ArrayList<String> mergeSortHelper (String[] lines1, String[] lines2, ArrayList<String> linesList2, int currLine1, int currLine2){
        for (String line : lines1) {
            if (currLine2 < linesList2.size()) {
                String term1 = line.substring(0, lines1[0].indexOf("(") - 1);
                String term2 = lines2[currLine2].substring(0, lines1[0].indexOf("(") - 1);

                while (term1.compareTo(term2) == 1) { //term1 > term2
                    currLine2++;
                }
                //terms are equals - merge them
                if (term1.equals(term2)) {
                    //add info of the term from posting1 to posting2
                    String termInfo = line.substring(lines1[0].indexOf(":") + 2, line.length());
                    String mergedLine = linesList2.get(currLine2) + termInfo;
                    linesList2.remove(currLine2);
                    linesList2.add(currLine2, mergedLine);
                }
                //term1 < term2 - term1 doesn't exist in posting2
                else{
                    linesList2.add(currLine2 - 1, line);
                }
            }
            else{
                //there are still unhandled lines in posting1
                if (currLine1 < lines1.length){
                    //add the rest of the lines in posting1 to the end of posting2
                    for (int i = currLine1; i < lines1.length; i++){
                        linesList2.add(currLine2 + 1, lines1[i]);
                    }
                }
            }
        }
        return linesList2;
    }
}
