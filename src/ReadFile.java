import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ReadFile {

    private ArrayList<File> files = new ArrayList<>();
    private ArrayList<Document> documents = new ArrayList<>();
    private Parse parse = new Parse();


    public void getFilesFromDir (String path){
        Path dirPath = Paths.get(path);
        try {
            DirectoryStream <Path> stream = Files.newDirectoryStream(dirPath);
            for (Path filePath:stream) {
                files.add(filePath.toFile());
                separateFileToDocs(filePath.toFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void separateFileToDocs (File file){
        InputStream fIO = null;
        try {
            fIO = new FileInputStream(file.getPath());
            BufferedReader buf = new BufferedReader(new InputStreamReader(fIO));
            try {
                String line = "";
                StringBuilder sb = new StringBuilder();

                while ((line = buf.readLine()) != null){





                    parse.parseDocText(new Document());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public String getTextFromDoc (Document doc){
        return "";
    }

    public void setDocContent (String docText){

    }
}
