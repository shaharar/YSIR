import java.io.IOException;

public class Main {

    public static void main (String [] args) throws IOException {
        ReadFile rf = new ReadFile(false, "C:\\Users\\yardenhalili\\Documents\\test");
        try {
            rf.getFilesFromDir("C:\\Users\\yardenhalili\\Documents\\corpus");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
