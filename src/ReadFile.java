import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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
    }

    public void separateFileToDocs (File file){

        docsInFile = file.listFiles();
        for (File d : docsInFile) {
            try {
                org.jsoup.nodes.Document document = Jsoup.parse(new String(Files.readAllBytes(file.toPath())));
                org.jsoup.select.Elements elements = document.getElementsByTag("DOC");
                for (Element e: elements) {
                    String docText = e.select("TEXT").text();
                    Document doc = new Document();
                    doc.setText(docText);
                    doc.setContent(e.toString());
                    parse.parseDocText(doc);
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

 /*       InputStream fIO = null;
        try {
            fIO = new FileInputStream(file.getPath());
            BufferedReader buf = new BufferedReader(new InputStreamReader(fIO));
            try {
                String line = "";
                String[] splitLine;
                StringBuilder sb = new StringBuilder();


                String word = "";
                String docContent = "";
                String docText = "";
                while ((line = buf.readLine()) != null){
                    splitLine = line.split(" ");
*//*                    for (String word:splitLine) {
                        if (word.equals("<Doc>")) {
                            String docContent = word;
                            while (!word.equals("<Doc>")){
                                docContent += word;
                            }
                        }
                    }*//*
                    int i = 0;
                    while (i < splitLine.length){
                        if(splitLine[i].equals("<Doc>")){
                            docContent = splitLine[i];
                            i++;
                            while (! splitLine[i].equals("<Doc>")){
                                docContent += splitLine[i];
                                if(splitLine[i].equals("<Text>")){
                                    docText = splitLine[i];
                                    i++;
                                    while (! splitLine[i].equals("<Text>")) {
                                        docText += splitLine[i];
                                        docContent += splitLine[i];
                                        i++;
                                    }
                                }
                                i++;
                            }
                        }
                        i++;
                    }
                    parse.parseDocText(new Document());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

    }
    public static void main (String [] args) throws IOException {
        ReadFile rf = new ReadFile();
        rf.getFilesFromDir("resources/corpus.corpus");
    }
}

