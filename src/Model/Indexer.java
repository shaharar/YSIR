package Model;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Indexer {

    private HashMap <String, ArrayList <Integer>> dictionary;
    String path;
    private boolean withStemming;
    String postingDir;

    // constructor
    public Indexer(String path, boolean withStemming) {
        dictionary = new HashMap<>();
        this.path = path;
        this.withStemming = withStemming;
        new File(this.path + "\\indexResults").mkdir();
        if (!withStemming){
            postingDir = "\\indexResults\\postingFiles";
        }
        else{
            postingDir = "\\indexResults\\postingFiles_Stemming";
        }

        //create posting files by chunks
        new File(this.path + postingDir).mkdir();
        try {
            (new File(path + postingDir + "\\posting_A.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_B.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_C.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_D.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_E.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_F.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_G.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_H.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_I.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_J.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_K.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_L.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_M.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_N.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_O.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_P.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_Q.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_R.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_S.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_T.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_U.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_V.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_W.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_X.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_Y.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_Z.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_numbers.txt")).createNewFile();
            (new File(path + postingDir + "\\posting_other.txt")).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //build the inverted index
    public void index(HashMap<String, Term> terms, int docsInCollection, boolean withStemming) {

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
            updateChunk(chunkLists.get(i), i, terms, docsInCollection);
            chunkLists.get(i).clear();
        }
        chunkLists.clear();
        terms.clear();
    }

    private void updateChunk(ArrayList<String> listChunk, int index, HashMap <String, Term> terms, int docsInCollection) {
        String chunk = getChunk(index);
        ArrayList <String> listPosting = new ArrayList<>();
        StringBuilder docsListStr, strPosting;

        //read posting file from disk, and insert it's lines to list
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(path + postingDir + "\\posting_" + chunk + ".txt")));
            String line = "";
            while ((line = (br.readLine())) != null) {
                listPosting.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int currIdx = listPosting.size() + 1; // the next free index in the listPosting

        for (String termStr : listChunk) {
            Term term = terms.get(termStr);
            HashMap <String, AtomicInteger> docsList = term.getDocs();
            ArrayList <Integer> termInfo;
            int currDf = docsList.size();
            int currTotalFreq = 0;
            double currIdf = 0;
            try {
                currIdf = Math.log(docsInCollection / currDf);
            }catch (ArithmeticException e){
                currIdf = 0;
            }
            for (AtomicInteger tf:docsList.values()) {
                currTotalFreq += tf.intValue();
            }
            docsListStr = new StringBuilder();
            Integer pointer;
            if (isSmallLetter(termStr)) {
                if (dictionary.containsKey(termStr.toUpperCase())) {
                    termInfo = dictionary.get(termStr.toUpperCase());
                    dictionary.replace(termStr,termInfo);
                }
            } else if (isCapitalLetter(termStr)) {
                if (dictionary.containsKey(termStr.toLowerCase())) {
                    termStr = termStr.toLowerCase();
                }
            }

            //term doesn't exist in posting - add it to the end of the posting
            if (!dictionary.containsKey(termStr)) {
                for (String docNo : docsList.keySet()) {
/*                    double weight = docsList.get(docNo).intValue() * currIdf;
                    String weightStr = Double.toString(weight);
                    if (weightStr.contains(".")){
                        if(weightStr.indexOf(".") + 3 < weightStr.length()) {
                            weightStr = weightStr.substring(0, weightStr.indexOf(".") + 3);
                        }
                    }
                    docsListStr.append(docNo + " " + docsList.get(docNo) + " " + weightStr + "; ");*/
                    docsListStr.append(docNo + ": " + docsList.get(docNo) + "; ");
                }
                String currIdfStr = Double.toString(currIdf);
                if (currIdfStr.contains(".")) {
                    if (currIdfStr.indexOf(".") + 3 < currIdfStr.length()) {
                        currIdfStr = currIdfStr.substring(0, currIdfStr.indexOf(".") + 3);
                    }
                }
                listPosting.add(docsListStr + "[" + currIdfStr + "]");
                pointer = currIdx;
                termInfo = new ArrayList<>();
                termInfo.add(pointer);
                termInfo.add(currDf);
                termInfo.add(currTotalFreq);
                dictionary.put(termStr, termInfo);
                currIdx++;
            }
            //term exists in posting - update the posting in the relevant line
            else {
                termInfo = dictionary.get(termStr);
                pointer = termInfo.get(0);
                currDf += termInfo.get(1);
                currTotalFreq += termInfo.get(2);
                try{
                    currIdf = Math.log(docsInCollection / currDf);
                }
                catch (ArithmeticException e){
                    currIdf = 0;
                }
                ArrayList <Integer> newTermInfo = new ArrayList<>();
                newTermInfo.add(pointer);
                newTermInfo.add(currDf);
                newTermInfo.add(currTotalFreq);
                String linePosting = listPosting.get(pointer - 1);
                for (String docNo : docsList.keySet()) {
/*                    double weight = docsList.get(docNo).intValue() * currIdf;
                    String weightStr = Double.toString(weight);
                    if (weightStr.contains(".")){
                        if(weightStr.indexOf(".") + 3 < weightStr.length()) {
                            weightStr = weightStr.substring(0, weightStr.indexOf(".") + 3);
                        }
                    }
                    docsListStr.append(docNo + " " + docsList.get(docNo) + " " + weightStr + ";");*/
                    docsListStr.append(docNo + ": " + docsList.get(docNo) + ";");
                }
                String currIdfStr = Double.toString(currIdf);
                if (currIdfStr.contains(".")){
                    if(currIdfStr.indexOf(".") + 3 < currIdfStr.length()) {
                        currIdfStr = currIdfStr.substring(0, currIdfStr.indexOf(".") + 3);
                    }
                }
                listPosting.set(pointer -1, linePosting.substring(0, linePosting.indexOf("[")) + docsListStr + "[" + currIdfStr + "]");
                dictionary.replace(termStr,termInfo,newTermInfo);
            }
        }


        strPosting = new StringBuilder();
        for (String postingRec : listPosting) {
            strPosting.append(postingRec + "\n");
        }

        listPosting.clear();
        listChunk.clear();

        // create file writer
        FileWriter fwPosting = null;
        try {
            fwPosting = new FileWriter(new File(path + postingDir + "\\posting_" + chunk + ".txt"));
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



    public String classifyToPosting (String term){
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
        else if ((firstLetter >= '0' && firstLetter <= '9') || (firstLetter == '-' && isNumeric(term.substring(1)))){
            return "numbers";
        }
        else{
            return "other";
        }

    }

    private boolean isNumeric(String str)
    {
        try {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    public void writeDocsInfoToDisk (StringBuilder sb){
        String docsPath;
        if (!withStemming){
            docsPath = "\\docsInformation";
        }
        else{
            docsPath = "\\docsInformation_stemming";
        }
        File docsInformation = new File(path + docsPath + ".txt");
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
        StringBuilder sbShowDic = new StringBuilder();
        //sort the dictionary by keys (terms)
        ArrayList <String> strList = new ArrayList<>();
        for (String termStr: dictionary.keySet()) {
            strList.add(termStr);
        }
        Collections.sort(strList);

        for (String termStr : strList) {
            if (termStr.length() == 0){
                break;
            }
      //      if (dictionary.get(termStr).get(2) > 1) {
                sbShowDic.append(termStr + " : " + dictionary.get(termStr).get(2) + "\n");
                sb.append(termStr + " : " + dictionary.get(termStr).get(2) + " , " + dictionary.get(termStr).get(1) + " , " + classifyToPosting(termStr) + "_" + dictionary.get(termStr).get(0) + "\n");
      //      }
        }

        String dicPath;
        if (!withStemming){
            dicPath = "\\indexResults\\dictionary";
        }
        else{
            dicPath = "\\indexResults\\dictionary_stemming";
        }
        File dictionary = new File(path + dicPath + ".txt");
        File dictionaryShow = new File(path + dicPath + "Show.txt");
        try {
            dictionary.createNewFile();
            dictionaryShow.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter fw = null;
        FileWriter fwShow = null;
        try {
            fw = new FileWriter(dictionary);
            fwShow = new FileWriter(dictionaryShow);
            fw.write(sb.toString());
            fwShow.write(sbShowDic.toString());
            fw.close();
            fwShow.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finished(HashMap<String , Term> terms, int docsInCollection, boolean withStemming) {
        index(terms, docsInCollection, withStemming);
        writeDictionaryToDisk();
    }


    public void reset() {
        dictionary.clear();
    }

    public void loadDictionary(File newDic) {
        dictionary.clear();
        BufferedReader br = null;
        try {
            String term;
            ArrayList<Integer> termInfo = new ArrayList<>();
            br = new BufferedReader(new FileReader(newDic));
            String line = "";
            while ((line = (br.readLine())) != null) {
                termInfo = new ArrayList<>();
                term = line.substring(0, line.indexOf(':') - 1);
                String values = line.substring(line.indexOf(':') + 2);
                String[] valuesArr = values.split(",");
                try {
                    String v1 = valuesArr[0].substring(0, valuesArr[0].length() - 1);
                    termInfo.add(Integer.parseInt(v1));
                    String v2 = valuesArr[1].substring(1, valuesArr[1].length() - 1);
                    termInfo.add(Integer.parseInt(v2));
                    String v3 = valuesArr[2].substring(valuesArr[2].indexOf('_') + 1);
                    termInfo.add(Integer.parseInt(v3));
                    dictionary.put(term, termInfo);
                } catch (NumberFormatException e){
                  //  System.out.println(valuesArr[0] + "," + valuesArr[1] + "," + valuesArr[2]);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getDicSize() {
        return dictionary.size();
    }

    public HashMap<String, ArrayList<Integer>> getDictionary() {
        return dictionary;
    }

    public String getPostingDir() {
        return postingDir;
    }
}
