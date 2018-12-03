package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;

public class ReadFile {

    private Parse parse;

    public ReadFile(boolean withStemming, String path, String corpusPath) {
        parse = new Parse(withStemming, path, corpusPath);
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

    private void separateFileToDocs (File file){

        File [] docsInFile = file.listFiles();
        for (File d : docsInFile) {
            try {
                org.jsoup.nodes.Document document = Jsoup.parse(d, "UTF-8");
                org.jsoup.select.Elements elements = document.getElementsByTag("DOC");
                for (Element e: elements) {
                    String docWithTags = e.select("DOC").outerHtml();
                    String city = "";
//                    int cityIdx = docWithTags.indexOf("<F P=104>");
//                    if (cityIdx != -1){
//                        String subByCity = docWithTags.substring(cityIdx + 8);
//                        city = subByCity.substring(0, subByCity.indexOf(' '));
//                    }
                    String [] cityTag = e.outerHtml().split("<f P=\"104\">");
                    String [] temspStr;
                    if (cityTag.length > 1){
                        temspStr = cityTag[1].split("</f>");
                        city = temspStr[0];
                        String [] firstStr = city.split(" ");
                        city = firstStr[0];
                    }
                    else{
                        city = cityTag[0];
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

    public void reset() {
        parse.reset();
//        parse = new Parse(false,"","");
    }
}

