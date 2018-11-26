import java.io.*;
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
                fw.write(str + " (" + tempPosting.get(str).size() + ") : ");
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
        File postingFile = new File(path + "\\tempPostingFiles\\tempPosting");
        File[] tempFiles = postingFile.listFiles();

        int i = 0;
        while (tempFiles.length > 2){
            mergeSort(tempFiles[i], tempFiles[i+1]);
            tempFiles[i].delete();
            tempFiles[i+1].delete();
            tempFiles = postingFile.listFiles();
            i = i + 2;
        }
        //merge the last two files
        if (tempFiles.length == 2){
            mergeSort(tempFiles[0], tempFiles[1]);
        }





    }

    private void mergeSort(File posting1, File posting2) {

        ArrayList<String> linesList1 = new ArrayList<>();
        ArrayList<String> linesList2 = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(posting1));
            String line = "";
            while((line = (br.readLine())) != null){
                linesList1.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(posting2));
            String line = "";
            while((line = (br.readLine())) != null){
                linesList2.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int currLine1 = 0, currLine2 = 0; //index of line in posting file
        String mergedPostings = "";

        //get the first term in each of the posting files
        String firstTerm1 = linesList1.get(0).substring(0, linesList1.get(0).indexOf("(") - 1);
        String firstTerm2 = linesList2.get(0).substring(0, linesList2.get(0).indexOf("(") - 1);

        //firstTerm1 > firstTerm2
        if (firstTerm1.compareTo(firstTerm2) >= 0) {
            mergeSortHelper(linesList1,linesList2,currLine1,currLine2);
        }
        //firstTerm2 > firstTerm1
        else{
            mergeSortHelper(linesList2,linesList1,currLine2,currLine1);
        }
    }

    private void mergeSortHelper (ArrayList<String> lines1, ArrayList<String>lines2, int currLine1, int currLine2){
        FileWriter mergedFile = null;
        try {
            mergedFile = new FileWriter(path + "\\tempPostingFiles\\tempPosting" + writtenFilesCount + ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String line : lines1) {
            if (currLine2 < lines2.size()) {
                if (! (currLine1 < lines1.size())){
                    //write to file the rest of the lines in posting2
                    for (int i = currLine2; i < lines2.size(); i++){
                        try {
                            mergedFile.write(lines2.get(i) + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    String term1 = line.substring(0, line.indexOf("(") - 1);
                    String term2 = lines2.get(currLine2).substring(0, line.indexOf("(") - 1);

                    while (term1.compareTo(term2) == 1) { //term1 > term2
                        try {
                            mergedFile.write(lines2.get(currLine2) + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        currLine2++;
                        term2 = lines2.get(currLine2).substring(0, line.indexOf("(") - 1);
                    }
                    //terms are equals - merge them
                    if (term1.equals(term2)) {
                        //add info of the term from posting1 to posting2
                        String termInfo = line.substring(line.indexOf(":") + 2, line.length());
                        String line2 = lines2.get(currLine2);
                        String df1 = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                        String df2 = line2.substring(line2.indexOf("(") + 1, line2.indexOf(")"));
                        int totalDf = Integer.parseInt(df1) + Integer.parseInt(df2);
                        String mergedLine = term1 + " (" + totalDf + ") : " + line2.substring(line2.indexOf(":") + 2, line2.length()) + termInfo;
                        lines2.remove(currLine2);
                        lines2.add(currLine2, mergedLine);
                        try {
                            mergedFile.write(mergedLine + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //term1 < term2 - term1 doesn't exist in posting2
                    else{
                        lines2.add(currLine2, line);
                        try {
                            mergedFile.write(line + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        currLine2++;
                    }
                }

            }
            else{
                //there are still unhandled lines in posting1
                if (currLine1 < lines1.size()){
                    //add the rest of the lines in posting1 to the end of posting2
                    for (int i = currLine1; i < lines1.size(); i++){
                        lines2.add(currLine2 + 1, lines1.get(i));
                        try {
                            mergedFile.write(lines1.get(i) + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        writtenFilesCount++;
    }
}
