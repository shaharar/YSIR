import java.io.IOException;

public class Main {

    public static void main (String [] args) throws IOException {
        ReadFile rf = new ReadFile(false, "d:\\documents\\users\\haliliya\\Downloads\\test");
        try {
            rf.getFilesFromDir("d:\\documents\\users\\haliliya\\Downloads\\corpus\\corpus");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
