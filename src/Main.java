import java.io.IOException;

public class Main {

    public static void main (String [] args) throws IOException {
        ReadFile rf = new ReadFile();
        try {
            rf.getFilesFromDir("../resources/corpus");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
