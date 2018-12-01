import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class NewIndexer {

    private HashMap <String, Integer> dictionary;
    String path;

    // constructor
    public NewIndexer (String path) {
        dictionary = new HashMap<>();
        this.path = path;
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
    }

    public void index(HashMap<String, Term> terms) {

        ArrayList<ArrayList<String>> chunkLists;
        chunkLists = new ArrayList<>();
        for (int i = 0; i < 28; i++) {
            chunkLists.add(new ArrayList<>());
        }

        for (String termStr : terms.keySet()) {
            String chunk = "";
            if (termStr.length() == 0){
                break;
            }
            chunk = classifyToPosting(termStr);
            switch (chunk) {
                case "A":
                    chunkLists.get(0).add(termStr);
                    break;
                case "B":
                    chunkLists.get(1).add(termStr);
                    break;
                case "C":
                    chunkLists.get(2).add(termStr);
                    break;
                case "D":
                    chunkLists.get(3).add(termStr);
                    break;
                case "E":
                    chunkLists.get(4).add(termStr);
                    break;
                case "F":
                    chunkLists.get(5).add(termStr);
                    break;
                case "G":
                    chunkLists.get(6).add(termStr);
                    break;
                case "H":
                    chunkLists.get(7).add(termStr);
                    break;
                case "I":
                    chunkLists.get(8).add(termStr);
                    break;
                case "J":
                    chunkLists.get(9).add(termStr);
                    break;
                case "K":
                    chunkLists.get(10).add(termStr);
                    break;
                case "L":
                    chunkLists.get(11).add(termStr);
                    break;
                case "M":
                    chunkLists.get(12).add(termStr);
                    break;
                case "N":
                    chunkLists.get(13).add(termStr);
                    break;
                case "O":
                    chunkLists.get(14).add(termStr);
                    break;
                case "P":
                    chunkLists.get(15).add(termStr);
                    break;
                case "Q":
                    chunkLists.get(16).add(termStr);
                    break;
                case "R":
                    chunkLists.get(17).add(termStr);
                    break;
                case "S":
                    chunkLists.get(18).add(termStr);
                    break;
                case "T":
                    chunkLists.get(19).add(termStr);
                    break;
                case "U":
                    chunkLists.get(20).add(termStr);
                    break;
                case "V":
                    chunkLists.get(21).add(termStr);
                    break;
                case "W":
                    chunkLists.get(22).add(termStr);
                    break;
                case "X":
                    chunkLists.get(23).add(termStr);
                    break;
                case "Y":
                    chunkLists.get(24).add(termStr);
                    break;
                case "Z":
                    chunkLists.get(25).add(termStr);
                    break;
                case "numbers":
                    chunkLists.get(26).add(termStr);
                    break;
                case "other":
                    chunkLists.get(27).add(termStr);
                    break;
            }
        }

        for (int i = 0; i < chunkLists.size(); i++) {
            updateChunk(chunkLists.get(i), i, terms);
            chunkLists.get(i).clear();
        }
        chunkLists.clear();
        terms.clear();
    }

    private void updateChunk(ArrayList<String> listChunk, int index, HashMap <String, Term> terms) {
        String chunk = getChunk(index);
        ArrayList <String> listPosting = new ArrayList<>();
        StringBuilder docsListStr, strPosting;
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
            Term term = terms.get(termStr);
            HashMap <String, AtomicInteger> docsList = term.getDocs();
            docsListStr = new StringBuilder();
            //docsListStr.setLength(docsListStr.length());
            Integer pointer;
            if (isSmallLetter(termStr)) {
                if (dictionary.containsKey(termStr.toUpperCase())) {
                    pointer = dictionary.get(termStr.toUpperCase());
                    dictionary.replace(termStr,pointer);
                }
            } else if (isCapitalLetter(termStr)) {
                if (terms.containsKey(termStr.toLowerCase())) {
                    termStr = termStr.toLowerCase();
                }
            }

            if (term == null){
                break;
            }

            for (String docNo : docsList.keySet()) {
                docsListStr.append(docNo + " " + docsList.get(docNo) + ";");
            }
            //term doesn't exist in posting - add it to the end of the posting
            if (!dictionary.containsKey(termStr)) {
                listPosting.add(docsListStr + "[" + term.getDf() + "]");
                pointer = currIdx;
                dictionary.put(termStr, pointer);
                currIdx++;
            }
            //term exists in posting - update the posting in the relevant line
            else {
                pointer = dictionary.get(termStr);
                listPosting.set(pointer, listPosting.get(pointer).substring(0, listPosting.get(pointer).indexOf("[")) + docsListStr + "[" + term.getDf() + "]");
               // dictionary.put(termStr, pointer);
            }
        }


        strPosting = new StringBuilder();
        //strPosting.setLength(listPosting.size());
        for (String postingRec : listPosting) {
            strPosting.append(postingRec + "\n");
        }

        listPosting.clear();
        listChunk.clear();

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

    private String getChunk(int index) {
        switch (index){
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            case 4:
                return "E";
            case 5:
                return "F";
            case 6:
                return "G";
            case 7:
                return "H";
            case 8:
                return "I";
            case 9:
                return "J";
            case 10:
                return "K";
            case 11:
                return "L";
            case 12:
                return "M";
            case 13:
                return "N";
            case 14:
                return "O";
            case 15:
                return "P";
            case 16:
                return "Q";
            case 17:
                return "R";
            case 18:
                return "S";
            case 19:
                return "T";
            case 20:
                return "U";
            case 21:
                return "V";
            case 22:
                return "W";
            case 23:
                return "X";
            case 24:
                return "Y";
            case 25:
                return "Z";
            case 26:
                return "numbers";
            case 27:
                return "other";
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
        ArrayList <String> strList = new ArrayList<>();
        for (String termStr: dictionary.keySet()) {
            strList.add(termStr);
        }
        Collections.sort(strList);
        //sb.setLength(dictionary.keySet().size());
        for (String termStr : strList) {
            if (termStr.length() == 0){
                break;
            }
            sb.append(termStr + " " + classifyToPosting(termStr) + dictionary.get(termStr)).append("\n");
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
        System.out.println("'finished' called in index");/////////////////////////////////////////////////////////////test
        index(terms);
        writeDictionaryToDisk();
        System.out.println("'finished' ended in index");//////////////////////////////////////////////////////////////test
    }



}
