package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class ReadFile {

    private Parse parse;
    private HashSet<String> languages;

    /**
     * constructor
     * @param withStemming
     * @param path
     * @param corpusPath
     */
    public ReadFile(boolean withStemming, String path, String corpusPath) {
        parse = new Parse(withStemming, path, corpusPath);
        languages = new HashSet<>();
    }

    /**
     * the following function reads files from a directory in the given path
     * @param path
     * @throws IOException
     */
    public void getFilesFromDir (String path) throws IOException {
        File corpus = new File(path);
        File[] files = corpus.listFiles();
        corpus.delete();
        for (File file:files) {
            if (! file.isDirectory()){ // to pass the stop words file
                continue;
            }
            separateFileToDocs(file);
        }
        parse.finished();
    }

    /**
     * the following function separates file to docs by labels
     * @param file
     */
    private void separateFileToDocs(File file){
        File [] docsInFile = file.listFiles();
        for (File d : docsInFile) {
            try {
                org.jsoup.nodes.Document document = Jsoup.parse(d, "UTF-8");
                org.jsoup.select.Elements elements = document.getElementsByTag("DOC");
                for (Element e: elements) {
                    String city = getCityByTag(e.outerHtml());
                    String language = getLanguageByTag(e.outerHtml());
                    if (language.length() > 1 && (language.charAt(language.length()-1) == '.' || language.charAt(language.length()-1) == ',' || language.charAt(language.length()-1) == ';' || language.charAt(language.length()-1) == '-' || language.charAt(language.length()-1) == '3')){
                        language = language.substring(0, language.length() - 1);
                    }
                    if (language.length() > 0 && language.charAt(0) == ' '){
                        language = language.substring(1);
                    }
                    if (!isNumeric(language)) {
                        languages.add(language);
                    }
                    String docText = e.select("TEXT").text();
                    String docNo = e.select("DOCNO").text();
                    parse.parseDocText(docText, docNo, city);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * the following function reads city by tag <fp=104></fp=104>
     * @param str
     * @return
     */
    private String getCityByTag(String str) {
        String tempStr;
        String [] lines = str.split("\n");
        for (int i = 0; i < lines.length; i++){
            if (lines[i].equals(" <f p=\"104\">") || lines[i].equals("  <f p=\"104\">") || lines[i].equals("   <f p=\"104\">") || lines[i].equals("<f p=\"104\">")){
                tempStr = lines[i + 1];
                int j = 0;
                while (j < tempStr.length() && tempStr.charAt(j) == ' '){
                    j++;
                }
                int k = j;
                while (k < tempStr.length() && tempStr.charAt(k) != ' ')
                    k++;
                return tempStr.substring(j,k);
            }
        }
        return "";
    }

    /**
     * the following function reads language by tag <fp=105></fp=105>
     * @param str
     * @return
     */
    private String getLanguageByTag(String str) {
        String [] tempStr;
        String [] lines = str.split("\n");
        for (int i = 0; i < lines.length; i++){
            if (lines[i].equals(" <f p=\"105\">")){
                tempStr = lines[i + 1].split(" ");
                if (tempStr[3].equals("")){
                    if (!tempStr[4].equals("")) {
                        return tempStr[4];
                    }
                }
                return tempStr[3];
            }
        }
        return "";
    }

    /**
     * the following function resets program's memory
     */
    public void reset() {
        parse.reset();
        languages.clear();
    }

    /**
     * getter
     * @return
     */
    public HashSet<String> getLanguages() {
        return languages;
    }

    /**
     * the following function returns whether a string represents a numeric value
     * @param str
     * @return
     */
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

    public int endOfRun() {
        return parse.getDocsInCollection();
    }

    /**
     * getter
     * @return
     */
    public int getDicSize() {
        return parse.getDicSize();
    }

    /**
     * getter
     * @return
     */
    public int getDocsInCollection(){
        return parse.getDocsInCollection();
    }

    /**
     * getter
     * @return
     */
    public Indexer getIndexer (){
       return parse.getIndexer();
    }

    /**
     * getter
     * @return
     */
    public CityIndexer getCityIndexer (){
        return parse.getCityIndexer();
    }

    /**
     * the following function writes languages file to disk
     * @param savePath
     */
    public void writeLanguagesToDisk (String savePath){
        StringBuilder sb = new StringBuilder();
        for (String lang : languages) {
            sb.append(lang + "\n");
        }
        File languagesFile = new File(savePath + "\\languages.txt");
        try {
            languagesFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(languagesFile);
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

