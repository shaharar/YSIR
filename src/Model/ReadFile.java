package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class ReadFile {

    private Parse parse;
  //  private HashSet<String> languages;

    public ReadFile(boolean withStemming, String path, String corpusPath) {
        parse = new Parse(withStemming, path, corpusPath);
//        languages = new HashSet<>();
    }

    public void getFilesFromDir (String path) throws IOException {
        File corpus = new File(path);
        File[] files = corpus.listFiles();
        corpus.delete();
        for (File file:files) {
            //System.out.println(file.getPath().toString());
          //  if (file.getPath().toString().equals(path + "\\stop_words.txt")){
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
                    String docWithTags = e.select("DOC").outerHtml();
                    String city = getCityByTag(e.outerHtml());
                 //   String language = getLanguageByTag(e.outerHtml());
                 //   languages.add(language);
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
        String city = "";
        String [] tempStr;
        String [] lines = str.split("\n");
        for (int i = 0; i < lines.length; i++){
            if (lines[i].equals(" <f p=\"104\">")){
                tempStr = lines[i + 1].split(" ");
                return tempStr[3];
            }
        }
        return "";
    }

    private String getLanguageByTag(String str) {
        String language = "";
        String [] tempStr;
        String [] lines = str.split("\n");
        for (int i = 0; i < lines.length; i++){
            if (lines[i].equals(" <f p=\"105\">")){
                tempStr = lines[i + 1].split(" ");
                return tempStr[3];
            }
        }
        return "";
    }

    public void reset() {
        parse.reset();
   //     languages.clear();
    }

/*    public HashSet<String> getLanguages() {
        return languages;
    }*/
}

