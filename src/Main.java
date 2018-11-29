import java.io.IOException;

public class Main {

    public static void main (String [] args) throws IOException {
        ReadFile rf = new ReadFile(false, "D:\\documents\\users\\haliliya\\Downloads\\test");
        try {
            rf.getFilesFromDir("D:\\documents\\users\\haliliya\\Downloads\\corpus\\corpus");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
