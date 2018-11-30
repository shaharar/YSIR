import javafx.util.Pair;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Indexer {
    private HashMap<String, Term> dictionary;
    String path;
    StringBuilder docsListStr;
    StringBuilder strPosting;
    //int idx1 = 0, idx2 = 0, idx3 = 0, idx4 = 0, idx5 = 0, idx6 = 0, idx7 = 0, idx8 = 0, idx9 = 0;
    //String[] arrChunkAC, arrChunkDG, arrChunkHK, arrChunkLO, arrChunkPS, arrChunkTV, arrChunkWZ, arrChunkNumbers, arrChunkOther;
    ArrayList<String> listChunk_A, listChunk_B, listChunk_C , listChunk_D, listChunk_E, listChunk_F, listChunk_G, listChunk_H , listChunk_I, listChunk_J, listChunk_K, listChunk_L , listChunk_M, listChunk_N, listChunk_O, listChunk_P, listChunk_Q, listChunk_R , listChunk_S, listChunk_T, listChunk_U, listChunk_V , listChunk_W, listChunk_X, listChunk_Y, listChunk_Z, listChunkNumbers , listChunkOther ;
    ArrayList<String> listPosting_A, listPosting_B, listPosting_C , listPosting_D, listPosting_E, listPosting_F, listPosting_G, listPosting_H , listPosting_I, listPosting_J, listPosting_K, listPosting_L , listPosting_M, listPosting_N, listPosting_O, listPosting_P, listPosting_Q, listPosting_R , listPosting_S, listPosting_T, listPosting_U, listPosting_V , listPosting_W, listPosting_X, listPosting_Y, listPosting_Z, listPostingNumbers , listPostingOther ;

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
            (new File(path + "\\indexResults\\postingFiles\\posting_A.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_B.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_C.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_D.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_E.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_F.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_G.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_H.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_I.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_J.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_K.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_L.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_M.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_N.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_O.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_P.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_Q.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_R.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_S.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_T.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_U.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_V.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_W.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_X.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_Y.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_Z.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_numbers.txt")).createNewFile();
            (new File(path + "\\indexResults\\postingFiles\\posting_other.txt")).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listChunk_A = new ArrayList<>();
        listChunk_B = new ArrayList<>();
        listChunk_C = new ArrayList<>();
        listChunk_D = new ArrayList<>();
        listChunk_E = new ArrayList<>();
        listChunk_F = new ArrayList<>();
        listChunk_G = new ArrayList<>();
        listChunk_H = new ArrayList<>();
        listChunk_I = new ArrayList<>();
        listChunk_J = new ArrayList<>();
        listChunk_K = new ArrayList<>();
        listChunk_L = new ArrayList<>();
        listChunk_M = new ArrayList<>();
        listChunk_N = new ArrayList<>();
        listChunk_O = new ArrayList<>();
        listChunk_P = new ArrayList<>();
        listChunk_Q = new ArrayList<>();
        listChunk_R = new ArrayList<>();
        listChunk_S = new ArrayList<>();
        listChunk_T = new ArrayList<>();
        listChunk_U = new ArrayList<>();
        listChunk_V = new ArrayList<>();
        listChunk_W = new ArrayList<>();
        listChunk_X = new ArrayList<>();
        listChunk_Y = new ArrayList<>();
        listChunk_Z = new ArrayList<>();
        listChunkNumbers = new ArrayList<>();
        listChunkOther = new ArrayList<>();

        listPosting_A = new ArrayList<>();
        listPosting_B = new ArrayList<>();
        listPosting_C = new ArrayList<>();
        listPosting_D = new ArrayList<>();
        listPosting_E = new ArrayList<>();
        listPosting_F = new ArrayList<>();
        listPosting_G = new ArrayList<>();
        listPosting_H = new ArrayList<>();
        listPosting_I = new ArrayList<>();
        listPosting_J = new ArrayList<>();
        listPosting_K = new ArrayList<>();
        listPosting_L = new ArrayList<>();
        listPosting_M = new ArrayList<>();
        listPosting_N = new ArrayList<>();
        listPosting_O = new ArrayList<>();
        listPosting_P = new ArrayList<>();
        listPosting_Q = new ArrayList<>();
        listPosting_R = new ArrayList<>();
        listPosting_S = new ArrayList<>();
        listPosting_T = new ArrayList<>();
        listPosting_U = new ArrayList<>();
        listPosting_V = new ArrayList<>();
        listPosting_W = new ArrayList<>();
        listPosting_X = new ArrayList<>();
        listPosting_Y = new ArrayList<>();
        listPosting_Z = new ArrayList<>();
        listPostingNumbers = new ArrayList<>();
        listPostingOther = new ArrayList<>();

    }


    public void index(HashMap<String, Term> terms) {

        //readPostingFiles();
//        Set <String> strList = terms.keySet();
        ArrayList <String> strList = new ArrayList<>();
        for (Term term: terms.values()) {
            String termStr = term.getTermStr();
            if (termStr.equals("")) {
                break;
            }
            strList.add(termStr);
            addToDic(term);
        }
        terms.clear();
        for (String termStr: strList) {
            if (isCapitalLetter(termStr) && dictionary.containsKey(termStr.toLowerCase())){
                termStr = termStr.toLowerCase();
            }
            if (isSmallLetter(termStr) && dictionary.containsKey(termStr.toUpperCase())){
                termStr = termStr.toUpperCase();
            }
            String chunk = "";
            chunk = classifyToPosting(termStr);
            switch (chunk){
                case "A":
                    listChunk_A.add(termStr);
                    break;
                case "B":
                    listChunk_B.add(termStr);
                    break;
                case "C":
                    listChunk_C.add(termStr);
                    break;
                case "D":
                    listChunk_D.add(termStr);
                    break;
                case "E":
                    listChunk_E.add(termStr);
                    break;
                case "F":
                    listChunk_F.add(termStr);
                    break;
                case "G":
                    listChunk_G.add(termStr);
                    break;
                case "H":
                    listChunk_H.add(termStr);
                    break;
                case "I":
                    listChunk_I.add(termStr);
                    break;
                case "J":
                    listChunk_J.add(termStr);
                    break;
                case "K":
                    listChunk_K.add(termStr);
                    break;
                case "L":
                    listChunk_L.add(termStr);
                    break;
                case "M":
                    listChunk_M.add(termStr);
                    break;
                case "N":
                    listChunk_N.add(termStr);
                    break;
                case "O":
                    listChunk_O.add(termStr);
                    break;
                case "P":
                    listChunk_P.add(termStr);
                    break;
                case "Q":
                    listChunk_Q.add(termStr);
                    break;
                case "R":
                    listChunk_R.add(termStr);
                    break;
                case "S":
                    listChunk_S.add(termStr);
                    break;
                case "T":
                    listChunk_T.add(termStr);
                    break;
                case "U":
                    listChunk_U.add(termStr);
                    break;
                case "V":
                    listChunk_V.add(termStr);
                    break;
                case "W":
                    listChunk_W.add(termStr);
                    break;
                case "X":
                    listChunk_X.add(termStr);
                    break;
                case "Y":
                    listChunk_Y.add(termStr);
                    break;
                case "Z":
                    listChunk_Z.add(termStr);
                    break;
                case "numbers":
                    listChunkNumbers.add(termStr);
                    break;
                case "other":
                    listChunkOther.add(termStr);
                    break;
            }
        }
        strList.clear();

        updateChunkToPosting("A");
        updateChunkToPosting("B");
        updateChunkToPosting("C");
        updateChunkToPosting("D");
        updateChunkToPosting("E");
        updateChunkToPosting("F");
        updateChunkToPosting("G");
        updateChunkToPosting("H");
        updateChunkToPosting("I");
        updateChunkToPosting("J");
        updateChunkToPosting("K");
        updateChunkToPosting("L");
        updateChunkToPosting("M");
        updateChunkToPosting("N");
        updateChunkToPosting("O");
        updateChunkToPosting("P");
        updateChunkToPosting("Q");
        updateChunkToPosting("R");
        updateChunkToPosting("S");
        updateChunkToPosting("T");
        updateChunkToPosting("U");
        updateChunkToPosting("V");
        updateChunkToPosting("W");
        updateChunkToPosting("X");
        updateChunkToPosting("Y");
        updateChunkToPosting("Z");
        updateChunkToPosting("numbers");
        updateChunkToPosting("other");

        }
//            terms.remove(termStr, term);

//        terms.clear();
//    }

    public void addToDic(Term term) {
        String str = term.getTermStr();
        if (isSmallLetter(str)) {
            if (dictionary.containsKey(str.toUpperCase())) {
                dictionary.put(str, dictionary.get(str.toUpperCase()));
                dictionary.get(str).setTermStr(str);
                dictionary.remove(str.toUpperCase(),dictionary.get(str.toUpperCase()) );
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

    private void updateChunkToPosting(String chunk) {
        switch (chunk){
            case "A":
                updateChunk (listChunk_A, chunk);
                break;
            case "B":
                updateChunk (listChunk_B, chunk);
                break;
            case "C":
                updateChunk (listChunk_C, chunk);
                break;
            case "D":
                updateChunk (listChunk_D, chunk);
                break;
            case "E":
                updateChunk (listChunk_E, chunk);
                break;
            case "F":
                updateChunk (listChunk_F, chunk);
                break;
            case "G":
                updateChunk (listChunk_G, chunk);
                break;
            case "H":
                updateChunk (listChunk_H, chunk);
                break;
            case "I":
                updateChunk (listChunk_I, chunk);
                break;
            case "J":
                updateChunk (listChunk_J, chunk);
                break;
            case "K":
                updateChunk (listChunk_K, chunk);
                break;
            case "L":
                updateChunk (listChunk_L, chunk);
                break;
            case "M":
                updateChunk (listChunk_M, chunk);
                break;
            case "N":
                updateChunk (listChunk_N, chunk);
                break;
            case "O":
                updateChunk (listChunk_O, chunk);
                break;
            case "P":
                updateChunk (listChunk_P, chunk);
                break;
            case "Q":
                updateChunk (listChunk_Q, chunk);
                break;
            case "R":
                updateChunk (listChunk_R, chunk);
                break;
            case "S":
                updateChunk (listChunk_S, chunk);
                break;
            case "T":
                updateChunk (listChunk_T, chunk);
                break;
            case "U":
                updateChunk (listChunk_U, chunk);
                break;
            case "V":
                updateChunk (listChunk_V, chunk);
                break;
            case "W":
                updateChunk (listChunk_W, chunk);
                break;
            case "X":
                updateChunk (listChunk_X, chunk);
                break;
            case "Y":
                updateChunk (listChunk_Y, chunk);
                break;
            case "Z":
                updateChunk (listChunk_Z, chunk);
                break;
            case "numbers":
                updateChunk (listChunkNumbers, chunk);
                break;
            case "other":
                updateChunk (listChunkOther, chunk);
                break;
        }
    }

    private void updateChunk(ArrayList<String > listChunk, String chunk) {
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

        int currIdx = listPosting.size(); // the next free index in the listPosting

        for (String termStr : listChunk) {
            Term term = dictionary.get(termStr);
            if (term == null){
                break;
            }
            docsListStr = new StringBuilder();
            HashMap<String, AtomicInteger> docsList = term.getDocs();
            for (String docNo : docsList.keySet()) {
                docsListStr.append(docNo + " " + docsList.get(docNo) + ";");
            }
            //term doesn't exist in posting - add it to the end of the posting
/*            if (term.getPostingPointer().getKey().equals("")) {
                listPosting.add("[" + term.getDf() + "] : " + docsListStr);
                term.postingPointer = new Pair<String, Integer>(chunk, currIdx);
                currIdx++;
            }
            //term exists in posting - update the posting in the relevant line
            else {
                listPosting.set(term.getPostingPointer().getValue(), "[" + term.getDf() + "] : " + docsListStr);
            }
            docsListStr = new StringBuilder();*/
        }


        strPosting = new StringBuilder();
        for (String postingRec : listPosting) {
            //  if (postingRec != null){
            strPosting.append(postingRec + "\n");
            //  }
        }


        listPosting.clear();
        listChunk.clear();

        //   System.out.println("start writing"); //////////////////////////////////////////////////////

        // create file writer
        FileWriter fwPosting = null;
        try {
            fwPosting = new FileWriter(new File(path + "\\indexResults\\postingFiles\\posting_" + chunk + ".txt"),true);
            fwPosting.append(strPosting.toString());
            fwPosting.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //     System.out.println("finished writing"); //////////////////////////////////////////////////////

        strPosting = new StringBuilder();
    }

    private ArrayList<String > getListByChunk ( String chunk){
        switch (chunk){
            case "A":
                return listPosting_A;
            case "B":
                return listPosting_B;
            case "C":
                return listPosting_C;
            case "D":
                return listPosting_D;
            case "E":
                return listPosting_E;
            case "F":
                return listPosting_F;
            case "G":
                return listPosting_G;
            case "H":
                return listPosting_H;
            case "I":
                return listPosting_I;
            case "J":
                return listPosting_J;
            case "K":
                return listPosting_K;
            case "L":
                return listPosting_L;
            case "M":
                return listPosting_M;
            case "N":
                return listPosting_N;
            case "O":
                return listPosting_O;
            case "P":
                return listPosting_P;
            case "Q":
                return listPosting_Q;
            case "R":
                return listPosting_R;
            case "S":
                return listPosting_S;
            case "T":
                return listPosting_T;
            case "U":
                return listPosting_U;
            case "V":
                return listPosting_V;
            case "W":
                return listPosting_W;
            case "X":
                return listPosting_X;
            case "Y":
                return listPosting_Y;
            case "Z":
                return listPosting_Z;
            case "numbers":
                return listPostingNumbers;
            case "other":
                return listPostingOther;
        }
        return null;
    }

    private String classifyToPosting (String term){
        char firstLetter = term.charAt(0);
        if (firstLetter == 'a' || firstLetter == 'A'){
            return "A";
        }
        else if (firstLetter == 'b' || firstLetter == 'B'){
            return "B";
        }
        else if (firstLetter == 'c' || firstLetter == 'C'){
            return "C";
        }
        else if (firstLetter == 'd' || firstLetter == 'D'){
            return "D";
        }
        else if (firstLetter == 'e' || firstLetter == 'E'){
            return "E";
        }
        else if (firstLetter == 'f' || firstLetter == 'F'){
            return "F";
        }
        else if (firstLetter == 'g' || firstLetter == 'G'){
            return "G";
        }
        else if (firstLetter == 'h' || firstLetter == 'H'){
            return "H";
        }
        else if (firstLetter == 'i' || firstLetter == 'I'){
            return "I";
        }
        else if (firstLetter == 'j' || firstLetter == 'J'){
            return "J";
        }
        else if (firstLetter == 'k' || firstLetter == 'K'){
            return "K";
        }
        else if (firstLetter == 'l' || firstLetter == 'L'){
            return "L";
        }
        else if (firstLetter == 'm' || firstLetter == 'M'){
            return "M";
        }
        else if (firstLetter == 'n' || firstLetter == 'N'){
            return "N";
        }
        else if (firstLetter == 'o' || firstLetter == 'O'){
            return "O";
        }
        else if (firstLetter == 'p' || firstLetter == 'P'){
            return "P";
        }
        else if (firstLetter == 'q' || firstLetter == 'Q'){
            return "Q";
        }
        else if (firstLetter == 'r' || firstLetter == 'R'){
            return "R";
        }
        else if (firstLetter == 's' || firstLetter == 'S'){
            return "S";
        }
        else if (firstLetter == 't' || firstLetter == 'T'){
            return "T";
        }
        else if (firstLetter == 'u' || firstLetter == 'U'){
            return "U";
        }
        else if (firstLetter == 'v' || firstLetter == 'V'){
            return "V";
        }
        else if (firstLetter == 'w' || firstLetter == 'W'){
            return "W";
        }
        else if (firstLetter == 'x' || firstLetter == 'X'){
            return "X";
        }
        else if (firstLetter == 'y' || firstLetter == 'Y'){
            return "Y";
        }
        else if (firstLetter == 'z' || firstLetter == 'Z'){
            return "Z";
        }
        else if ((firstLetter >= '0' && firstLetter <= '9')){
            return "numbers";
        }
        else{
            return "other";
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

    public void writeDocsInfoToDisk (StringBuilder sb){
        File docsInformation = new File(path + "\\indexResults\\docsInformation.txt");
        try {
            docsInformation.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(docsInformation, true);
            fw.append(sb.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeDictionaryToDisk() {

        StringBuilder sb = new StringBuilder();
        for (Term term: dictionary.values()) {
//            sb.append(term.getTermStr() + " " + term.getTotalFreq() + " " + term.getPostingPointer().getKey() + "," + term.getPostingPointer().getValue()).append("\n");
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
        System.out.println("'finished' called in index");
        index(terms);
        writeDictionaryToDisk();
        System.out.println("'finished' ended in index");
    }
}