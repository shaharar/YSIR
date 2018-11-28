import javafx.util.Pair;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Indexer {
    private HashMap<String, Term> dictionary;
    private ArrayList<String> tempTermList;
    int numOfDocs, totalDocs;
    String path;
    int writtenFilesCount;

    // constructor
    public Indexer(String path, boolean withStemming) {
        dictionary = new HashMap<>();
        tempTermList = new ArrayList<>();
        numOfDocs = 0;
        totalDocs = 0;
        this.path = path;
        writtenFilesCount = 0;
        new File(this.path + "\\indexResults").mkdir();
        new File(this.path + "\\indexResults\\postingFiles").mkdir();
        File postingAC = new File(path + "\\indexResults\\postingFiles\\posting_A-C.txt");
        File postingDG = new File(path + "\\indexResults\\postingFiles\\posting_D-G.txt");
        File postingHK = new File(path + "\\indexResults\\postingFiles\\posting_H-K.txt");
        File postingLO = new File(path + "\\indexResults\\postingFiles\\posting_L-O.txt");
        File postingPS = new File(path + "\\indexResults\\postingFiles\\posting_P-S.txt");
        File postingTV = new File(path + "\\indexResults\\postingFiles\\posting_T-V.txt");
        File postingWZ = new File(path + "\\indexResults\\postingFiles\\posting_W-Z.txt");
        File postingNumbers = new File(path + "\\indexResults\\postingFiles\\posting_numbers.txt");
        File postingOther = new File(path + "\\indexResults\\postingFiles\\posting_other.txt");

    }

    public void index(HashMap<String, Term> terms, String docNo, String city) {
        File docsInformation = new File(path + "\\indexResults\\docsInformation.txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter(docsInformation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int maxTf = 0;
        String frequentTerm = "";
        for (String str : terms.keySet()) {
            if (terms.get(str).getTf() > maxTf){
                maxTf = terms.get(str).getTf();
                frequentTerm = str;
            }
            mergeDics(str, terms.get(str).getTf(), docNo);
        }
        try {
            fw.write(docNo + ": " + terms.size() + ", " + frequentTerm + ", " + maxTf + ", " + city + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        numOfDocs++;
        totalDocs++;
        if (numOfDocs > 15000) {
            writeToPosting();
            numOfDocs = 0;
            tempTermList.clear();
        }
    }

    private void writeToPosting() {

        ArrayList<String> listPostingAC = new ArrayList<>(), listPostingDG = new ArrayList<>(), listPostingHK = new ArrayList<>(), listPostingLO = new ArrayList<>(), listPostingPS = new ArrayList<>(), listPostingTV = new ArrayList<>(), listPostingWZ = new ArrayList<>(), listPostingNumbers = new ArrayList<>(), listPostingOther = new ArrayList<>();

        //read posting files from disk
        BufferedReader brPostingAC = null, brPostingDG = null, brPostingHK = null, brPostingLO = null, brPostingPS = null, brPostingTV = null, brPostingWZ = null, brPostingNumbers = null, brPostingOther = null;
        try {
            brPostingAC = new BufferedReader(new FileReader(new File(path + "\\indexResults\\postingFiles\\posting_A-C.txt")));
            brPostingDG = new BufferedReader(new FileReader(new File(path + "\\indexResults\\postingFiles\\posting_D-G.txt")));
            brPostingHK = new BufferedReader(new FileReader(new File(path + "\\indexResults\\postingFiles\\posting_H-K.txt")));
            brPostingLO = new BufferedReader(new FileReader(new File(path + "\\indexResults\\postingFiles\\posting_L-O.txt")));
            brPostingPS = new BufferedReader(new FileReader(new File(path + "\\indexResults\\postingFiles\\posting_P-S.txt")));
            brPostingTV = new BufferedReader(new FileReader(new File(path + "\\indexResults\\postingFiles\\posting_T-V.txt")));
            brPostingWZ = new BufferedReader(new FileReader(new File(path + "\\indexResults\\postingFiles\\posting_W-Z.txt")));
            brPostingNumbers = new BufferedReader(new FileReader(new File(path + "\\indexResults\\postingFiles\\posting_numbers.txt")));
            brPostingOther = new BufferedReader(new FileReader(new File(path + "\\indexResults\\postingFiles\\posting_other.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //create a list for each posting file
        String line = "";
        try {
            while((line = (brPostingAC.readLine())) != null){
                listPostingAC.add(line);
            }
            while((line = (brPostingDG.readLine())) != null){
                listPostingDG.add(line);
            }
            while((line = (brPostingHK.readLine())) != null){
                listPostingHK.add(line);
            }
            while((line = (brPostingLO.readLine())) != null){
                listPostingLO.add(line);
            }
            while((line = (brPostingPS.readLine())) != null){
                listPostingPS.add(line);
            }
            while((line = (brPostingTV.readLine())) != null){
                listPostingTV.add(line);
            }
            while((line = (brPostingWZ.readLine())) != null){
                listPostingWZ.add(line);
            }
            while((line = (brPostingNumbers.readLine())) != null){
                listPostingNumbers.add(line);
            }
            while((line = (brPostingOther.readLine())) != null){
                listPostingOther.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // close posting files
        try {
            brPostingAC.close();
            brPostingDG.close();
            brPostingHK.close();
            brPostingLO.close();
            brPostingPS.close();
            brPostingTV.close();
            brPostingWZ.close();
            brPostingNumbers.close();
            brPostingOther.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // write terms to posting files
        for (String termStr: tempTermList) {
            Term term = dictionary.get(termStr);
            String docsListStr = "";
            ArrayList <String> docsList = term.getDocs();
            Collections.sort(docsList); //////////////////////////////////////////////////////////////////// optional
            for (String docNo: docsList) {
                docsListStr += docNo + " " + term.getTf() + ";";
            }

            //term doesn't exist in posting - add it to the relevant posting file
            if (term.getPostingPointer() == null){
                String pointer = classifyToPosting(termStr);
                switch (pointer){
                    case "AC":
                        listPostingAC.add("[" + term.getDf() + "] : " + docsListStr);
                        dictionary.get(termStr).postingPointer = new Pair<>("AC", listPostingAC.size() - 1);
                        break;
                    case "DG":
                        listPostingDG.add("[" + term.getDf() + "] : " + docsListStr);
                        dictionary.get(termStr).postingPointer = new Pair<>("DG", listPostingDG.size() - 1);
                        break;
                    case "HK":
                        listPostingHK.add("[" + term.getDf() + "] : " + docsListStr);
                        dictionary.get(termStr).postingPointer = new Pair<>("HK", listPostingHK.size() - 1);
                        break;
                    case "LO":
                        listPostingLO.add("[" + term.getDf() + "] : " + docsListStr);
                        dictionary.get(termStr).postingPointer = new Pair<>("LO", listPostingLO.size() - 1);
                        break;
                    case "PS":
                        listPostingPS.add("[" + term.getDf() + "] : " + docsListStr);
                        dictionary.get(termStr).postingPointer = new Pair<>("PS", listPostingPS.size() - 1);
                        break;
                    case "TV":
                        listPostingTV.add("[" + term.getDf() + "] : " + docsListStr);
                        dictionary.get(termStr).postingPointer = new Pair<>("TV", listPostingTV.size() - 1);
                        break;
                    case "WZ":
                        listPostingWZ.add("[" + term.getDf() + "] : " + docsListStr);
                        dictionary.get(termStr).postingPointer = new Pair<>("WZ", listPostingWZ.size() - 1);
                        break;
                    case "numbers":
                        listPostingNumbers.add("[" + term.getDf() + "] : " + docsListStr);
                        dictionary.get(termStr).postingPointer = new Pair<>("numbers", listPostingNumbers.size() - 1);
                        break;
                    case "other":
                        listPostingOther.add("[" + term.getDf() + "] : " + docsListStr);
                        dictionary.get(termStr).postingPointer = new Pair<>("other", listPostingOther.size() - 1);
                        break;
                }
            }
            //term exists in posting - update the relevant posting file
            else{
                Pair<String, Integer> pointer = term.getPostingPointer();
                switch (pointer.getKey()){
                    case "AC":
                        listPostingAC.add(pointer.getValue(),"[" + term.getDf() + "] : " + docsListStr);
                        break;
                    case "DG":
                        listPostingDG.add(pointer.getValue(),"[" + term.getDf() + "] : " + docsListStr);
                        break;
                    case "HK":
                        listPostingHK.add(pointer.getValue(),"[" + term.getDf() + "] : " + docsListStr);
                        break;
                    case "LO":
                        listPostingLO.add(pointer.getValue(),"[" + term.getDf() + "] : " + docsListStr);
                        break;
                    case "PS":
                        listPostingPS.add(pointer.getValue(),"[" + term.getDf() + "] : " + docsListStr);
                        break;
                    case "TV":
                        listPostingTV.add(pointer.getValue(),"[" + term.getDf() + "] : " + docsListStr);
                        break;
                    case "WZ":
                        listPostingWZ.add(pointer.getValue(),"[" + term.getDf() + "] : " + docsListStr);
                        break;
                    case "numbers":
                        listPostingNumbers.add(pointer.getValue(),"[" + term.getDf() + "] : " + docsListStr);
                        break;
                    case "other":
                        listPostingOther.add(pointer.getValue(),"[" + term.getDf() + "] : " + docsListStr);
                        break;
                }
            }
        }

        // write updated posting files to disk
        FileWriter fwPostingAC = null, fwPostingDG = null, fwPostingHK = null, fwPostingLO = null, fwPostingPS = null, fwPostingTV = null, fwPostingWZ = null, fwPostingNumbers = null, fwPostingOther = null;
        String strPostingAC = "", strPostingDG = "", strPostingHK = "", strPostingLO = "", strPostingPS = "", strPostingTV = "", strPostingWZ = "", strPostingNumbers = "", strPostingOther = "";

        for (String postingRec: listPostingAC) {
            strPostingAC += postingRec + "\n";
        }
        for (String postingRec: listPostingDG) {
            strPostingDG += postingRec + "\n";
        }
        for (String postingRec: listPostingHK) {
            strPostingHK += postingRec + "\n";
        }
        for (String postingRec: listPostingLO) {
            strPostingLO += postingRec + "\n";
        }
        for (String postingRec: listPostingPS) {
            strPostingPS += postingRec + "\n";
        }
        for (String postingRec: listPostingTV) {
            strPostingTV += postingRec + "\n";
        }
        for (String postingRec: listPostingWZ) {
            strPostingWZ += postingRec + "\n";
        }
        for (String postingRec: listPostingNumbers) {
            strPostingNumbers += postingRec + "\n";
        }
        for (String postingRec: listPostingOther) {
            strPostingOther += postingRec + "\n";
        }

        // create file writers
        try {
            fwPostingAC = new FileWriter(new File(path + "\\indexResults\\postingFiles\\posting_A-C.txt"));
            fwPostingDG = new FileWriter(new File(path + "\\indexResults\\postingFiles\\posting_D-G.txt"));
            fwPostingHK = new FileWriter(new File(path + "\\indexResults\\postingFiles\\posting_H-K.txt"));
            fwPostingLO = new FileWriter(new File(path + "\\indexResults\\postingFiles\\posting_L-O.txt"));
            fwPostingPS = new FileWriter(new File(path + "\\indexResults\\postingFiles\\posting_P-S.txt"));
            fwPostingTV = new FileWriter(new File(path + "\\indexResults\\postingFiles\\posting_T-V.txt"));
            fwPostingWZ = new FileWriter(new File(path + "\\indexResults\\postingFiles\\posting_W-Z.txt"));
            fwPostingNumbers = new FileWriter(new File(path + "\\indexResults\\postingFiles\\posting_numbers.txt"));
            fwPostingOther = new FileWriter(new File(path + "\\indexResults\\postingFiles\\posting_other.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fwPostingAC.write(strPostingAC, 0, strPostingAC.length());
            fwPostingDG.write(strPostingDG, 0, strPostingAC.length());
            fwPostingHK.write(strPostingHK, 0, strPostingAC.length());
            fwPostingLO.write(strPostingLO, 0, strPostingAC.length());
            fwPostingPS.write(strPostingPS, 0, strPostingAC.length());
            fwPostingTV.write(strPostingTV, 0, strPostingAC.length());
            fwPostingWZ.write(strPostingWZ, 0, strPostingAC.length());
            fwPostingNumbers.write(strPostingNumbers, 0, strPostingAC.length());
            fwPostingOther.write(strPostingOther, 0, strPostingAC.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String classifyToPosting (String term){
        char firstLetter = term.charAt(0);
        if ((firstLetter >= 'a' && firstLetter <= 'c') || (firstLetter >= 'A' && firstLetter <= 'C')){
            return "AC";
        }
        else if ((firstLetter >= 'd' && firstLetter <= 'g') || (firstLetter >= 'D' && firstLetter <= 'G')){
            return "DG";
        }
        else if ((firstLetter >= 'h' && firstLetter <= 'k') || (firstLetter >= 'H' && firstLetter <= 'K')){
            return "HK";
        }
        else if ((firstLetter >= 'l' && firstLetter <= 'o') || (firstLetter >= 'L' && firstLetter <= 'O')){
            return "LO";
        }
        else if ((firstLetter >= 'p' && firstLetter <= 's') || (firstLetter >= 'P' && firstLetter <= 'S')){
            return "PS";
        }
        else if ((firstLetter >= 't' && firstLetter <= 'v') || (firstLetter >= 'T' && firstLetter <= 'V')){
            return "TV";
        }
        else if ((firstLetter >= 'w' && firstLetter <= 'z') || (firstLetter >= 'W' && firstLetter <= 'Z')){
            return "WZ";
        }
        else if ((firstLetter >= '0' && firstLetter <= '9')){
            return "numbers";
        }
        else{
            return "other";
        }
    }

    public void mergeDics(String str, int tf, String docNo) {
        if (str.charAt(0) >= 'a' && str.charAt(0) <= 'z'){
            if (dictionary.containsKey(str.toUpperCase())){
                dictionary.put(str, dictionary.get(str.toUpperCase()));
                dictionary.remove(str.toUpperCase());
            }
        }
        else if (str.charAt(0) >= 'A' && str.charAt(0) <= 'Z'){
            if (dictionary.containsKey(str.toLowerCase())){
                str = str.toLowerCase();
            }
        }
        if (!dictionary.containsKey(str)) {
            dictionary.put(str, new Term(tf));
        }
        dictionary.get(str).totalFreq += tf;
        dictionary.get(str).df++;
        dictionary.get(str).docs.add(docNo);

        if (!tempTermList.contains(str)){
            tempTermList.add(str);
        }
    }

/*    public void createTempPosting(Term term, String str, String docID) {
        if (tempPosting.containsKey(str)) {
            tempPosting.get(str).add(new PostingTermDetails(term, docID));
        } else {
            ArrayList<PostingTermDetails> ptdList = new ArrayList<>();
            ptdList.add(new PostingTermDetails(term, docID));
            tempPosting.put(str, ptdList);
        }
    }*/

/*    public void sortTempPosting() {
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
    }*/

/*    public void writeTempPostingToDisk() {
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
    }*/


/*    private void mergeTempPostings (){
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





    }*/

    /*private void mergeSort(File posting1, File posting2) {

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
    }*/
}
