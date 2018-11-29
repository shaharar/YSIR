import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReadFile {

    private File[] docsInFile;
    private Parse parse;

    public ReadFile(boolean withStemming, String path) {
        parse = new Parse(withStemming, path);
    }

    public void getFilesFromDir (String path) throws IOException {

        File corpus = new File(path);
        File[] files = corpus.listFiles();
        for (File file:files) {
            //System.out.println(file.getPath().toString());
            separateFileToDocs(file);
        }
        // create docs information file
        parse.finished();
    }

    public void separateFileToDocs (File file){

        docsInFile = file.listFiles();
        for (File d : docsInFile) {
            try {
                org.jsoup.nodes.Document document = Jsoup.parse(d, "UTF-8");
                org.jsoup.select.Elements elements = document.getElementsByTag("DOC");
                for (Element e: elements) {
                    String docWithTags = e.select("DOC").outerHtml();
                    String city = "";
                    int cityIdx = docWithTags.indexOf("<F P=104>");
                    if (cityIdx != -1){
                        String subByCity = docWithTags.substring(cityIdx + 8);
                        city = subByCity.substring(0, subByCity.indexOf(' '));
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


    public static void main (String [] args) throws IOException {
/*
        ReadFile rf = new ReadFile();
        rf.getFilesFromDir("d:\\documents\\users\\shaharar\\Downloads\\corpus\\corpus");
        System.out.println(Parse.docsTotal);
*/

    }
}

