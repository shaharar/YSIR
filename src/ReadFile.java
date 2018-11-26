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
    //private ArrayList<Document> documents = new ArrayList<>();
    private Parse parse;

    public ReadFile() {
        parse = new Parse();
    }

    public void getFilesFromDir (String path) throws IOException {

        File corpus = new File(path);
        File[] files = corpus.listFiles();
        for (File file:files) {
            //System.out.println(file.getPath().toString());
            separateFileToDocs(file);
        }
/*        Path dirPath = Paths.get(path);
        try {
            DirectoryStream <Path> stream = Files.newDirectoryStream(dirPath);
            for (Path filePath:stream) {
                files.add(filePath.toFile());
                separateFileToDocs(filePath.toFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        System.out.println("dicSize: " + parse.terms.size());
    }

    public void separateFileToDocs (File file){

        docsInFile = file.listFiles();
        for (File d : docsInFile) {
            try {
                //org.jsoup.nodes.Document document = Jsoup.parse(new String(Files.readAllBytes(file.toPath())));
                org.jsoup.nodes.Document document = Jsoup.parse(d, "UTF-8");
                org.jsoup.select.Elements elements = document.getElementsByTag("DOC");
                for (Element e: elements) {
                    String docText = e.select("TEXT").text();
                    String docNo = e.select("DOCNO").text();
                    Document doc = new Document();
                    //doc.setText(docText);
                    doc.setDocNo(docNo);
                    doc.setContent(e.toString());
                    parse.parseDocText(docText);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


/*        try {
            org.jsoup.nodes.Document document = Jsoup.parse(new String(Files.readAllBytes(file.toPath())));
            org.jsoup.select.Elements elements = document.getElementsByTag("DOC");
            for (org.jsoup.nodes.Element e: elements) {
                String docText = e.select("TEXT").text();
                Document doc = new Document();
                doc.setText(docText);
                doc.setContent(e.toString());
                parse.parseDocText(doc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }


    public static void main (String [] args) throws IOException {
        ReadFile rf = new ReadFile();
        rf.getFilesFromDir("d:\\documents\\users\\shaharar\\Downloads\\corpus\\corpus");
        System.out.println(Parse.docsTotal);

    }
}

