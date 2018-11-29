import java.io.IOException;

public class Main {

    public static void main (String [] args) throws IOException {
        ReadFile rf = new ReadFile(true);
        try {
            rf.getFilesFromDir("d:\\documents\\users\\shaharar\\Downloads\\corpus\\corpus");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
