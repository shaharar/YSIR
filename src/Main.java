import java.io.IOException;

public class Main {

    public static void main (String [] args) throws IOException {
        ReadFile rf = new ReadFile();
        rf.getFilesFromDir("../resources/corpus");
    }
}
