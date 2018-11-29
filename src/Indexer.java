import javafx.util.Pair;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Indexer {
    private HashMap<String, Term> dictionary;
    String path;
    //StringBuilder docsListStr;
    int idx1 = 0, idx2 = 0, idx3 = 0, idx4 = 0, idx5 = 0, idx6 = 0, idx7 = 0, idx8 = 0, idx9 = 0;
    String[] arrChunkAC, arrChunkDG, arrChunkHK, arrChunkLO, arrChunkPS, arrChunkTV, arrChunkWZ, arrChunkNumbers, arrChunkOther;
    ArrayList<String> listChunkAC = new ArrayList<>(), listChunkDG = new ArrayList<>(), listChunkHK = new ArrayList<>(), listChunkLO = new ArrayList<>(), listChunkPS = new ArrayList<>(), listChunkTV = new ArrayList<>(), listChunkWZ = new ArrayList<>(), listChunkNumbers = new ArrayList<>(), listChunkOther = new ArrayList<>();
    //private StringBuilder strPostingAC = null, strPostingDG = null, strPostingHK = null, strPostingLO = null, strPostingPS = null, strPostingTV = null, strPostingWZ = null, strPostingNumbers = null, strPostingOther = null;
    //private FileWriter fwPostingAC = null, fwPostingDG = null, fwPostingHK = null, fwPostingLO = null, fwPostingPS = null, fwPostingTV = null, fwPostingWZ = null, fwPostingNumbers = null, fwPostingOther = null;

    // constructor
    public Indexer(String path) {
        dictionary = new HashMap<>();
        this.path = path;
        //docsListStr = new StringBuilder();
        new File(this.path + "\\indexResults").mkdir();
        new File(this.path + "\\indexResults\\postingFiles").mkdir();
        try {
            (new File(path + "\\indexResults\\postingFiles\\posting_AC.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_DG.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_HK.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_LO.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_PS.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_TV.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_WZ.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_numbers.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_other.txt")).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void index(HashMap<String, Term> terms) {
        int counterAC = 0, counterDG = 0, counterHK = 0, counterLO = 0, counterPS = 0, counterTV = 0, counterWZ = 0, counterNumbers = 0, counterOther = 0;

        //readPostingFiles();
        for (Term term: terms.values()) {
            String termStr = term.getTermStr();
            addToDic(term);
            if (isCapitalLetter(termStr) && dictionary.containsKey(termStr.toLowerCase())){
                termStr = termStr.toLowerCase();
            }
            String chunk = "";
            chunk = classifyToPosting(termStr);
            switch (chunk){
                case "AC":
                    listChunkAC.add(termStr);
                    if (term.getPostingPointer().getKey() == "")
                        counterAC++;
                    break;
                case "DG":
                    listChunkDG.add(termStr);
                    if (term.getPostingPointer().getKey() == "")
                        counterDG++;
                    break;
                case "HK":
                    listChunkHK.add(termStr);
                    if (term.getPostingPointer().getKey() == "")
                        counterHK++;
                    break;
                case "LO":
                    listChunkLO.add(termStr);
                    if (term.getPostingPointer().getKey() == "")
                        counterLO++;
                    break;
                case "PS":
                    listChunkPS.add(termStr);
                    if (term.getPostingPointer().getKey() == "")
                        counterPS++;
                    break;
                case "TV":
                    listChunkTV.add(termStr);
                    if (term.getPostingPointer().getKey() == "")
                        counterTV++;
                    break;
                case "WZ":
                    listChunkWZ.add(termStr);
                    if (term.getPostingPointer().getKey() == "")
                        counterWZ++;
                    break;
                case "numbers":
                    listChunkNumbers.add(termStr);
                    if (term.getPostingPointer().getKey() == "")
                        counterNumbers++;
                    break;
                case "other":
                    listChunkOther.add(termStr);
                    if (term.getPostingPointer().getKey() == "")
                        counterOther++;
                    break;
            }
        }
        arrChunkAC = listChunkAC.toArray(new String[listChunkAC.size()]);
        listChunkAC.clear();
        arrChunkDG = listChunkDG.toArray(new String[listChunkDG.size()]);
        listChunkDG.clear();
        arrChunkHK = listChunkHK.toArray(new String[listChunkHK.size()]);
        listChunkHK.clear();
        arrChunkLO = listChunkLO.toArray(new String[listChunkLO.size()]);
        listChunkLO.clear();
        arrChunkPS = listChunkPS.toArray(new String[listChunkPS.size()]);
        listChunkPS.clear();
        arrChunkTV = listChunkTV.toArray(new String[listChunkTV.size()]);
        listChunkTV.clear();
        arrChunkWZ = listChunkWZ.toArray(new String[listChunkWZ.size()]);
        listChunkWZ.clear();
        arrChunkNumbers = listChunkNumbers.toArray(new String[listChunkNumbers.size()]);
        listChunkNumbers.clear();
        arrChunkOther = listChunkOther.toArray(new String[listChunkOther.size()]);
        listChunkOther.clear();

        updateChunkToPosting("AC",counterAC);
        updateChunkToPosting("DG",counterDG);
        updateChunkToPosting("HK",counterHK);
        updateChunkToPosting("LO",counterLO);
        updateChunkToPosting("PS",counterPS);
        updateChunkToPosting("TV",counterTV);
        updateChunkToPosting("WZ",counterWZ);
        updateChunkToPosting("numbers",counterNumbers);
        updateChunkToPosting("other",counterOther);


        System.out.println("starts writing");
        //writePostingToDisk(terms);
        System.out.println("finished writing");
    }

    public void addToDic(Term term) {
        String str = term.getTermStr();
        if (isSmallLetter(str)) {
            if (dictionary.containsKey(str.toUpperCase())) {
                dictionary.put(str, dictionary.get(str.toUpperCase()));
                dictionary.get(str).setTermStr(str);
                dictionary.remove(str.toUpperCase());
            }
        } else if (isCapitalLetter(str)) {
            if (dictionary.containsKey(str.toLowerCase())) {
                str = str.toLowerCase();
            }
        }
        if (!dictionary.containsKey(str)) {
            dictionary.put(str, term);
        }
    }


    private boolean isCapitalLetter (String s) {
        if (s.charAt(0) >= 'A' && s.charAt(0) <= 'Z') {
            return true;
        } else {
            return false;
        }
    }

    private boolean isSmallLetter (String s) {
        if (s.charAt(0) >= 'a' && s.charAt(0) <= 'z') {
            return true;
        } else {
            return false;
        }
    }

    private void updateChunkToPosting(String chunk, int counter) {
        switch (chunk){
            case "AC":
                updateChunk (arrChunkAC, chunk, counter);
                break;
            case "DG":
                updateChunk (arrChunkDG, chunk, counter);
                break;
            case "HK":
                updateChunk (arrChunkHK, chunk, counter);
                break;
            case "LO":
                updateChunk (arrChunkLO, chunk, counter);
                break;
            case "PS":
                updateChunk (arrChunkPS, chunk, counter);
                break;
            case "TV":
                updateChunk (arrChunkTV, chunk, counter);
                break;
            case "WZ":
                updateChunk (arrChunkWZ, chunk, counter);
                break;
            case "numbers":
                updateChunk (arrChunkNumbers, chunk, counter);
                break;
            case "other":
                updateChunk (arrChunkOther, chunk, counter);
                break;
        }
    }

    private void updateChunk(String[] arrChunk, String chunk, int counter) {
        String[] arrPosting;
        ArrayList<String> listPosting = getListByChunk(chunk);

        //read posting file from disk, and insert it's lines to list
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(path + "\\indexResults\\postingFiles\\posting_" + chunk + ".txt")));
            String line = "";
            while ((line = (br.readLine())) != null) {
                listPosting.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int arrPostingSize = listPosting.size() + counter;
        arrPosting = new String[arrPostingSize];
        for (int i = 0; i < listPosting.size(); i++) {
            arrPosting[i] = listPosting.get(i);
        }
        int currIdx = listPosting.size(); // the next free index in the arrPosting
        listPosting.clear();

        for (String termStr : arrChunk) {
            Term term = dictionary.get(termStr);
            StringBuilder docsListStr = new StringBuilder();
            HashMap<String, AtomicInteger> docsList = term.getDocs();
            for (String docNo : docsList.keySet()) {
                docsListStr.append(docNo + " " + docsList.get(docNo) + ";");
            }
            //term doesn't exist in posting - add it to the end of the posting
            if (term.getPostingPointer().getKey().equals("")) {
                arrPosting[currIdx] = "[" + term.getDf() + "] : " + docsListStr;
                term.postingPointer = new Pair<>(chunk, currIdx);
                currIdx++;
                break;
            }
            //term exists in posting - update the posting in the relevant line
            else {
                arrPosting[term.getPostingPointer().getValue()] = "[" + term.getDf() + "] : " + docsListStr;
                break;
            }
        }
        StringBuilder strPosting = new StringBuilder();
        for (String postingRec : arrPosting) {
            strPosting.append(postingRec).append("\n");
        }

        // create file writer
        FileWriter fwPosting = null;
        try {
            fwPosting = new FileWriter(new File(path + "\\indexResults\\postingFiles\\posting_" + chunk + ".txt"));
            fwPosting.write(strPosting.toString());
            fwPosting.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String > getListByChunk ( String chunk){
        switch (chunk){
            case "AC":
                return listChunkAC;
            case "DG":
                return listChunkDG;
            case "HK":
                return listChunkHK;
            case "LO":
                return listChunkLO;
            case "PS":
                return listChunkPS;
            case "TV":
                return listChunkTV;
            case "WZ":
                return listChunkWZ;
            case "numbers":
                return listChunkNumbers;
            case "other":
                return listChunkOther;
        }
        return null;
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


    public void writeDocsInfoToDisk (StringBuilder sb){
        File docsInformation = new File(path + "\\indexResults\\docsInformation.txt");
        try {
            docsInformation.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(docsInformation);
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeDictionaryToDisk() {

        StringBuilder sb = new StringBuilder();
        for (Term term: dictionary.values()) {
            sb.append(term.getTermStr() + " " + term.getTotalFreq() + " " + term.getPostingPointer().getKey() + "," + term.getPostingPointer().getValue()).append("\n");
        }
        File dictionary = new File(path + "\\indexResults\\dictionary.txt");
        try {
            dictionary.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(dictionary);
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finished(HashMap<String ,Term> terms) {
        index(terms);
        writeDictionaryToDisk();
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
