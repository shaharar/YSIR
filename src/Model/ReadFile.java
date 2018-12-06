package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class ReadFile {

    private Parse parse;
    private HashSet<String> languages;

    public ReadFile(boolean withStemming, String path, String corpusPath) {
        parse = new Parse(withStemming, path, corpusPath);
        languages = new HashSet<>();
    }

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

    public void reset() {
        parse.reset();
        languages.clear();
    }

    public HashSet<String> getLanguages() {
        return languages;
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

    public int endOfRun() {
        return parse.getDocsInCollection();
    }

    public int getDicSize(){
        return parse.getDicSize();
    }
}

